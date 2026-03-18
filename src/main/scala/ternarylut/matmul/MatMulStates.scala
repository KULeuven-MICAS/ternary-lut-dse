// Copyright 2026 KU Leuven.
// Solderpad Hardware License, Version 0.51, see LICENSE for details.
// SPDX-License-Identifier: SHL-0.51
// Author: Joran Heldens

package ternarylut.matmul

import chisel3._

/** FSM states shared by INT and FP controllers.
  *
  *   - sIdle: waiting for start.
  *   - sApplyActivationsAndKeys: active computation, cycling through tiles.
  *   - sShutdown: final cycle, asserts done.
  *   - sRampDown: FP-only, drains the pipeline after all inputs have been applied.
  */
object MatMulStates {
  object State extends ChiselEnum {
    val sIdle, sApplyActivationsAndKeys, sShutdown, sRampDown = Value
  }
}
