// Copyright 2026 KU Leuven.
// Solderpad Hardware License, Version 0.51, see LICENSE for details.
// SPDX-License-Identifier: SHL-0.51
// Author: Joran Heldens

package ternarylut.lutgen

import scala.math._

import chisel3._
import chisel3.util._

import fp_unit._

/** Pipelined LUT generator (V1), floating-point activations.
  *
  * Inserts ShiftRegister stages between adder-tree levels to allow high-frequency operation. Set nbRegisters = 0 to
  * obtain the purely combinational equivalent of LUTGenCombV1FP.
  *
  * @param groupSize
  *   LUT group size µ (1 to 12)
  * @param fpType
  *   floating-point format (FP16, BF16, FP32, etc.)
  * @param nbRegisters
  *   pipeline register stages per adder-tree level (0 = combinational)
  */
class LUTGenPipelinedV1FP(groupSize: Int = 4, fpType: FpType = FP16, nbRegisters: Int = 1)
    extends Module
    with RequireAsyncReset {
  require(groupSize >= 1 && groupSize <= 12, "Group size must be in the range of 1 and 12")
  override def desiredName: String = s"lutgen_v1_pipelined_gs_$groupSize"
  val numberEntries = LUTGenUtil.numberOfEntries(groupSize)

  val io = IO(new Bundle {
    val in  = Input(Vec(groupSize, UInt((fpType.width).W)))
    val out = Output(Vec(numberEntries, UInt((fpType.width).W)))
  })

  if (groupSize >= 3) {
    val fpAdders    = Seq.fill(2)(Module(new FpAddFp(fpType, fpType, fpType)))
    val msbs        = Wire(Vec(4, UInt((fpType.width).W)))
    val nbAlignRegs = LUTGenUtil.getAlignRegisters(groupSize)

    fpAdders(0).io.in_a := io.in(0)
    fpAdders(0).io.in_b := io.in(1)
    msbs(0)             := ShiftRegister(fpAdders(0).io.out, nbRegisters * (1 + nbAlignRegs))

    msbs(1) := ShiftRegister(io.in(0), nbRegisters * (1 + nbAlignRegs))

    fpAdders(1).io.in_a := io.in(0)
    fpAdders(1).io.in_b := io.in(1) ^ (1.U << (fpType.width - 1).U)
    msbs(2)             := ShiftRegister(fpAdders(1).io.out, nbRegisters * (1 + nbAlignRegs))

    msbs(3) := ShiftRegister(io.in(1), nbRegisters * (1 + nbAlignRegs))
    val level1Out    = LUTGenUtil.level1OutSize(groupSize)
    val remainingOut = LUTGenUtil.remainingOutCount(groupSize)
    val nextLevelAct = Array.tabulate(groupSize) { i =>
      val size = if (i == 0) LUTGenUtil.level1OutSize(groupSize) else pow(3, groupSize - 1 - i).toInt
      WireInit(VecInit(Seq.fill(size)(0.U(fpType.width.W))))
    }
    val fpAddersTree = Seq.fill(LUTGenUtil.getNbAdders(groupSize)) { Module(new FpAddFp(fpType, fpType, fpType)) }

    var acc = 0
    for {
      i <- 0 until 4
      j <- 0 until level1Out
    } {
      fpAddersTree(acc).io.in_a := msbs(i)
      fpAddersTree(acc).io.in_b := nextLevelAct(0)(j)
      io.out(i * level1Out + j) := ShiftRegister(fpAddersTree(acc).io.out, nbRegisters)
      acc += 1
    }

    var rangeK = 1
    for (i <- 1 until (groupSize - 1)) {
      if (i >= 2) rangeK = 3
      for (k <- 0 until rangeK) {
        for (j <- 0 until (level1Out / (pow(3, i - 1).toInt))) {
          val currLevelIdx   = (k * (level1Out / (pow(3, i - 1).toInt)).toInt) + j
          val rangeJ         = level1Out / (pow(3, i - 1).toInt)
          val comb           = (j.toFloat / (rangeJ.toFloat / 3)).floor.toInt
          val extraAlignRegs = groupSize - 3 - (i - 1)
          if (comb == 0) {
            fpAddersTree(acc).io.in_a         := nextLevelAct(i)(j)
            fpAddersTree(acc).io.in_b         := ShiftRegister(io.in(1 + i), nbRegisters * extraAlignRegs)
            nextLevelAct(i - 1)(currLevelIdx) := ShiftRegister(fpAddersTree(acc).io.out, nbRegisters)
            acc += 1
          } else if (comb == 1)
            nextLevelAct(i - 1)(currLevelIdx) := ShiftRegister(nextLevelAct(i)(j), nbRegisters)
          else {
            fpAddersTree(acc).io.in_a         := nextLevelAct(i)(j)
            fpAddersTree(acc).io.in_b         := ShiftRegister(
              io.in(1 + i) ^ (1.U << (fpType.width - 1).U),
              nbRegisters * extraAlignRegs
            )
            nextLevelAct(i - 1)(currLevelIdx) := ShiftRegister(fpAddersTree(acc).io.out, nbRegisters)
            acc += 1
          }
        }
      }
    }

    for (i <- 0 until remainingOut) io.out((level1Out * 4) + i) := ShiftRegister(nextLevelAct(0)(i), nbRegisters)
    for (i <- 0 until 3) nextLevelAct(groupSize - 2)(i)         := 0.U

  } else if (groupSize == 2) {
    val fpAddersAdd = Module(new FpAddFp(fpType, fpType, fpType))
    fpAddersAdd.io.in_a := io.in(0)
    fpAddersAdd.io.in_b := io.in(1)
    io.out(0)           := ShiftRegister(fpAddersAdd.io.out, nbRegisters)

    io.out(1) := ShiftRegister(io.in(0), nbRegisters)

    val fpAddersSub = Module(new FpAddFp(fpType, fpType, fpType))
    fpAddersSub.io.in_a := io.in(0)
    fpAddersSub.io.in_b := io.in(1) ^ (1.U << (fpType.width - 1).U)
    io.out(2)           := ShiftRegister(fpAddersSub.io.out, nbRegisters)

    io.out(3) := ShiftRegister(io.in(1), nbRegisters)
  } else io.out(0) := ShiftRegister(io.in(0), nbRegisters)
}

