// Copyright 2026 KU Leuven.
// Solderpad Hardware License, Version 0.51, see LICENSE for details.
// SPDX-License-Identifier: SHL-0.51
// Author: Joran Heldens

package ternarylut.lutgen

import scala.math._

import chisel3.util.log2Ceil

/** Shared LUT parameters and adder-tree helpers (entries, key width, level sizes, adder count, alignment). */
object LUTGenUtil {

  /** Number of LUT entries for group size µ: (3^µ - 1) / 2. */
  def numberOfEntries(groupSize: Int): Int = ((pow(3, groupSize) - 1) / 2).toInt

  /** Total key bit width (index + sign bit) for group size µ. Uses Chisel log2Ceil for RTL consistency. */
  def keyBitsWidth(groupSize: Int): Int = log2Ceil(numberOfEntries(groupSize) + 1) + 1

  /** Size of the first-level block output (3^(µ−2)) in the V1 adder tree. Requires groupSize >= 3. */
  def level1OutSize(groupSize: Int): Int = pow(3, groupSize - 2).toInt

  /** Number of LUT entries beyond the first four blocks: numberOfEntries − 4×level1OutSize. */
  def remainingOutCount(groupSize: Int): Int = numberOfEntries(groupSize) - 4 * level1OutSize(groupSize)

  /** Output bit width for INT LUT: (µ − 1) + bitWidth (accumulation growth). */
  def outNbBitsInt(groupSize: Int, bitWidth: Int): Int = (groupSize - 1) + bitWidth

  /** Number of adders in the optimized V1 tree */
  def getNbAdders(groupSize: Int = 2): Int = {
    val level1Out   = level1OutSize(groupSize)
    var totalAdders = 0
    var rangeK      = 1
    for (i <- 1 until (groupSize - 1)) {
      if (i >= 2) rangeK = 3
      for (_ <- 0 until rangeK) {
        for (j <- 0 until (level1Out / (pow(3, i - 1).toInt))) {
          val rangeJ = level1Out / (pow(3, i - 1).toInt)
          val comb   = (j.toFloat / (rangeJ.toFloat / 3)).floor.toInt
          if (comb == 0 || comb == 2) totalAdders += 1
        }
      }
    }
    totalAdders += (4 * level1Out)
    totalAdders
  }

  /** Returns the number of pipeline alignment registers needed for group sizes ≥ 4.
    *
    * For pipelined implementations, later activation inputs arrive at different pipeline stages and require alignment
    * registers to synchronize with the adder tree outputs.
    */
  def getAlignRegisters(groupSize: Int = 2): Int = if (groupSize >= 4) groupSize - 3 else 0
}
