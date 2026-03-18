// Copyright 2026 KU Leuven.
// Solderpad Hardware License, Version 0.51, see LICENSE for details.
// SPDX-License-Identifier: SHL-0.51
// Author: Joran Heldens
//
// Top-Level Accelerator Integration Test — Floating-Point Activations
//
// End-to-end functional test for the MatMulTopFP module (FP datapath).
// Similar to TopTesterINT but additionally handles:
//   - FP-specific pipeline latency alignment (LUT build + post-adder chain)
//   - Ramp-down phase: after all activations are applied, the pipelined LUTs
//     need extra cycles to flush results through the pipeline before the
//     controller signals done.
//   - Bit-level accuracy checking (0-bit, 1-bit, 2-bit error margins) since
//     FP rounding may cause minor deviations from the Scala golden model.

package ternarylut.matmul

import scala.math.max

import chisel3._

import chiseltest._
import fp_unit._
import org.scalatest.flatspec.AnyFlatSpec

/** Helpers for active LUT/key counts at tile boundaries. */
trait TopTesterUtils {
  def getCurLuts(nbLuts: Int, nKey: Int, groupSize: Int, startIdxAct: Int): Int =
    if (startIdxAct + (groupSize * nbLuts) > (nKey * groupSize))
      ((nKey * groupSize) - startIdxAct) / groupSize
    else
      nbLuts

  def getCurKeys(nbKeys: Int, M: Int, startIdxKey: Int): Int =
    if (startIdxKey + nbKeys > M) (M - startIdxKey) else nbKeys

}

class TopTesterFP extends AnyFlatSpec with ChiselScalatestTester with TopTesterUtils with MatmulUtil with FpUtils {

  // ---- Test Configuration ----
  val verbose     = false
  val nbCycles    = 10 // Number of tile passes in the n-dimension
  val groupSize   = 2  // LUT group size µ
  val fpType      = FP16
  val n           = 8  // Tile size in n-dimension (= L · µ)
  val nbKeys      = 8  // Tile size in m-dimension (= K, parallel fetchers)
  val nbRegisters = 2  // Pipeline register stages per FP adder (0 = combinational)

  val nbLuts = (n.toFloat / groupSize.toFloat).ceil.toInt
  val N      = n * nbCycles // Workload N-dim
  val M      = nbKeys       // Workload M-dim
  val nKey   = (N.toFloat / groupSize.toFloat).ceil.toInt
  val mKey   = M

  if (verbose) {
    println(
      s"TopTesterFP: groupSize=$groupSize, matrix ${N}x$M, key ${nKey}x$mKey, tile ${nbLuts * groupSize}x$nbKeys, nbRegs=$nbRegisters"
    )
  }