/** Pipelined LUT generator (V1), INT activations.
  *
  * Inserts one RegNext per adder-tree level, giving a latency of (groupSize - 1) cycles.
  *
  * @param groupSize
  *   LUT group size µ (must be ≥ 3)
  * @param bitWidth
  *   activation bit width
  */
class LUTGenPipelinedV1(groupSize: Int = 4, bitWidth: Int = 16) extends Module with RequireAsyncReset {
  require(groupSize >= 3)
  val outNbBits     = LUTGenUtil.outNbBitsInt(groupSize, bitWidth)
  val numberEntries = LUTGenUtil.numberOfEntries(groupSize)
  override def desiredName: String = s"lutgen_v1_pipelined_gs_$groupSize"

  val io = IO(new Bundle {
    val in  = Input(Vec(groupSize, SInt(bitWidth.W)))
    val out = Output(Vec(numberEntries, SInt((outNbBits).W)))
  })

  withReset(reset.asAsyncReset) {
    val msbs = Wire(Vec(4, SInt((outNbBits).W)))
    msbs(0) := RegNext(io.in(0) +& io.in(1), 0.S(outNbBits.W))
    msbs(1) := RegNext(io.in(0), 0.S(outNbBits.W))
    msbs(2) := RegNext(io.in(0) -& io.in(1), 0.S(outNbBits.W))
    msbs(3) := RegNext(io.in(1), 0.S(outNbBits.W))
    val level1Out    = LUTGenUtil.level1OutSize(groupSize)
    val remainingOut = LUTGenUtil.remainingOutCount(groupSize)
    val nextLevelAct = Array.tabulate(groupSize) { i =>
      val size = if (i == 0) LUTGenUtil.level1OutSize(groupSize) else pow(3, groupSize - 1 - i).toInt
      WireInit(VecInit(Seq.fill(size)(0.S(outNbBits.W))))
    }

    for {
      i <- 0 until 4
      j <- 0 until level1Out
    } { io.out(i * level1Out + j) := RegNext(msbs(i) + nextLevelAct(0)(j), 0.S(outNbBits.W)) }

    var rangeK = 1
    for (i <- 1 until (groupSize - 1)) {
      if (i >= 2) rangeK = 3
      for (k <- 0 until rangeK) {
        for (j <- 0 until (level1Out / (pow(3, i - 1).toInt))) {
          val currLevelIdx = (k * (level1Out / (pow(3, i - 1).toInt)).toInt) + j
          val rangeJ       = level1Out / (pow(3, i - 1).toInt)
          val comb         = (j.toFloat / (rangeJ.toFloat / 3)).floor.toInt
          val useReg       = i != groupSize - 2 || groupSize == 3
          if (comb == 0) {
            nextLevelAct(i - 1)(currLevelIdx) := (if (useReg) RegNext(nextLevelAct(i)(j) + io.in(1 + i))
                                                  else nextLevelAct(i)(j) + io.in(1 + i))
          } else if (comb == 1) {
            nextLevelAct(i - 1)(currLevelIdx) := (if (useReg) RegNext(nextLevelAct(i)(j))
                                                  else nextLevelAct(i)(j))
          } else {
            nextLevelAct(i - 1)(currLevelIdx) := (if (useReg) RegNext(nextLevelAct(i)(j) - io.in(1 + i))
                                                  else nextLevelAct(i)(j) - io.in(1 + i))
          }
        }
      }
    }
    for (i <- 0 until remainingOut) io.out((level1Out * 4) + i) := RegNext(nextLevelAct(0)(i))
    for (i <- 0 until 3) nextLevelAct(groupSize - 2)(i)         := 0.S
  }
}

