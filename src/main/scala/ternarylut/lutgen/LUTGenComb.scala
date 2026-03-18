// Copyright 2026 KU Leuven.
// Solderpad Hardware License, Version 0.51, see LICENSE for details.
// SPDX-License-Identifier: SHL-0.51
// Author: Joran Heldens

package ternarylut.lutgen

import scala.math._

import chisel3._
import chisel3.util._

import fp_unit._

/** Combinational LUT generator (V1), INT, with registered outputs.
  *
  * Implements the optimized Build-Phase adder tree (paper Section III-B) using symmetry, redundancy, and sparsity
  * optimizations. For µ < 3 use LUTGenCombV1MuAll.
  *
  * @param groupSize
  *   LUT group size µ (must be ≥ 3)
  * @param bitWidth
  *   activation bit width (e.g. 8 for INT8)
  */
class LUTGenCombV1OutReg(groupSize: Int = 4, bitWidth: Int = 16) extends Module with RequireAsyncReset {
  require(groupSize >= 3)
  val outNbBits     = LUTGenUtil.outNbBitsInt(groupSize, bitWidth)
  val numberEntries = LUTGenUtil.numberOfEntries(groupSize)

  override def desiredName: String = s"lutgen_v1_comb_gs_$groupSize"
  val io = IO(new Bundle {
    val in  = Input(Vec(groupSize, SInt(bitWidth.W)))
    val out = Output(Vec(numberEntries, SInt((outNbBits).W)))
  })

  val outputRegs = RegInit(VecInit(Seq.fill(numberEntries)(0.S((outNbBits).W))))
  io.out := outputRegs
  val msbs = Wire(Vec(4, SInt((outNbBits).W)))
  msbs(0) := io.in(0) +& io.in(1)
  msbs(1) := io.in(0)
  msbs(2) := io.in(0) -& io.in(1)
  msbs(3) := io.in(1)

  val level1Out    = LUTGenUtil.level1OutSize(groupSize)
  val remainingOut = LUTGenUtil.remainingOutCount(groupSize)
  val nextLevelAct = Array.tabulate(groupSize) { i =>
    val size = if (i == 0) LUTGenUtil.level1OutSize(groupSize) else pow(3, groupSize - 1 - i).toInt
    WireInit(VecInit(Seq.fill(size)(0.S(outNbBits.W))))
  }

  for {
    i <- 0 until 4
    j <- 0 until level1Out
  } outputRegs(i * level1Out + j) := msbs(i) + nextLevelAct(0)(j)

  var rangeK = 1
  for (i <- 1 until (groupSize - 1)) {
    if (i >= 2) rangeK = 3
    for (k <- 0 until rangeK) {
      for (j <- 0 until (level1Out / (pow(3, i - 1).toInt))) {
        val currLevelIdx = (k * (level1Out / (pow(3, i - 1).toInt)).toInt) + j
        val rangeJ       = level1Out / (pow(3, i - 1).toInt)
        val comb         = (j.toFloat / (rangeJ.toFloat / 3)).floor.toInt
        if (comb == 0) nextLevelAct(i - 1)(currLevelIdx) := nextLevelAct(i)(j) + io.in(1 + i)
        else if (comb == 1) nextLevelAct(i - 1)(currLevelIdx) := nextLevelAct(i)(j)
        else nextLevelAct(i - 1)(currLevelIdx)                := nextLevelAct(i)(j) - io.in(1 + i)
      }
    }
  }
  for (i <- 0 until remainingOut) outputRegs((level1Out * 4) + i) := nextLevelAct(0)(i)
  for (i <- 0 until 3) nextLevelAct(groupSize - 2)(i)             := 0.S
}

/** Combinational LUT generator (V1), INT, with direct wire outputs.
  *
  * Same optimized adder tree as LUTGenCombV1OutReg but without output registers.
  *
  * @param groupSize
  *   LUT group size µ (must be ≥ 3; use LUTGenCombV1MuAll for µ < 3)
  * @param bitWidth
  *   activation bit width
  */
class LUTGenCombV1(groupSize: Int = 3, bitWidth: Int = 16) extends Module with RequireAsyncReset {
  require(groupSize >= 3, "Group size must be at least 3 for this variant; use LUTGenCombV1MuAll for smaller sizes")
  val outNbBits     = LUTGenUtil.outNbBitsInt(groupSize, bitWidth)
  val numberEntries = LUTGenUtil.numberOfEntries(groupSize)
  override def desiredName: String = s"lutgen_v1_comb_gs_$groupSize"

  val io = IO(new Bundle {
    val in  = Input(Vec(groupSize, SInt(bitWidth.W)))
    val out = Output(Vec(numberEntries, SInt((outNbBits).W)))
  })

