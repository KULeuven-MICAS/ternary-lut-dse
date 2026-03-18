// Copyright 2026 KU Leuven.
// Solderpad Hardware License, Version 0.51, see LICENSE for details.
// SPDX-License-Identifier: SHL-0.51
// Author: Joran Heldens

package ternarylut.matmul

import scala.annotation.unused

import chisel3._
import chisel3.util._

import ternarylut.lutgen.LUTGenCombV1MuAll
import ternarylut.lutgen.LUTGenUtil

/** Datapath for integer activations (paper Figure 3).
  *
  * Instantiates L parallel LUTs (LUTGenCombV1MuAll), each with K fetch MUXes and sign-inversion logic. Partial results
  * from all LUTs are reduced and accumulated into the output registers via RegEnable gates.
  *
  * @param N_key
  *   total weight groups in the n-dimension (= total_n / µ)
  * @param M_key
  *   total output columns
  * @param groupSize
  *   LUT group size µ
  * @param bitWidth
  *   activation bit width
  * @param nbLUTS
  *   parallel LUTs L (0 = all = N_key)
  * @param nbKeys
  *   parallel key fetches per LUT K (0 = all = M_key)
  * @param _LUTGen_version
  *   unused; kept for API compatibility
  */
class MatMulDatapathSplitted(
  N_key:                   Int,
  M_key:                   Int,
  groupSize:               Int = 3,
  bitWidth:                Int = 16,
  nbLUTS:                  Int = 0,
  nbKeys:                  Int = 1,
  @unused _LUTGen_version: Int = 1
) extends Module
    with RequireAsyncReset {

  val nbLuts         = if (nbLUTS == 0) N_key else nbLUTS
  val requiredCycles = (N_key.toFloat / nbLuts.toFloat).ceil.toInt
  val luts           = (0 until nbLuts).map(_ => Module(new LUTGenCombV1MuAll(groupSize, bitWidth)))
  val outNbBits      = 32
  val keyBits        = LUTGenUtil.keyBitsWidth(groupSize)
  val nbEnableInputs = (M_key.toFloat / nbKeys.toFloat).ceil.toInt
  val io             = IO(new Bundle {
    val input_activations = Input(Vec(nbLuts, Vec(groupSize, SInt(bitWidth.W))))
    val input_key         = Input(Vec(nbLuts, Vec(nbKeys, UInt(keyBits.W))))
    val input_enable      = Input(Vec(nbEnableInputs, Bool()))
    val out               = Output(Vec(M_key, SInt((outNbBits).W)))
  })

  val fetchedEntries = Wire(Vec(nbLuts, Vec(nbKeys, SInt((outNbBits).W))))
  val partialResults = Wire(Vec(nbLuts, Vec(nbKeys, SInt((outNbBits).W))))
  val fetchMuxes     =
    Seq.fill(nbLuts)(Seq.fill(nbKeys)(Module(new MatMulMuxLut(bitWidth, groupSize, outNbBits, keyBits))))
  val invertMuxes    =
    Seq.fill(nbLuts)(Seq.fill(nbKeys)(Module(new MatMulMuxInverse(outNbBits))))
  for (i <- 0 until nbLuts) {
    luts(i).io.in := io.input_activations(i)
    for (j <- 0 until nbKeys) {
      fetchMuxes(i)(j).io.key     := io.input_key(i)(j)(keyBits - 2, 0)
      fetchMuxes(i)(j).io.in      := luts(i).io.out
      fetchedEntries(i)(j)        := fetchMuxes(i)(j).io.selected_entry
      invertMuxes(i)(j).io.in     := fetchedEntries(i)(j)
      invertMuxes(i)(j).io.invert := io.input_key(i)(j)(keyBits - 1)
      partialResults(i)(j)        := invertMuxes(i)(j).io.out
    }
  }
  if (requiredCycles == 1) {
    var keyIdx    = 0
    var enableIdx = 0
    for (i <- 0 until M_key) {
      val reg = RegEnable(partialResults.map { x => x(keyIdx) }.reduce(_ +& _), 0.S, io.input_enable(enableIdx))
      io.out(i) := reg

      keyIdx += 1
      if (keyIdx >= nbKeys) {
        keyIdx = 0
        enableIdx += 1
      }
    }
  } else {
    var keyIdx    = 0
    var enableIdx = 0
    for (i <- 0 until M_key) {
      val reg =
        RegEnable(partialResults.map { x => x(keyIdx) }.reduce(_ +& _) + io.out(i), 0.S, io.input_enable(enableIdx))
      io.out(i) := reg

      keyIdx += 1
      if (keyIdx >= nbKeys) {
        keyIdx = 0
        enableIdx += 1
      }
    }
  }
}
