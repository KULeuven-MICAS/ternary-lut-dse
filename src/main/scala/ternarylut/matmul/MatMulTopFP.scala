// Copyright 2026 KU Leuven.
// Solderpad Hardware License, Version 0.51, see LICENSE for details.
// SPDX-License-Identifier: SHL-0.51
// Author: Joran Heldens

package ternarylut.matmul

import scala.math._

import chisel3._
import chisel3.util._

import fp_unit._
import ternarylut.lutgen.LUTGenUtil

/** Top-level module for floating-point activations.
  *
  * Connects MatMulControllerFP to MatMulDatapathSplittedFP. The enable signal from the controller is delayed by
  * ShiftRegister to compensate for the combined LUT and post-adder pipeline latency.
  *
  * @param N_key
  *   total weight groups in the n-dimension (= total_n / µ)
  * @param M_key
  *   total output columns
  * @param groupSize
  *   LUT group size µ (1 to 12)
  * @param fpType
  *   floating-point format (FP16, BF16, FP32, etc.)
  * @param nbLUTS
  *   parallel LUTs per cycle L (0 = all = N_key)
  * @param nbKeys
  *   parallel key fetches per LUT K (0 = all = M_key)
  * @param nbRegisters
  *   pipeline register stages per FP adder (0 = combinational)
  */
class MatMulTopFP(
  N_key:       Int,
  M_key:       Int,
  groupSize:   Int    = 3,
  fpType:      FpType = FP16,
  nbLUTS:      Int    = 0,
  nbKeys:      Int    = 1,
  nbRegisters: Int    = 0
) extends Module
    with RequireAsyncReset {
  require(groupSize   >= 1 && groupSize <= 12, "Group size must be between 1 and 12")
  require(N_key > 0)
  require(M_key > 0)
  require(nbRegisters >= 0)

  val nbLuts            = if (nbLUTS <= 0 || nbLUTS > N_key) N_key else nbLUTS
  val nbKeysEffective   = if (nbKeys <= 0 || nbKeys > M_key) M_key else nbKeys
  val keyBits           = LUTGenUtil.keyBitsWidth(groupSize)
  val lutDelay          = max(1, groupSize - 1)
  val maxRampDownCycles = nbLuts - 1
  val normalApplyCycles = (N_key.toFloat / nbLuts.toFloat).ceil.toInt
  val maxActValue       = nbLuts * groupSize * (maxRampDownCycles + normalApplyCycles)
  val maxKeyNValue      = nbLuts * (maxRampDownCycles + normalApplyCycles)
  val nbBitsNCnt        = math.max(1, log2Ceil(maxKeyNValue))
  val nbBitsMCnt        = math.max(1, log2Ceil(M_key))
  val nbBitsActCnt      = math.max(1, log2Ceil(maxActValue))

  val io = IO(new Bundle {
    val start                  = Input(Bool())
    val done                   = Output(Bool())
    val ramp_down_act_finished = Input(Bool())
    val ramp_down_key_finished = Input(Bool())
    val all_outputs_received   = Input(Bool())
    val key_N_cnt              = Output(UInt(nbBitsNCnt.W))
    val key_M_cnt              = Output(UInt(nbBitsMCnt.W))
    val act_cnt                = Output(UInt(nbBitsActCnt.W))

    val input_activations = Input(Vec(nbLuts, Vec(groupSize, UInt(fpType.width.W))))
    val input_key         = Input(Vec(nbLuts, Vec(nbKeysEffective, UInt(keyBits.W))))
    val out               = Output(Vec(M_key, UInt(fpType.width.W)))

    val input_enable_test = Output(Vec((M_key.toFloat / nbKeysEffective.toFloat).ceil.toInt, Bool()))
  })

  val controller = Module(
    new MatMulControllerFP(N_key, M_key, groupSize, nbLUTS, nbKeys, nbRegisters, maxActValue, maxKeyNValue)
  )
  val datapath   = Module(
    new MatMulDatapathSplittedFP(N_key, M_key, groupSize, fpType, nbLUTS, nbKeys, nbRegisters = nbRegisters)
  )

  datapath.io.input_enable      := ShiftRegister(controller.io.input_enable, nbRegisters * (1 + nbLuts + lutDelay))
  datapath.io.input_activations := io.input_activations
  datapath.io.input_key         := io.input_key
  io.input_enable_test          := datapath.io.input_enable

  controller.io.start                  := io.start
  controller.io.ramp_down_act_finished := io.ramp_down_act_finished
  controller.io.ramp_down_key_finished := io.ramp_down_key_finished
  controller.io.all_outputs_received   := io.all_outputs_received
  io.done                              := ShiftRegister(controller.io.done, nbRegisters * lutDelay)
  io.key_N_cnt                         := controller.io.key_N_cnt
  io.key_M_cnt                         := controller.io.key_M_cnt
  io.act_cnt                           := controller.io.act_cnt

  io.out := datapath.io.out
}
