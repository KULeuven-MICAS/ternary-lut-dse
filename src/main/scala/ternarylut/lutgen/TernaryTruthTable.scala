// Copyright 2026 KU Leuven.
// Solderpad Hardware License, Version 0.51, see LICENSE for details.
// SPDX-License-Identifier: SHL-0.51
// Author: Joran Heldens

package ternarylut.lutgen

/** Generates the positive-half ternary truth table used by V2 LUT generators.
  *
  * Symmetry reduction (paper Section III-B.1): the full 3^µ table is halved by exploiting the {-1, 0, +1} symmetry.
  * Only the (3^µ - 1)/2 "positive" entries are stored; negative counterparts are recovered at runtime by sign-flipping
  * the stored entry.
  */
object TernaryTruthTable {

  /** Returns (3^µ − 1)/2 rows, each a list of µ ternary weight values in {-1, 0, +1}.
    *
    * @param groupSize
    *   LUT group size µ
    */
  def generateTernaryTruthTable(groupSize: Int): List[List[Int]] = {
    val values     = List(-1, 0, 1)
    val truthTable = cartesianProduct(List.fill(groupSize)(values))
    truthTable.map(_.map(_ * -1)).take((truthTable.length - 1) / 2)
  }

  private def cartesianProduct(lists: List[List[Int]]): List[List[Int]] =
    lists match {
      case Nil          => List(Nil)
      case head :: tail =>
        for {
          h <- head
          t <- cartesianProduct(tail)
        } yield h :: t
    }
}