  val msbs = Wire(Vec(4, SInt((outNbBits).W)))
  msbs(0) := io.in(0) +& io.in(1)
  msbs(1) := io.in(0)
  msbs(2) := io.in(0) -& io.in(1)
  msbs(3) := io.in(1)

  val level1Out    = LUTGenUtil.level1OutSize(groupSize)
  val remainingOut = LUTGenUtil.remainingOutCount(groupSize)
  val nextLevelAct = Array.tabulate(groupSize) { i =>
    val size = if (i == 0) LUTGenUtil.level1OutSize(groupSize) else pow(3, groupSize - 1 - i).toInt
    WireInit(VecInit(Seq.fill(size)(0.S(outNbBits.W))))
  }

  for {
    i <- 0 until 4
    j <- 0 until level1Out
  } { io.out(i * level1Out + j) := msbs(i) + nextLevelAct(0)(j) }

  var rangeK = 1
  for (i <- 1 until (groupSize - 1)) {
    if (i >= 2) rangeK = 3
    for (k <- 0 until rangeK) {
      for (j <- 0 until (level1Out / (pow(3, i - 1).toInt))) {
        val currLevelIdx = (k * (level1Out / (pow(3, i - 1).toInt)).toInt) + j
        val rangeJ       = level1Out / (pow(3, i - 1).toInt)
        val comb         = (j.toFloat / (rangeJ.toFloat / 3)).floor.toInt
        if (comb == 0) nextLevelAct(i - 1)(currLevelIdx) := nextLevelAct(i)(j) + io.in(1 + i)
        else if (comb == 1) nextLevelAct(i - 1)(currLevelIdx) := nextLevelAct(i)(j)
        else nextLevelAct(i - 1)(currLevelIdx)                := nextLevelAct(i)(j) - io.in(1 + i)
      }
    }
  }
  for (i <- 0 until remainingOut) io.out((level1Out * 4) + i) := nextLevelAct(0)(i)
  for (i <- 0 until 3) nextLevelAct(groupSize - 2)(i)         := 0.S
}

/** Combinational LUT generator (V1), floating-point activations.
  *
  * Same optimized adder-tree structure as the INT variant, but uses FpAddFp hardware units instead of integer adders.
  *
  * @param groupSize
  *   LUT group size µ (1 to 12)
  * @param fpType
  *   floating-point format (FP16, BF16, FP32, etc.)
  */
class LUTGenCombV1FP(groupSize: Int = 2, fpType: FpType = FP16) extends Module with RequireAsyncReset {

  require(groupSize >= 1 && groupSize <= 12, "Group size must be in the range of 1 and 12")
  val numberEntries = LUTGenUtil.numberOfEntries(groupSize)

  override def desiredName: String = s"lutgen_v1_comb_gs_$groupSize"

  val io = IO(new Bundle {
    val in  = Input(Vec(groupSize, UInt(fpType.width.W)))
    val out = Output(Vec(numberEntries, UInt(fpType.width.W)))
  })

  val outputRegs = RegInit(VecInit(Seq.fill(numberEntries)(0.U((fpType.width).W))))
  io.out := outputRegs

