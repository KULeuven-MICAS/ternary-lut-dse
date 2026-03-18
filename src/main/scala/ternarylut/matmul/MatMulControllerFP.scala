// Copyright 2026 KU Leuven.
// Solderpad Hardware License, Version 0.51, see LICENSE for details.
// SPDX-License-Identifier: SHL-0.51
// Author: Joran Heldens

package ternarylut.matmul

import scala.math.max

import chisel3._
import chisel3.util._

/** Controller FSM for floating-point activations.
  *
  * Extends the INT controller with an sRampDown state: after all inputs have been applied, the pipeline still holds
  * in-flight partial sums. The controller keeps incrementing counters until external signals confirm all outputs have
  * been collected.
  *
  * @param N_key
  *   total weight groups in the n-dimension
  * @param M_key
  *   total output columns
  * @param groupSize
  *   LUT group size µ
  * @param nbLUTS
  *   parallel LUTs per cycle L (0 = all = N_key)
  * @param nbKeys
  *   parallel key fetches per LUT K (0 = all = M_key)
  * @param nbRegisters
  *   pipeline register stages per FP adder
  * @param maxActValue
  *   upper bound for the activation counter (normal compute + ramp-down), used to size the counter register
  * @param maxKeyNValue
  *   upper bound for the key-N counter (normal compute + ramp-down), used to size the counter register
  */
class MatMulControllerFP(
  N_key:        Int,
  M_key:        Int,
  groupSize:    Int = 3,
  nbLUTS:       Int = 0,
  nbKeys:       Int = 1,
  nbRegisters:  Int = 0,
  maxActValue:  Int,
  maxKeyNValue: Int
) extends Module
    with RequireAsyncReset {
  import MatMulStates.State._

  require(groupSize >= 1)
  require(N_key > 0)
  require(M_key > 0)
  require(nbLUTS    >= 0)
  require(nbKeys    >= 0)
  require(nbLUTS    <= N_key)
  require(nbKeys    <= M_key)
  val nbLuts          = if (nbLUTS == 0) N_key else nbLUTS
  val nbKeysEffective = if (nbKeys == 0) M_key else nbKeys
  val nbEnableSignals = (M_key.toFloat / nbKeysEffective.toFloat).ceil.toInt
  val lutDelay        = max(1, groupSize - 1)
  val nbBitsNCnt      = math.max(1, log2Ceil(maxKeyNValue))
  val nbBitsMCnt      = math.max(1, log2Ceil(M_key))
  val nbBitsActCnt    = math.max(1, log2Ceil(maxActValue))

  val io = IO(new Bundle {
    val start                  = Input(Bool())
    val done                   = Output(Bool())
    val key_N_cnt              = Output(UInt(nbBitsNCnt.W))
    val key_M_cnt              = Output(UInt(nbBitsMCnt.W))
    val act_cnt                = Output(UInt(nbBitsActCnt.W))
    val ramp_down_act_finished = Input(Bool())
    val ramp_down_key_finished = Input(Bool())
    val all_outputs_received   = Input(Bool())

    val input_enable = Output(Vec(nbEnableSignals, Bool()))
  })

  val regKeyNCnt = RegInit(0.U(nbBitsNCnt.W))
  val regKeyMCnt = RegInit(0.U(nbBitsMCnt.W))
  val regActCnt  = RegInit(0.U(nbBitsActCnt.W))
  io.key_N_cnt := ShiftRegister(regKeyNCnt, nbRegisters * lutDelay)
  io.key_M_cnt := ShiftRegister(regKeyMCnt, nbRegisters * lutDelay)

  io.act_cnt := regActCnt

  val state = RegInit(sIdle)

  io.done := false.B
  for (i <- 0 until nbEnableSignals) {
    io.input_enable(i) := false.B
  }

  switch(state) {
    is(sIdle) {
      when(io.start) {
        state := sApplyActivationsAndKeys
      }.otherwise {
        state := sIdle
      }

      regKeyNCnt := 0.U
      regKeyMCnt := 0.U
      regActCnt  := 0.U
      io.done    := false.B
    }
    is(sApplyActivationsAndKeys) {

      io.done := false.B

      when((regKeyMCnt +& nbKeysEffective.U) >= (M_key).U) {
        regKeyMCnt := 0.U

        when(regKeyNCnt +& nbLuts.U >= N_key.U) {
          if (nbRegisters > 0) {
            regKeyNCnt := regKeyNCnt + (nbLuts).U
            regActCnt  := regActCnt + (groupSize * nbLuts).U
            state      := sRampDown
          } else {
            regKeyNCnt := 0.U
            state      := sShutdown
          }

        }.otherwise {
          regKeyNCnt := regKeyNCnt + (nbLuts).U
          regActCnt  := regActCnt + (groupSize * nbLuts).U
          state      := sApplyActivationsAndKeys
        }
      }.otherwise {
        regKeyMCnt := regKeyMCnt + (nbKeysEffective).U
        state      := sApplyActivationsAndKeys
      }

      io.input_enable(regKeyMCnt / nbKeysEffective.U) := true.B
    }
    is(sShutdown) {
      io.done    := true.B
      regKeyNCnt := 0.U
      regKeyMCnt := 0.U
      regActCnt  := 0.U
      state      := sIdle
    }

    is(sRampDown) {
      when(io.ramp_down_act_finished & io.ramp_down_key_finished & io.all_outputs_received) {
        io.done    := true.B
        regKeyNCnt := 0.U
        regKeyMCnt := 0.U
        regActCnt  := 0.U
        state      := sIdle
      }.otherwise {
        io.done    := false.B
        regActCnt  := regActCnt + (groupSize * nbLuts).U
        regKeyNCnt := regKeyNCnt + (nbLuts).U
        state      := sRampDown
      }
    }
  }
}
