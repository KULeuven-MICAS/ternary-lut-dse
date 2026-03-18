// Copyright 2026 KU Leuven.
// Solderpad Hardware License, Version 0.51, see LICENSE for details.
// SPDX-License-Identifier: SHL-0.51
// Author: Joran Heldens

package ternarylut.matmul

import scala.annotation.unused

import chisel3._
import chisel3.util._

import fp_unit._
import ternarylut.lutgen.LUTGenUtil

/** Fetch MUX for FP activations: selects a LUT entry by encoded weight key.
  *
  * @param groupSize
  *   LUT group size µ; determines number of MUX inputs ((3^µ - 1)/2)
  * @param fpType
  *   floating-point format (FP16, BF16, FP32, etc.)
  * @param keyBits
  *   encoded weight key width
  */
class MatMulMuxLutFP(groupSize: Int, fpType: FpType, keyBits: Int) extends Module with RequireAsyncReset {

  val numberEntries = LUTGenUtil.numberOfEntries(groupSize)

  val io = IO(new Bundle {
    val in             = Input(Vec(numberEntries, UInt((fpType.width).W)))
    val key            = Input(UInt(keyBits.W))
    val selected_entry = Output(UInt(fpType.width.W))
  })

  io.selected_entry := MuxLookup(io.key, 0.U)((0 until io.in.size).map(idx => idx.U -> io.in(idx)))
}

/** Sign inversion for FP: conditionally flips the sign bit (MSB) of the fetched entry.
  *
  * @param _fpType
  *   unused; present for API symmetry with the INT variant (MatMulMuxInverse).
  */
class MatMulMuxInverseFP(@unused _fpType: FpType) extends Module with RequireAsyncReset {
  val io = IO(new Bundle {
    val in     = Input(Bool())
    val out    = Output(Bool())
    val invert = Input(Bool())
  })
  io.out := Mux(io.invert, ~io.in, io.in)
}