  s"MatMulTopFP_OS_FP${fpType.width}_${nbLuts * groupSize}x${nbKeys}___${N}x${M}_gs_${groupSize}" should "work" in {
    test(new MatMulTopFP(nKey, mKey, groupSize, fpType, nbLuts, nbKeys, nbRegisters))
      .withAnnotations(Seq(VerilatorBackendAnnotation, WriteVcdAnnotation)) { c =>
        // generate a random ternary matrix
        val (ternaryMatrix, keyMatrix) = generateKeyMatrix(groupSize, N, M, printDebug = verbose)

        // generate a random floating point activation vector
        val activationVector                                 = Array.fill(N) {
          genRandomValue(fpType)
        }
        // calculate the expected output floating point activation vector
        val (activationVectorPaddedFloatRet, expectedOutput) =
          goldenMatMulFP(activationVector, ternaryMatrix, verbose, fpType)
        val activationVectorPaddedFloat                      =
          0f +: activationVectorPaddedFloatRet // add the zero value at the start of the vector
        val activationVectorPaddedInt = activationVectorPaddedFloat.map(value => floatToUInt(fpType, value))
        val lutDelay                  = max(1, groupSize - 1) * nbRegisters

        if (verbose) println(s"Activation input FP (padded): ${activationVectorPaddedFloat.mkString(", ")}")

        // Start the controller
        c.io.start.poke(1.B)
        c.clock.step(1)
        c.io.start.poke(0.B)

        var exactly_correct    = 0
        var exactly_correct_1b = 0
        var exactly_correct_2b = 0

        var cycleCounter     = 0
        var rampDownCounter  = 0
        var nbOutputsWritten = 0
        var curNbKeys        = nbKeys
        var curNbLuts        = nbLuts
        val keyZeroIdx       = ((math.pow(3, groupSize).toInt - 1) / 2) // key to apply on mux which fetches the value 0
        val maxRampDownCycles = nbLuts - 1
        val normalApplyCycles = (nKey.toFloat / nbLuts.toFloat).ceil.toInt

        while (c.io.done.peek().litValue == 0) {

          // determine over how many LUTs and nbKeys you should apply new values
          // normally these are equal to nbKeys and LUTs, unless the dimension M is not a multiple of nbKeys
          // or the dimension N_key is not a multiple of nbLUTS

          if (nbRegisters == 0) {
            curNbLuts = getCurLuts(nbLuts, nKey, groupSize, c.io.act_cnt.peek().litValue.toInt)
            curNbKeys = getCurKeys(nbKeys, M, c.io.key_M_cnt.peek().litValue.toInt)
          } else {
            curNbLuts = nbLuts
            curNbKeys = nbKeys
          }

          // Apply input activations (offset by controller's act_cnt)
          if (rampDownCounter < maxRampDownCycles) {
            for (i <- 0 until curNbLuts) {
              for (j <- 0 until groupSize) {
                val actIdx          = j + c.io.act_cnt.peek().litValue.toInt
                val absActIdxPadded = actIdx + 1 // +1 for leading zero in padded vector
                val delayCorrection = i * (nbLuts - 1) * groupSize
                val corrAbsActIdx   = max(0, absActIdxPadded - delayCorrection)
                val absActIdx       = if (corrAbsActIdx >= activationVectorPaddedInt.length) 0 else corrAbsActIdx
                c.io.input_activations(i)(j).poke(activationVectorPaddedInt(absActIdx).U)
              }
            }
          }

          // Apply key values (offset by key_N_cnt, key_M_cnt; with delay correction for pipelining)
          if (cycleCounter >= lutDelay && rampDownCounter < maxRampDownCycles + lutDelay) {
            for (i <- 0 until curNbLuts) {
              for (j <- 0 until curNbKeys) {
                val nIdx            = c.io.key_N_cnt.peek().litValue.toInt
                val delayCorrection = i * (nbLuts - 1)
                val absNIdx         = nIdx - delayCorrection
                val absMIdx         = j + c.io.key_M_cnt.peek().litValue.toInt
                if (absNIdx < 0 || absNIdx >= nKey) {
                  c.io.input_key(i)(j).poke(keyZeroIdx.U)
                } else {
                  c.io.input_key(i)(j).poke(keyMatrix(absNIdx)(absMIdx))
                }
              }
            }
            for {
              i    <- curNbLuts until nbLuts
              j    <- 0 until curNbKeys
            } {
              c.io.input_key(i)(j).poke(keyZeroIdx.U)
            }
          }

          if (c.io.act_cnt.peek().litValue >= N) rampDownCounter += 1

          if (rampDownCounter >= maxRampDownCycles) c.io.ramp_down_act_finished.poke(1.B)
          else c.io.ramp_down_act_finished.poke(0.B)

          if (rampDownCounter >= (maxRampDownCycles + lutDelay)) c.io.ramp_down_key_finished.poke(1.B)
          else c.io.ramp_down_key_finished.poke(0.B)

          if (c.io.input_enable_test.peek().litValue == 1) nbOutputsWritten += 1

          if (nbOutputsWritten >= normalApplyCycles) c.io.all_outputs_received.poke(1.B)
          else c.io.all_outputs_received.poke(0.B)

          if (verbose) {
            print("Output activations: ")
            for (k <- 0 until mKey) print(s"${uintToFloat(fpType, c.io.out(k).peek().litValue)} ")
            println(s" [cycle $cycleCounter]")
          }

          cycleCounter += 1
          c.clock.step(1)
          if (verbose && cycleCounter % 100 == 0) println(s"Cycle: $cycleCounter")
        }

        // check if the done signal is high
        c.io.done.expect(1.B)

        // Check output activations (count 0-, 1-, 2-bit differences for FP rounding)
        for (i <- 0 until mKey) {
          val expectedBits  = uintToStr(floatToUInt(fpType, expectedOutput(i)), fpType)
          val actualBits    = uintToStr(c.io.out(i).peek().litValue, fpType)
          val differingBits = expectedBits.zip(actualBits).count { case (e, a) => e != a }
          if (differingBits == 0) {
            exactly_correct += 1
            exactly_correct_1b += 1
            exactly_correct_2b += 1
          } else if (differingBits == 1) {
            exactly_correct_1b += 1
            exactly_correct_2b += 1
          } else if (differingBits == 2) {
            exactly_correct_2b += 1
          }
          if (verbose && differingBits > 0) {
            println(
              s"Output $i: ${differingBits} bit(s) diff (expected ${expectedOutput(i)}, got ${uintToFloat(fpType, c.io.out(i).peek().litValue)})"
            )
          }
        }

        if (verbose) {
          println(
            s"Final output: ${(0 until mKey).map(i => uintToFloat(fpType, c.io.out(i).peek().litValue)).mkString(", ")}"
          )
          println(s"Expected:     ${expectedOutput.mkString(", ")}")
        }
        println(
          f"Test done in $cycleCounter cycles — 0-bit: ${exactly_correct.toFloat / mKey * 100}%.1f%%, 1-bit: ${exactly_correct_1b.toFloat / mKey * 100}%.1f%%, 2-bit: ${exactly_correct_2b.toFloat / mKey * 100}%.1f%%"
        )
      }
  }
}