  if (groupSize >= 3) {

    val msbs = Wire(Vec(4, UInt((fpType.width).W)))

    val fpAdders = Seq.fill(2) { Module(new FpAddFp(fpType, fpType, fpType)) }

    fpAdders(0).io.in_a := io.in(0)
    fpAdders(0).io.in_b := io.in(1)
    msbs(0)             := fpAdders(0).io.out

    msbs(1) := io.in(0)

    fpAdders(1).io.in_a := io.in(0)
    fpAdders(1).io.in_b := io.in(1) ^ (1.U << (fpType.width - 1).U)
    msbs(2)             := fpAdders(1).io.out

    msbs(3) := io.in(1)

    val level1Out    = LUTGenUtil.level1OutSize(groupSize)
    val remainingOut = LUTGenUtil.remainingOutCount(groupSize)
    val nextLevelAct = Array.tabulate(groupSize) { i =>
      val size = if (i == 0) LUTGenUtil.level1OutSize(groupSize) else pow(3, groupSize - 1 - i).toInt
      WireInit(VecInit(Seq.fill(size)(0.U(fpType.width.W))))
    }

    val totalAdders  = LUTGenUtil.getNbAdders(groupSize)
    val fpAddersTree = Seq.fill(totalAdders) { Module(new FpAddFp(fpType, fpType, fpType)) }

    var acc = 0
    for {
      i <- 0 until 4
      j <- 0 until level1Out
    } {
      fpAddersTree(acc).io.in_a     := msbs(i)
      fpAddersTree(acc).io.in_b     := nextLevelAct(0)(j)
      outputRegs(i * level1Out + j) := fpAddersTree(acc).io.out
      acc += 1
    }

    var rangeK = 1
    for (i <- 1 until (groupSize - 1)) {
      if (i >= 2) rangeK = 3
      for (k <- 0 until rangeK) {
        for (j <- 0 until (level1Out / (pow(3, i - 1).toInt))) {
          val currLevelIdx = (k * (level1Out / (pow(3, i - 1).toInt)).toInt) + j
          val rangeJ       = level1Out / (pow(3, i - 1).toInt)
          val comb         = (j.toFloat / (rangeJ.toFloat / 3)).floor.toInt
          if (comb == 0) {
            fpAddersTree(acc).io.in_a         := nextLevelAct(i)(j)
            fpAddersTree(acc).io.in_b         := io.in(1 + i)
            nextLevelAct(i - 1)(currLevelIdx) := fpAddersTree(acc).io.out
            acc += 1
          } else if (comb == 1) nextLevelAct(i - 1)(currLevelIdx) := nextLevelAct(i)(j)
          else {
            fpAddersTree(acc).io.in_a         := nextLevelAct(i)(j)
            fpAddersTree(acc).io.in_b         := io.in(1 + i) ^ (1.U << (fpType.width - 1).U)
            nextLevelAct(i - 1)(currLevelIdx) := fpAddersTree(acc).io.out
            acc += 1
          }
        }
      }
    }
    for (i <- 0 until remainingOut) outputRegs((level1Out * 4) + i) := nextLevelAct(0)(i)
    for (i <- 0 until 3) nextLevelAct(groupSize - 2)(i)             := 0.U
  } else {
    if (groupSize == 2) {
      val fpAddersAdd = Module(new FpAddFp(fpType, fpType, fpType))
      fpAddersAdd.io.in_a := io.in(0)
      fpAddersAdd.io.in_b := io.in(1)
      outputRegs(0)       := fpAddersAdd.io.out

      outputRegs(1) := io.in(0)

      val fpAddersSub = Module(new FpAddFp(fpType, fpType, fpType))
      fpAddersSub.io.in_a := io.in(0)
      fpAddersSub.io.in_b := io.in(1) ^ (1.U << (fpType.width - 1).U)
      outputRegs(2)       := fpAddersSub.io.out

      outputRegs(3) := io.in(1)
    } else outputRegs(0) := io.in(0)
  }
}

/** Combinational LUT generator (V1), INT, supporting all group sizes µ ≥ 1.
  *
  * Handles the edge cases µ = 1 (single pass-through) and µ = 2 (one adder). For µ ≥ 3 uses the same optimized adder
  * tree as LUTGenCombV1OutReg.
  *
  * @param groupSize
  *   LUT group size µ (any value ≥ 1)
  * @param bitWidth
  *   activation bit width
  */
class LUTGenCombV1MuAll(groupSize: Int = 4, bitWidth: Int = 16) extends Module with RequireAsyncReset {
  val outNbBits     = LUTGenUtil.outNbBitsInt(groupSize, bitWidth)
  val numberEntries = LUTGenUtil.numberOfEntries(groupSize)
  override def desiredName: String = s"lutgen_v1_comb_gs_$groupSize"

  val io = IO(new Bundle {
    val in  = Input(Vec(groupSize, SInt(bitWidth.W)))
    val out = Output(Vec(numberEntries, SInt((outNbBits).W)))
  })

  val outputRegs = RegInit(VecInit(Seq.fill(numberEntries)(0.S((outNbBits).W))))
  io.out := outputRegs

