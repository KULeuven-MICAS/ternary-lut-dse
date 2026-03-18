// Copyright 2026 KU Leuven.
// Solderpad Hardware License, Version 0.51, see LICENSE for details.
// SPDX-License-Identifier: SHL-0.51
// Author: Joran Heldens

package ternarylut.matmul

import chisel3._
import chisel3.util._

/** Controller FSM for the integer activation datapath.
  *
  * Generates counter values (key_N_cnt, key_M_cnt, act_cnt) that tell the external memory/testbench which slice of the
  * activation vector and key matrix to present each cycle. Also generates per-column-group enable signals that gate the
  * output accumulation registers.
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
class MatMulController(
  N_key:     Int,
  M_key:     Int,
  groupSize: Int = 3,
  bitWidth:  Int = 16,
  nbLUTS:    Int = 0,
  nbKeys:    Int = 1
) extends Module
    with RequireAsyncReset {
  import MatMulStates.State._

  require(groupSize >= 1)
  require(N_key > 0)
  require(M_key > 0)
  require(bitWidth > 0)
  require(nbLUTS    >= 0)
  require(nbKeys    >= 0)
  require(nbLUTS    <= N_key)
  require(nbKeys    <= M_key)

  val nbLuts          = if (nbLUTS == 0) N_key else nbLUTS
  val nbKeysEffective = if (nbKeys == 0) M_key else nbKeys
  val nbEnableSignals = (M_key.toFloat / nbKeysEffective.toFloat).ceil.toInt
  val nbBitsNCnt      = math.max(1, log2Ceil(N_key))
  val nbBitsMCnt      = math.max(1, log2Ceil(M_key))
  val nbBitsActCnt    = math.max(1, log2Ceil(N_key * groupSize))

  val io = IO(new Bundle {
    val start        = Input(Bool())
    val done         = Output(Bool())
    val key_N_cnt    = Output(UInt(nbBitsNCnt.W))
    val key_M_cnt    = Output(UInt(nbBitsMCnt.W))
    val act_cnt      = Output(UInt(nbBitsActCnt.W))
    val input_enable = Output(Vec(nbEnableSignals, Bool()))
  })

  val regKeyNCnt       = RegInit(0.U(nbBitsNCnt.W))
  val regKeyMCnt       = RegInit(0.U(nbBitsMCnt.W))
  val regKeyNCntBuffer = RegInit(0.U(nbBitsNCnt.W))
  val regKeyMCntBuffer = RegInit(0.U(nbBitsMCnt.W))
  val regActCnt        = RegInit(0.U(nbBitsActCnt.W))

  io.key_N_cnt     := regKeyNCntBuffer
  io.key_M_cnt     := regKeyMCntBuffer
  regKeyNCntBuffer := regKeyNCnt
  regKeyMCntBuffer := regKeyMCnt
  io.act_cnt       := regActCnt

  val inputEnableBuffer = RegInit(VecInit(Seq.fill(nbEnableSignals)(false.B)))
  io.input_enable := inputEnableBuffer

  val state = RegInit(sIdle)

  io.done := false.B
  for (i <- 0 until nbEnableSignals) inputEnableBuffer(i) := false.B
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
      io.done    := true.B
    }
    is(sApplyActivationsAndKeys) {

      io.done := false.B

      when((regKeyMCnt +& nbKeysEffective.U) >= (M_key).U) {
        regKeyMCnt := 0.U

        when(regKeyNCnt +& nbLuts.U >= N_key.U) {
          regKeyNCnt := 0.U
          state      := sShutdown
        }.otherwise {
          regKeyNCnt := regKeyNCnt + (nbLuts).U
          regActCnt  := regActCnt + (groupSize * nbLuts).U
          state      := sApplyActivationsAndKeys
        }
      }.otherwise {
        regKeyMCnt := regKeyMCnt + (nbKeysEffective).U
        state      := sApplyActivationsAndKeys
      }

      inputEnableBuffer(regKeyMCnt / nbKeysEffective.U) := true.B
    }
    is(sShutdown) {
      state                                                  := sIdle
      inputEnableBuffer((M_key.U / nbKeysEffective.U) - 1.U) := true.B
    }
  }
}
