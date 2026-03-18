// Copyright 2026 KU Leuven.
// Solderpad Hardware License, Version 0.51, see LICENSE for details.
// SPDX-License-Identifier: SHL-0.51
// Author: Joran Heldens

package ternarylut.matmul

import chisel3._
import chisel3.util._

import ternarylut.lutgen.LUTGenUtil

/** BlackBox wrapper around a pre-synthesised MatMul Verilog (integer activations).
  *
  * The underlying Verilog file must be placed at `generated/Top_1CC_128x128___1024x1024_gs_<groupSize>.v` and expose
  * the same port list.
  */
class TopMMBlackBox(N_key: Int, M_key: Int, groupSize: Int = 3, bitWidth: Int = 16, nbLUTS: Int = 0, nbKeys: Int = 1)
    extends BlackBox
    with HasBlackBoxPath {

  require(groupSize >= 1)
  require(N_key > 0)
  require(M_key > 0)
  require(bitWidth > 0)

  val nbLuts          = if (nbLUTS <= 0 || nbLUTS > N_key) N_key else nbLUTS
  val nbKeysEffective = if (nbKeys <= 0 || nbKeys > M_key) M_key else nbKeys
  val outNbBits       = 32
  val keyBits         = LUTGenUtil.keyBitsWidth(groupSize)
  val nbBitsNCnt      = math.max(1, log2Ceil(N_key))
  val nbBitsMCnt      = math.max(1, log2Ceil(M_key))
  val nbBitsActCnt    = math.max(1, log2Ceil(N_key * groupSize))
  val io              = IO(new Bundle {
    val clock     = Input(Clock())
    val reset     = Input(Bool())
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
  override def desiredName: String = "MatMulTop"
  addPath(s"generated/Top_1CC_128x128___1024x1024_gs_$groupSize.v")
}

/** Chisel wrapper that connects clock/reset for TopMMBlackBox and re-exports its ports. */
class TopMM(N_key: Int, M_key: Int, groupSize: Int = 3, bitWidth: Int = 16, nbLUTS: Int = 0, nbKeys: Int = 1)
    extends Module
    with RequireAsyncReset {

  override def desiredName: String = "TopMM"

  require(groupSize >= 1)
  require(N_key > 0)
  require(M_key > 0)
  require(bitWidth > 0)

  val nbLuts          = if (nbLUTS <= 0 || nbLUTS > N_key) N_key else nbLUTS
  val nbKeysEffective = if (nbKeys <= 0 || nbKeys > M_key) M_key else nbKeys
  val outNbBits       = 32
  val keyBits         = LUTGenUtil.keyBitsWidth(groupSize)
  val nbBitsNCnt      = math.max(1, log2Ceil(N_key))
  val nbBitsMCnt      = math.max(1, log2Ceil(M_key))
  val nbBitsActCnt    = math.max(1, log2Ceil(N_key * groupSize))

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

  val mm_blackbox = Module(new TopMMBlackBox(N_key, M_key, groupSize, bitWidth, nbLUTS, nbKeys))
  mm_blackbox.io.clock := clock
  mm_blackbox.io.reset := reset

  mm_blackbox.io.start := io.start
  io.done              := mm_blackbox.io.done
  io.key_N_cnt         := mm_blackbox.io.key_N_cnt
  io.key_M_cnt         := mm_blackbox.io.key_M_cnt
  io.act_cnt           := mm_blackbox.io.act_cnt

  mm_blackbox.io.input_activations := io.input_activations
  mm_blackbox.io.input_key         := io.input_key
  io.out                           := mm_blackbox.io.out

  io.input_enable_test := mm_blackbox.io.input_enable_test
}
