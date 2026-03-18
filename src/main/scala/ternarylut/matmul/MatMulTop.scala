// Copyright 2026 KU Leuven.
// Solderpad Hardware License, Version 0.51, see LICENSE for details.
// SPDX-License-Identifier: SHL-0.51
// Author: Joran Heldens

package ternarylut.matmul

import chisel3._
import chisel3.util._

import ternarylut.lutgen.LUTGenUtil

/** Top-level module for integer activations.
  *
  * Connects MatMulController to MatMulDatapathSplitted and exposes counter outputs so the testbench can feed the
  * correct activations and keys each cycle.
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
  *   parallel LUTs per cycle L (0 = all = N_key)
  * @param nbKeys
  *   parallel key fetches per LUT K (0 = all = M_key)
  */
class MatMulTop(N_key: Int, M_key: Int, groupSize: Int = 3, bitWidth: Int = 16, nbLUTS: Int = 0, nbKeys: Int = 1)
    extends Module
    with RequireAsyncReset {
  require(groupSize >= 1, "Group size must be at least 1")
  require(N_key > 0)
  require(M_key > 0)
  require(bitWidth > 0)

  val nbLuts          = if (nbLUTS <= 0 || nbLUTS > N_key) N_key else nbLUTS
  val nbKeysEffective = if (nbKeys <= 0 || nbKeys > M_key) M_key else nbKeys
  val outNbBits       = 32

  val keyBits = LUTGenUtil.keyBitsWidth(groupSize)

  val nbBitsNCnt   = math.max(1, log2Ceil(N_key))
  val nbBitsMCnt   = math.max(1, log2Ceil(M_key))
  val nbBitsActCnt = math.max(1, log2Ceil(N_key * groupSize))

  val io = IO(new Bundle {
    val start     = Input(Bool())
    val done      = Output(Bool())
    val key_N_cnt = Output(UInt(nbBitsNCnt.W))
    val key_M_cnt = Output(UInt(nbBitsMCnt.W))
    val act_cnt   = Output(UInt(nbBitsActCnt.W))

    val input_activations = Input(Vec(nbLuts, Vec(groupSize, SInt(bitWidth.W))))
    val input_key         = Input(Vec(nbLuts, Vec(nbKeysEffective, UInt(keyBits.W))))
    val out               = Output(Vec(M_key, SInt(outNbBits.W)))

    val input_enable_test = Output(Vec((M_key.toFloat / nbKeysEffective.toFloat).ceil.toInt, Bool()))
  })

  val controller = Module(new MatMulController(N_key, M_key, groupSize, bitWidth, nbLUTS, nbKeys))
  val datapath   = Module(new MatMulDatapathSplitted(N_key, M_key, groupSize, bitWidth, nbLUTS, nbKeys))

  datapath.io.input_enable      := controller.io.input_enable
  datapath.io.input_activations := io.input_activations
  datapath.io.input_key         := io.input_key
  io.input_enable_test          := datapath.io.input_enable

  controller.io.start := io.start
  io.done             := controller.io.done
  io.key_N_cnt        := controller.io.key_N_cnt
  io.key_M_cnt        := controller.io.key_M_cnt
  io.act_cnt          := controller.io.act_cnt

  io.out := datapath.io.out
}
