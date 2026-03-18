// Copyright 2026 KU Leuven.
// Solderpad Hardware License, Version 0.51, see LICENSE for details.
// SPDX-License-Identifier: SHL-0.51
// Author: Joran Heldens

package ternarylut.matmul

import scala.annotation.unused

import chisel3._
import chisel3.util._

import fp_unit._
import ternarylut.lutgen.LUTGenPipelinedV1FP
import ternarylut.lutgen.LUTGenUtil

/** Datapath for floating-point activations (paper Figure 3).
  *
  * Same architecture as MatMulDatapathSplitted but uses LUTGenPipelinedV1FP and FpAddFp for the post-accumulation
  * chain. ShiftRegisters are inserted throughout to align data with the FP adder pipeline latency.
  *
  * @param N_key
  *   total weight groups in the n-dimension
  * @param M_key
  *   total output columns
  * @param groupSize
  *   LUT group size µ
  * @param fpType
  *   floating-point format (FP16, BF16, FP32, etc.)
  * @param nbLUTS
  *   parallel LUTs L (0 = all = N_key)
  * @param nbKeys
  *   parallel key fetches per LUT K (0 = all = M_key)
  * @param _LUTGen_version
  *   unused; kept for API compatibility
  * @param nbRegisters
  *   pipeline register stages per FP adder (0 = combinational)
  */
class MatMulDatapathSplittedFP(
  N_key:                   Int,
  M_key:                   Int,
  groupSize:               Int    = 3,
  fpType:                  FpType = FP16,
  nbLUTS:                  Int    = 0,
  nbKeys:                  Int    = 1,
  @unused _LUTGen_version: Int    = 1,
  nbRegisters:             Int    = 0
) extends Module
    with RequireAsyncReset {

  val nbLuts         = if (nbLUTS == 0) N_key else nbLUTS
  val requiredCycles = (N_key.toFloat / nbLuts.toFloat).ceil.toInt
  val luts           = (0 until nbLuts).map { _ =>
    Module(new LUTGenPipelinedV1FP(groupSize, fpType, nbRegisters))
  }

  val keyBits        = LUTGenUtil.keyBitsWidth(groupSize)
  val nbEnableInputs = (M_key.toFloat / nbKeys.toFloat).ceil.toInt
  val io             = IO(new Bundle {
    val input_activations = Input(Vec(nbLuts, Vec(groupSize, UInt((fpType.width).W))))
    val input_key         = Input(Vec(nbLuts, Vec(nbKeys, UInt(keyBits.W))))
    val input_enable      = Input(Vec(nbEnableInputs, Bool()))
    val out               = Output(Vec(M_key, UInt((fpType.width).W)))
  })

  val fetchedEntries = Wire(Vec(nbLuts, Vec(nbKeys, UInt((fpType.width).W))))
  val partialResults = Wire(Vec(nbLuts, Vec(nbKeys, UInt((fpType.width).W))))

  val fetchMuxes  = Seq.fill(nbLuts)(Seq.fill(nbKeys)(Module(new MatMulMuxLutFP(groupSize, fpType, keyBits))))
  val invertMuxes = Seq.fill(nbLuts)(Seq.fill(nbKeys)(Module(new MatMulMuxInverseFP(fpType))))

  for (i <- 0 until nbLuts) {
    luts(i).io.in := io.input_activations(i)

    for (j <- 0 until nbKeys) {
      fetchMuxes(i)(j).io.key := io.input_key(i)(j)(keyBits - 2, 0)
      fetchMuxes(i)(j).io.in  := luts(i).io.out
      fetchedEntries(i)(j)    := fetchMuxes(i)(j).io.selected_entry

      invertMuxes(i)(j).io.in     := fetchedEntries(i)(j)(fpType.width - 1)
      invertMuxes(i)(j).io.invert := io.input_key(i)(j)(keyBits - 1)
      partialResults(i)(j)        := ShiftRegister(
        Cat(invertMuxes(i)(j).io.out, fetchedEntries(i)(j)(fpType.width - 2, 0)),
        nbRegisters
      )
    }
  }

  val fpAdderPost =
    Seq.fill(nbLuts - 1)(Seq.fill(nbKeys)(Module(new FpAddFp(fpType, fpType, fpType))))

  val partialAccumulationChain = Wire(Vec((nbKeys), Vec((nbLuts - 1), UInt((fpType.width).W))))

  val L = nbLuts
  val K = nbKeys
  for (i <- 0 until K) {
    for (j <- 0 until (L - 1)) {
      fpAdderPost(j)(i).io.in_a      := partialResults(j + 1)(i)
      fpAdderPost(j)(i).io.in_b      := ShiftRegister(
        if (j == 0) partialResults(0)(i) else partialAccumulationChain(i)(j - 1),
        nbRegisters
      )
      partialAccumulationChain(i)(j) := fpAdderPost(j)(i).io.out
    }
  }

  if (requiredCycles == 1) {

    var keyIdx    = 0
    var enableIdx = 0
    for (i <- 0 until M_key) {
      if (nbLuts == 1) {
        val reg = RegEnable(partialResults(0)(keyIdx), 0.U, io.input_enable(enableIdx))
        io.out(i) := reg

      } else {
        val reg = RegEnable(partialAccumulationChain(keyIdx)(nbLuts - 2), 0.U, io.input_enable(enableIdx))
        io.out(i) := reg
      }

      keyIdx += 1
      if (keyIdx >= nbKeys) {
        keyIdx = 0
        enableIdx += 1
      }
    }
  } else {
    val fpAddersAcc = (0 until M_key).map(_ => Module(new FpAddFp(fpType, fpType, fpType)))

    var keyIdx    = 0
    var enableIdx = 0
    for (i <- 0 until M_key) {
      fpAddersAcc(i).io.in_a := io.out(i)
      fpAddersAcc(i).io.in_b := ShiftRegister(partialAccumulationChain(keyIdx)(nbLuts - 2), nbRegisters)
      val reg = RegEnable(fpAddersAcc(i).io.out, 0.U, io.input_enable(enableIdx))
      io.out(i) := reg

      keyIdx += 1
      if (keyIdx >= nbKeys) {
        keyIdx = 0
        enableIdx += 1
      }
    }
  }
}