  if (groupSize >= 3) {
    val msbs = Wire(Vec(4, SInt((outNbBits).W)))
    msbs(0) := io.in(0) +& io.in(1)
    msbs(1) := io.in(0)
    msbs(2) := io.in(0) -& io.in(1)
    msbs(3) := io.in(1)
    val level1Out    = LUTGenUtil.level1OutSize(groupSize)
    val remainingOut = LUTGenUtil.remainingOutCount(groupSize)
    val nextLevelAct = Array.tabulate(groupSize) { i =>
      val size = if (i == 0) LUTGenUtil.level1OutSize(groupSize) else pow(3, groupSize - 1 - i).toInt
      WireInit(VecInit(Seq.fill(size)(0.S(outNbBits.W))))
    }

    for {
      i <- 0 until 4
      j <- 0 until level1Out
    } { outputRegs(i * level1Out + j) := msbs(i) + nextLevelAct(0)(j) }

    var rangeK = 1
    for (i <- 1 until (groupSize - 1)) {
      if (i >= 2) rangeK = 3
      for (k <- 0 until rangeK) {
        for (j <- 0 until (level1Out / (pow(3, i - 1).toInt))) {
          val currLevelIdx = (k * (level1Out / (pow(3, i - 1).toInt)).toInt) + j
          val rangeJ       = level1Out / (pow(3, i - 1).toInt)
          val comb         = (j.toFloat / (rangeJ.toFloat / 3)).floor.toInt
          if (comb == 0) nextLevelAct(i - 1)(currLevelIdx) := nextLevelAct(i)(j) + io.in(1 + i)
          else if (comb == 1) nextLevelAct(i - 1)(currLevelIdx) := nextLevelAct(i)(j)
          else nextLevelAct(i - 1)(currLevelIdx)                := nextLevelAct(i)(j) - io.in(1 + i)
        }
      }
    }
    for (i <- 0 until remainingOut) outputRegs((level1Out * 4) + i) := nextLevelAct(0)(i)
    for (i <- 0 until 3) nextLevelAct(groupSize - 2)(i)             := 0.S
  } else if (groupSize == 2) {
    outputRegs(0) := io.in(0) +& io.in(1)
    outputRegs(1) := io.in(0)
    outputRegs(2) := io.in(0) -& io.in(1)
    outputRegs(3) := io.in(1)
  } else outputRegs(0) := io.in(0)
}

/** Naive combinational LUT generator (V2), INT — reference implementation.
  *
  * Computes each LUT entry independently by summing all µ ternary-weighted activations from the truth table. No sharing
  * or sparsity optimizations. Use for verification against the V1 variants.
  *
  * @param groupSize
  *   LUT group size µ (must be ≥ 3)
  * @param bitWidth
  *   activation bit width
  */
class LUTGenCombV2(groupSize: Int = 4, bitWidth: Int = 16) extends Module with RequireAsyncReset {
  require(groupSize >= 3)
  override def desiredName: String = s"lutgen_v2_comb_gs_$groupSize"
  val table         = TernaryTruthTable.generateTernaryTruthTable(groupSize)
  val outNbBits     = LUTGenUtil.outNbBitsInt(groupSize, bitWidth)
  val numberEntries = LUTGenUtil.numberOfEntries(groupSize)

  val io = IO(new Bundle {
    val in  = Input(Vec(groupSize, SInt(bitWidth.W)))
    val out = Output(Vec(numberEntries, SInt((outNbBits).W)))
  })

  for (i <- 0 until numberEntries) {
    io.out(i) := io.in
      .zip(table(i))
      .map { case (inVal, tableVal) =>
        tableVal match {
          case -1 => ~inVal +& 1.S
          case 0  => 0.S
          case 1  => inVal
        }
      }
      .reduce(_ +& _)
  }
}

/** BlackBox wrapper around a pre-generated combinational LUT Verilog file (V3).
  *
  * The resource file must be placed at `src/main/resources/lutgen_comb/<name>.v`.
  */
class LUTGenCombV3BlackBox(
  name:      String,
  groupSize: Int,
  bitWidth:  Int
) extends BlackBox
    with HasBlackBoxResource {
  require(groupSize >= 3)
  val numberEntries = LUTGenUtil.numberOfEntries(groupSize)
  val outNbBits     = LUTGenUtil.outNbBitsInt(groupSize, bitWidth)

  val io = IO(new Bundle {
    val in  = Input(Vec(groupSize, SInt(bitWidth.W)))
    val out = Output(Vec(numberEntries, SInt((outNbBits).W)))
  })
  override def desiredName: String = "LUTGen"
  addResource(s"/lutgen_comb/$name.v")
}

/** Chisel wrapper for LUTGenCombV3BlackBox, connecting its ports to standard Chisel IO. */
class LUTGenCombV3(
  name:      String = "lutgen_v3_comb_gs_3",
  groupSize: Int    = 3,
  bitWidth:  Int    = 8
) extends Module
    with RequireAsyncReset {

  override def desiredName: String = s"lutgen_v3_comb_gs_$groupSize"

  require(groupSize >= 3)
  val numberEntries = LUTGenUtil.numberOfEntries(groupSize)
  val outNbBits     = LUTGenUtil.outNbBitsInt(groupSize, bitWidth)
  val io            = IO(new Bundle {
    val in  = Input(Vec(groupSize, SInt(bitWidth.W)))
    val out = Output(Vec(numberEntries, SInt((outNbBits).W)))
  })

  val lutgenCombBlackbox = Module(new LUTGenCombV3BlackBox(name, groupSize, bitWidth))
  lutgenCombBlackbox.io.in := io.in
  io.out                   := lutgenCombBlackbox.io.out
}
