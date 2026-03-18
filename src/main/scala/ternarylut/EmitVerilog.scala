// Copyright 2026 KU Leuven.
// Solderpad Hardware License, Version 0.51, see LICENSE for details.
// SPDX-License-Identifier: SHL-0.51
// Author: Joran Heldens

package ternarylut

import chisel3._

import fp_unit._
import ternarylut.lutgen.LUTGenCombV1FP

object EmitVerilog extends App {
  val groupSize = 2
  val fpType    = FP16
  emitVerilog(new LUTGenCombV1FP(groupSize, fpType), Array("--target-dir", "generated"))
}