/** Naive pipelined LUT generator (V2), INT — reference implementation.
  *
  * Computes each entry independently via a recursive RegNext chain of depth (groupSize - 1). No sharing across entries.
  * Use for verification against LUTGenPipelinedV1.
  *
  * @param groupSize
  *   LUT group size µ (must be ≥ 3)
  * @param bitWidth
  *   activation bit width
  */
class LUTGenPipelinedV2(groupSize: Int = 4, bitWidth: Int = 16) extends Module with RequireAsyncReset {
  require(groupSize >= 3)
  override def desiredName: String = s"lutgen_v2_pipelined_gs_$groupSize"
  val table         = TernaryTruthTable.generateTernaryTruthTable(groupSize)
  val outNbBits     = LUTGenUtil.outNbBitsInt(groupSize, bitWidth)
  val numberEntries = LUTGenUtil.numberOfEntries(groupSize)
  val io            = IO(new Bundle {
    val in  = Input(Vec(groupSize, SInt(bitWidth.W)))
    val out = Output(Vec(numberEntries, SInt((outNbBits).W)))
  })

  def getChiselValue(activIdx: Int, ternaryValue: Int): SInt =
    if (ternaryValue == -1) ~io.in(activIdx) +& 1.S
    else if (ternaryValue == 0) 0.S(bitWidth.W)
    else io.in(activIdx)

  withReset(reset.asAsyncReset) {
    def completeRegChain(activIdx: Int, entryIdx: Int): SInt =
      if (activIdx == groupSize - 1) getChiselValue(activIdx, table(entryIdx)(activIdx))
      else
        RegNext(
          getChiselValue(activIdx, table(entryIdx)(activIdx)) +& completeRegChain(activIdx + 1, entryIdx),
          0.S(outNbBits.W)
        )
    for (i <- 0 until numberEntries)
      io.out(i) := RegNext(getChiselValue(0, table(i)(0)) +& completeRegChain(1, i), 0.S(outNbBits.W))
  }
}

/** BlackBox wrapper around a pre-generated pipelined LUT Verilog file (V3).
  *
  * The resource file must be placed at `src/main/resources/lutgen_pipelined/<name>.v`.
  */
class LUTGenPipelinedV3BlackBox(
  name:      String,
  groupSize: Int,
  bitWidth:  Int
) extends BlackBox
    with HasBlackBoxResource {
  require(groupSize >= 3)
  val numberEntries = LUTGenUtil.numberOfEntries(groupSize)
  val outNbBits     = LUTGenUtil.outNbBitsInt(groupSize, bitWidth)
  override def desiredName: String = "LUTGen"
  val io = IO(new Bundle {
    val clock = Input(Clock())
    val reset = Input(Bool())
    val in    = Input(Vec(groupSize, SInt(bitWidth.W)))
    val out   = Output(Vec(numberEntries, SInt((outNbBits).W)))
  })

  addResource(s"/lutgen_pipelined/$name.v")
}

/** Chisel wrapper for LUTGenPipelinedV3BlackBox, connecting its ports to standard Chisel IO. */
class LUTGenPipelinedV3(
  name:      String = "lutgen_v3_pipelined_gs_3",
  groupSize: Int    = 3,
  bitWidth:  Int    = 8
) extends Module
    with RequireAsyncReset {
  require(groupSize >= 3)
  override def desiredName: String = s"lutgen_v3_pipelined_gs_$groupSize"
  val numberEntries = LUTGenUtil.numberOfEntries(groupSize)
  val outNbBits     = LUTGenUtil.outNbBitsInt(groupSize, bitWidth)
  val io            = IO(new Bundle {
    val in  = Input(Vec(groupSize, SInt(bitWidth.W)))
    val out = Output(Vec(numberEntries, SInt((outNbBits).W)))
  })

  val lutgenPipelinedBlackbox = Module(new LUTGenPipelinedV3BlackBox(name, groupSize, bitWidth))
  lutgenPipelinedBlackbox.io.in    := io.in
  io.out                           := lutgenPipelinedBlackbox.io.out
  lutgenPipelinedBlackbox.io.clock := clock
  lutgenPipelinedBlackbox.io.reset := reset.asBool
}
