// Copyright 2026 KU Leuven.
// Solderpad Hardware License, Version 0.51, see LICENSE for details.
// SPDX-License-Identifier: SHL-0.51
// Author: Joran Heldens

package ternarylut.matmul

import chisel3._
import chisel3.util._

import ternarylut.lutgen.LUTGenUtil

/** Fetch MUX for INT activations: selects a LUT entry by encoded weight key (paper Section III-C).
  *
  * @param nbInputBits
  *   activation bit width
  * @param groupSize
  *   LUT group size µ; determines number of MUX inputs ((3^µ - 1)/2)
  * @param outNbBits
  *   output bit width (may be wider than the LUT output for safe accumulation)
  * @param keyBits
  *   encoded weight key width (index bits, sign bit excluded)
  */
class MatMulMuxLut(nbInputBits: Int, groupSize: Int, outNbBits: Int, keyBits: Int)
    extends Module
    with RequireAsyncReset {

  val numberEntries = LUTGenUtil.numberOfEntries(groupSize)
  val lutOutBits    = LUTGenUtil.outNbBitsInt(groupSize, nbInputBits)

  val io = IO(new Bundle {
    val in             = Input(Vec(numberEntries, SInt((lutOutBits).W)))
    val key            = Input(UInt(keyBits.W))
    val selected_entry = Output(SInt(outNbBits.W))
  })

  io.selected_entry := MuxLookup(io.key, 0.S)((0 until io.in.size).map(idx => idx.U -> io.in(idx)))
}

/** Sign inversion for INT: conditionally negates the fetched LUT entry (two's complement). */
class MatMulMuxInverse(outNbBits: Int) extends Module with RequireAsyncReset {
  val io = IO(new Bundle {
    val in     = Input(SInt(outNbBits.W))
    val out    = Output(SInt(outNbBits.W))
    val invert = Input(Bool())
  })

  io.out := Mux(io.invert, ~io.in +& 1.S, io.in)
}
