// Copyright 2026 KU Leuven.
// Solderpad Hardware License, Version 0.51, see LICENSE for details.
// SPDX-License-Identifier: SHL-0.51
// Author: Joran Heldens
//
// Top-Level Accelerator Integration Test — Integer Activations
//
// End-to-end functional test for the MatMulTop module (INT datapath).
// The test:
//   1. Generates a random ternary weight matrix and encodes it into keys.
//   2. Generates a random INT activation vector.
//   3. Computes the golden GEMV result in Scala.
//   4. Drives the DUT's controller FSM cycle-by-cycle, feeding activations
//      and encoded keys according to the controller's counter outputs.
//   5. After the done signal, compares DUT output against the golden model.
//
// The controller operates in multiple passes when the tile (nb_luts × nb_keys)
// is smaller than the full workload (N_key × M_key), accumulating partial
// results across cycles.

package ternarylut.matmul

import scala.math.pow

import chisel3._

import chiseltest._
import fp_unit._
import org.scalatest.flatspec.AnyFlatSpec

class TopTester extends AnyFlatSpec with ChiselScalatestTester with MatmulUtil with FpUtils {

  // ---- Test Configuration ----
  val verbose   = false
  val groupSize = 2      // LUT group size µ
  val nbBits    = 8      // Activation bit width (INT8)
  val N         = 10 * 4 // Total weight matrix rows (n-dimension)
  val M         = 8      // Total weight matrix columns (m-dimension)
  val n         = 4      // Tile size in n-dimension (= L · µ)
  val nKey      = (N.toFloat / groupSize.toFloat).ceil.toInt
  val mKey      = M
  val nbLuts    = {
    val raw = (n.toFloat / groupSize.toFloat).ceil.toInt
    if (raw <= 0 || raw > nKey) nKey else raw
  }
  val nbKeys    = {
    val requested = 8 // Tile size in m-dimension (= K, parallel fetchers)
    if (requested <= 0 || requested > mKey) mKey else requested
  }

  s"MatMulTop_OS_INT${nbBits}_${n}x${nbKeys}___${n}x${nbKeys}_gs_${groupSize}" should "work" in {
    test(new MatMulTop(nKey, mKey, groupSize, nbBits, nbLuts, nbKeys))
      .withAnnotations(Seq(VerilatorBackendAnnotation, WriteVcdAnnotation)) { c =>
        // generate a random ternary weight matrix and encode it into keys
        val (ternaryMatrix, keyMatrix) = generateKeyMatrix(groupSize, N, M, printDebug = verbose)

        // generate a random INT activation vector
        val activationVector = Array.fill(N) {
          scala.util.Random.nextInt((pow(2, nbBits - 1).toInt) * 2) - pow(2, nbBits - 1).toInt
        }

        // compute the golden GEMV result (activation vector is padded to a multiple of µ)
        val (activationVectorPaddedInt, expectedOutput) = goldenMatMulINT(activationVector, ternaryMatrix)

        var cycleCounter  = 0
        var prevCurNbLuts = 0
        var prevCurNbKeys = 0
        val keyZeroIdx    = ((math.pow(3, groupSize).toInt - 1) / 2)

        // Start the controller FSM
        c.io.start.poke(1.B)
        c.clock.step(1)
        c.io.start.poke(0.B)

        while (c.io.done.peek().litValue == 0) {

          // determine over how many LUTs and nbKeys you should apply new values
          // normally these are equal to nbKeys and LUTs, unless the dimension M is not a multiple of nbKeys
          // or the dimension N_key is not a multiple of nbLUTS

          // determine for LUTs
          val startIdxAct = c.io.act_cnt.peek().litValue.toInt
          val curNbLuts   =
            if (startIdxAct + (groupSize * nbLuts) > (nKey * groupSize))
              ((nKey * groupSize) - startIdxAct) / groupSize
            else nbLuts

          // determine for keys
          val startIdxKey = c.io.key_M_cnt.peek().litValue.toInt
          val curNbKeys   = if (startIdxKey + nbKeys > M) (M - startIdxKey) else nbKeys

          // apply the correct input activations based on the offset indication of the controller: act_cnt
          for (i <- 0 until curNbLuts) {
            for (j <- 0 until groupSize) {
              val absActIdx = i * groupSize + j + c.io.act_cnt.peek().litValue.toInt
              c.io.input_activations(i)(j).poke(activationVectorPaddedInt(absActIdx).S)
            }
          }

          if (cycleCounter != 0) {
            // apply the correct key values based on the controller's key_N_cnt and key_M_cnt offsets
            for (i <- 0 until prevCurNbLuts) {
              for (j <- 0 until curNbKeys) {
                val absNIdx = i + c.io.key_N_cnt.peek().litValue.toInt
                val absMIdx = j + c.io.key_M_cnt.peek().litValue.toInt
                c.io.input_key(i)(j).poke(keyMatrix(absNIdx)(absMIdx))
              }
            }

            // set the remaining keys to the zero idx if the remaining LUTs are not used
            for (i <- prevCurNbLuts until nbLuts) {
              for (j <- 0 until prevCurNbKeys) {
                c.io.input_key(i)(j).poke(keyZeroIdx.U)
              }
            }
          }

          prevCurNbLuts = curNbLuts
          prevCurNbKeys = curNbKeys

          cycleCounter += 1
          c.clock.step(1)

        }

        // check if the done signal is high
        c.io.done.expect(1.B)

        // check if the output activations are correct
        for (i <- 0 until mKey) {
          c.io.out(i).expect(expectedOutput(i).toInt.S)
        }

        // optionally print final vs expected output
        if (verbose) {
          println("Final Output activations: ")
          for (i <- 0 until mKey) print(s"${c.io.out(i).peek().litValue} ")

          println("\n")
          println("Expected output activations: ")
          for (i <- 0 until mKey) print(s"${expectedOutput(i)} ")

          println("\n")
          println(s"Test done in $cycleCounter cycle(s)")
        }
      }
  }
}
