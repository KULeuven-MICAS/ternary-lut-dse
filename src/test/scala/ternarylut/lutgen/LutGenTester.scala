// Copyright 2026 KU Leuven.
// Solderpad Hardware License, Version 0.51, see LICENSE for details.
// SPDX-License-Identifier: SHL-0.51
// Author: Joran Heldens
//
// LUT Generator Unit Tests
//
// Functional verification tests for all LUT generator variants (V1/V2/V3,
// combinational/pipelined, INT/FP). Each test:
//   1. Generates a Scala-side reference truth table for the given group size µ.
//   2. Applies random input activations to the DUT.
//   3. Compares the DUT output against the Scala golden model.
//
// For pipelined variants, tests account for pipeline latency by aligning the
// input stimulus schedule with the expected output timing.

package ternarylut.lutgen

import scala.math.pow

import chisel3._

import chiseltest._
import fp_unit._
import org.scalatest.flatspec.AnyFlatSpec

/** Shared configuration trait for all LUT generator tests.
  *
  * Override these methods in subclasses to customize the test sweep range, activation bit width/type, and pipeline
  * depth.
  */
trait SimulationParameters {
  def getStartGroupSize:      Int    = 1
  def getEndGroupSize:        Int    = 4
  def getNbBits:              Int    = 8
  def getFpType:              FpType = FP16
  def getTestCycles:          Int    = 10
  def getNbPipelineRegisters: Int    = 0
  def getLatency(groupSize: Int): Int = if (groupSize >= 2) groupSize - 1 else 1
}

class LUTGenPipelinedV1FPTester extends AnyFlatSpec with ChiselScalatestTester with SimulationParameters with FpUtils {

  val fpType          = getFpType
  val test_cycles     = getTestCycles
  val startGroupSize  = getStartGroupSize
  val endGroupSize    = getEndGroupSize
  val verbose         = false
  val fp_error_margin = 0.1
  val nbRegs          = getNbPipelineRegisters

  for (groupSize <- startGroupSize to endGroupSize) {
    if (verbose) println(s"LUTGenPipelinedV1FP: groupSize=$groupSize, cycles=$test_cycles, nbRegs=$nbRegs")

    s"LUTGenPipelinedV1FP_GS_${groupSize}" should "work" in {
      test(new LUTGenPipelinedV1FP(groupSize, fpType, nbRegs))
        .withAnnotations(Seq(VerilatorBackendAnnotation, WriteVcdAnnotation)) { c =>
          // generate the LUT in scala
          val table: List[List[Int]] = TernaryTruthTable.generateTernaryTruthTable(groupSize)
          var nbFails       = 0
          val print_summary = 100
          val latency       = getLatency(groupSize) * nbRegs

          // generate all the input test vectors
          val input_test_vectors: Seq[Seq[Float]] = Array
            .fill(test_cycles + latency) {
              Seq.fill(groupSize) {
                genRandomValue(fpType)
              }
            }
            .toSeq

          var execution_cycles = 0
          var ready_to_check   = 0

          if (nbRegs == 0) {
            // is combination circuit, so the latency is 0
            execution_cycles = input_test_vectors.length
            ready_to_check   = 0
          } else {
            // is pipelined circuit, so the latency is groupSize - 2
            execution_cycles = input_test_vectors.length - 1
            ready_to_check   = latency - 1
          }

          for (i <- 0 until execution_cycles) {
            // apply the input test vector to the DUT
            (0 until groupSize).foreach { j =>
              c.io.in(j).poke(floatToUInt(fpType, input_test_vectors(i)(j)).U)
            }

            c.clock.step(1)

            if (i >= ready_to_check) {
              // check the output of the DUT
              val check_idx = i - (ready_to_check)
              val expected_output: Seq[Float] = table.map { row =>
                row.zip(input_test_vectors(check_idx)).map { case (x, a) => x * a }.sum
              }

              if (verbose) {
                println(s"$i. Activation input: ${input_test_vectors(check_idx)}")
                println(s"$i. Expected value: $expected_output")
                print(s"$i. Actual output:  List(")
                (0 until expected_output.length).foreach(j =>
                  print(s"${uintToFloat(fpType, c.io.out(j).peek().litValue)} ")
                )
                println(")")
              }

              // manually check with peek() such that you can show more details when it fails
              var failed = false

              for (j <- 0 until expected_output.length) {
                val actual_output = uintToFloat(fpType, c.io.out(j).peek().litValue)
                val diff          = actual_output - expected_output(j)

                if (diff > fp_error_margin || diff < -fp_error_margin) {
                  failed = true
                  println(s"Mismatch at index $j: expected ${expected_output(j)} but got ${actual_output}")
                  println(s"Error margin applied: ${fp_error_margin}")
                }
              }

              if (failed) {
                println("Error for test cycle: " + i)
                println(s"$i. Activation input: ${input_test_vectors(check_idx)}")
                print(s"$i. Actual output:  List(")
                (0 until expected_output.length).foreach(j =>
                  print(s"${uintToFloat(fpType, c.io.out(j).peek().litValue)}, ")
                )
                println(")")
                println(s"$i. Expected value: $expected_output")
                println("*********************************")
                nbFails += 1
              }
            }

            // print the summary every print_summary cycles
            if (i % print_summary == 0 && i != 0) {
              println(s"$i test cycles done")
            }
          }

          if (nbFails == 0) {
            println("All test cycles passed for LUTGenPipelinedV1FP")
          } else {
            assert(false, s"$nbFails test cycles failed for LUTGenPipelinedV1FP")
          }
        }
    }
  }
}

class LUTGenCombV1FPTester extends AnyFlatSpec with ChiselScalatestTester with SimulationParameters with FpUtils {

  val fpType          = getFpType
  val test_cycles     = getTestCycles
  val startGroupSize  = getStartGroupSize
  val endGroupSize    = getEndGroupSize
  val verbose         = false
  val fp_error_margin = 0.01

  for (groupSize <- startGroupSize to endGroupSize) {
    if (verbose) println(s"LUTGenCombV1FP: groupSize=$groupSize, cycles=$test_cycles")

    s"LUTGenCombV1FP_GS_${groupSize}" should "work" in {
      test(new LUTGenCombV1FP(groupSize, fpType))
        .withAnnotations(Seq(VerilatorBackendAnnotation, WriteVcdAnnotation)) { c =>
          // generate the LUT in scala
          val table         = TernaryTruthTable.generateTernaryTruthTable(groupSize)
          var nbFails       = 0
          val print_summary = 100

          for (i <- 0 until test_cycles) {
            // apply groupSize random values to input
            val input = (0 until groupSize).map { _ =>
              genRandomValue(fpType)
            }
            if (verbose)
              println(s"$i. Activation input: $input")

            (0 until groupSize).foreach(j => c.io.in(j).poke(floatToUInt(fpType, input(j)).U))

            // calculate the expected value with the generated LUT from the previous cell
            val expected_lut_output = table.map { row =>
              row.zip(input).map { case (x, a) => x * a }.sum
            }

            c.clock.step(1)

            if (verbose) {
              println(s"$i. Expected value: $expected_lut_output")
              print(s"$i. Actual output:  List(")
              (0 until expected_lut_output.length).foreach(j =>
                print(s"${uintToFloat(fpType, c.io.out(j).peek().litValue)} ")
              )
              println(")")
            }

            // manually check with peek() such that you can show more details when it fails
            var failed = false

            for (j <- 0 until expected_lut_output.length) {
              val actual_output = uintToFloat(fpType, c.io.out(j).peek().litValue)
              val diff          = actual_output - expected_lut_output(j)

              if (diff > fp_error_margin || diff < -fp_error_margin) {
                failed = true
                println(s"Mismatch at index $j: expected ${expected_lut_output(j)} but got ${actual_output}")
                println(s"Error margin applied: ${fp_error_margin}")
              }
            }

            if (failed) {
              println("Error for test cycle: " + i)
              println(s"$i. Activation input: $input")
              print(s"$i. Actual output:  List(")
              (0 until expected_lut_output.length).foreach(j =>
                print(s"${uintToFloat(fpType, c.io.out(j).peek().litValue)}, ")
              )
              println(")")
              println(s"$i. Expected value: $expected_lut_output")
              println("*********************************")
              nbFails += 1
            }

            // print the summary every print_summary cycles
            if (i % print_summary == 0 && i != 0) {
              println(s"$i test cycles done")
            }

            c.clock.step(1)
          }
          if (nbFails == 0) {
            println("All test cycles passed for LUTGenCombV1FP")
          } else {
            assert(false, s"$nbFails test cycles failed for LUTGenCombV1FP")
          }
        }
    }
  }
}

class LUTGenCombV1Tester extends AnyFlatSpec with ChiselScalatestTester with SimulationParameters {

  val nbBits         = getNbBits
  val test_cycles    = getTestCycles
  val startGroupSize = getStartGroupSize
  val endGroupSize   = getEndGroupSize
  val verbose        = false

  for (groupSize <- startGroupSize to endGroupSize if groupSize >= 3) {
    if (verbose) println(s"LUTGenCombV1: groupSize=$groupSize, nbBits=$nbBits, cycles=$test_cycles")

    s"LUTGenCombV1_GS_${groupSize}" should "work" in {
      test(new LUTGenCombV1(groupSize, nbBits)).withAnnotations(Seq(WriteVcdAnnotation)) { c =>
        // generate the LUT in scala
        val table         = TernaryTruthTable.generateTernaryTruthTable(groupSize)
        var nbFails       = 0
        val print_summary = 100

        for (i <- 0 until test_cycles) {
          // apply groupSize random values to input
          val input = (0 until groupSize).map { _ =>
            scala.util.Random.nextInt((pow(2, nbBits - 1).toInt) * 2) - pow(2, nbBits - 1).toInt
          }
          if (verbose)
            println(s"$i. Activation input: $input")

          (0 until groupSize).foreach(j => c.io.in(j).poke(input(j).S))

          // calculate the expected value with the generated LUT from the previous cell
          val expected_lut_output = table.map { row =>
            row.zip(input).map { case (x, a) => x * a }.sum
          }

          if (verbose) {
            println(s"$i. Expected value: $expected_lut_output")
            (0 until expected_lut_output.length).foreach(j => print(s"${c.io.out(j).peek().litValue} "))
            println("")
          }

          // manually check with peek() such that you can show more details when it fails
          var failed = false
          (0 until expected_lut_output.length).foreach(j =>
            failed |= (c.io.out(j).peek().litValue != expected_lut_output(j))
          )

          if (failed) {
            println("Error for test cycle: " + i)
            println(s"$i. Activation input: $input")
            print(s"$i. Actual output:  List(")
            (0 until expected_lut_output.length).foreach(j => print(s"${c.io.out(j).peek().litValue}, "))
            println(")")
            println(s"$i. Expected value: $expected_lut_output")
            println("*********************************")
            nbFails += 1
          }

          // print the summary every print_summary cycles
          if (i % print_summary == 0 && i != 0) {
            println(s"$i test cycles done")
          }

          c.clock.step(1)
        }
        if (nbFails == 0) {
          println("All test cycles passed for LUTGenCombV1")
        } else {
          assert(false, s"$nbFails test cycles failed for LUTGenCombV1")
        }
      }
    }
  }
}

class LUTGenCombV1MuAllTester extends AnyFlatSpec with ChiselScalatestTester with SimulationParameters {

  val nbBits         = getNbBits
  val test_cycles    = getTestCycles
  val startGroupSize = 1 // mu_all supports µ ≥ 1
  val endGroupSize   = getEndGroupSize
  val verbose        = false

  for (groupSize <- startGroupSize to endGroupSize) {
    if (verbose) println(s"LUTGenCombV1MuAll: groupSize=$groupSize, nbBits=$nbBits, cycles=$test_cycles")

    s"LUTGenCombV1MuAll_GS_${groupSize}" should "work" in {
      test(new LUTGenCombV1MuAll(groupSize, nbBits)).withAnnotations(Seq(WriteVcdAnnotation)) { c =>
        // generate the LUT in scala
        val table         = TernaryTruthTable.generateTernaryTruthTable(groupSize)
        var nbFails       = 0
        val print_summary = 100

        for (i <- 0 until test_cycles) {
          // apply groupSize random values to input
          val input = (0 until groupSize).map { _ =>
            scala.util.Random.nextInt((pow(2, nbBits - 1).toInt) * 2) - pow(2, nbBits - 1).toInt
          }
          if (verbose)
            println(s"$i. Activation input: $input")

          (0 until groupSize).foreach(j => c.io.in(j).poke(input(j).S))

          // calculate the expected value with the generated LUT from the previous cell
          val expected_lut_output = table.map { row =>
            row.zip(input).map { case (x, a) => x * a }.sum
          }

          // step clock first since outputs are registered
          c.clock.step(1)

          if (verbose) {
            println(s"$i. Expected value: $expected_lut_output")
            (0 until expected_lut_output.length).foreach(j => print(s"${c.io.out(j).peek().litValue} "))
            println("")
          }

          // manually check with peek() such that you can show more details when it fails
          var failed = false
          (0 until expected_lut_output.length).foreach(j =>
            failed |= (c.io.out(j).peek().litValue != expected_lut_output(j))
          )

          if (failed) {
            println("Error for test cycle: " + i)
            println(s"$i. Activation input: $input")
            print(s"$i. Actual output:  List(")
            (0 until expected_lut_output.length).foreach(j => print(s"${c.io.out(j).peek().litValue}, "))
            println(")")
            println(s"$i. Expected value: $expected_lut_output")
            println("*********************************")
            nbFails += 1
          }

          // print the summary every print_summary cycles
          if (i % print_summary == 0 && i != 0) {
            println(s"$i test cycles done")
          }
        }
        if (nbFails == 0) {
          println("All test cycles passed for LUTGenCombV1MuAll")
        } else {
          assert(false, s"$nbFails test cycles failed for LUTGenCombV1MuAll")
        }
      }
    }
  }
}

class LUTGenCombV2Tester extends AnyFlatSpec with ChiselScalatestTester with SimulationParameters {

  val nbBits         = getNbBits
  val test_cycles    = getTestCycles
  val startGroupSize = getStartGroupSize
  val endGroupSize   = getEndGroupSize
  val verbose        = false

  for (groupSize <- startGroupSize to endGroupSize if groupSize >= 3) {
    if (verbose) println(s"LUTGenCombV2: groupSize=$groupSize, nbBits=$nbBits, cycles=$test_cycles")

    s"LUTGenCombV2_GS_${groupSize}" should "work" in {
      test(new LUTGenCombV2(groupSize, nbBits)).withAnnotations(Seq(WriteVcdAnnotation)) { c =>
        // generate the LUT in scala
        val table         = TernaryTruthTable.generateTernaryTruthTable(groupSize)
        var nbFails       = 0
        val print_summary = 100
        for (i <- 0 until test_cycles) {

          // apply groupSize random values to input
          val input = (0 until groupSize).map { _ =>
            scala.util.Random.nextInt((pow(2, nbBits - 1).toInt) * 2) - pow(2, nbBits - 1).toInt
          }
          if (verbose)
            println(s"$i. Activation input: $input")

          (0 until groupSize).foreach(j => c.io.in(j).poke(input(j).S))

          // calculate the expected value with the generated LUT from the previous cell
          val expected_lut_output = table.map { row =>
            row.zip(input).map { case (x, a) => x * a }.sum
          }

          if (verbose) {
            println(s"$i. Expected value: $expected_lut_output")
            (0 until expected_lut_output.length).foreach(j => print(s"${c.io.out(j).peek().litValue} "))
            println("")
          }

          // manually check with peek() such that you can show more details when it fails
          var failed = false
          (0 until expected_lut_output.length).foreach(j =>
            failed |= (c.io.out(j).peek().litValue != expected_lut_output(j))
          )

          if (failed) {
            println("Error for test cycle: " + i)
            println(s"$i. Activation input: $input")
            print(s"$i. Actual output:  List(")
            (0 until expected_lut_output.length).foreach(j => print(s"${c.io.out(j).peek().litValue}, "))
            println(")")
            println(s"$i. Expected value: $expected_lut_output")
            println("*********************************")
            nbFails += 1
          }

          // print the summary every print_summary cycles
          if (i % print_summary == 0 && i != 0) {
            println(s"$i test cycles done")
          }

          c.clock.step(1)
        }
        if (nbFails == 0) {
          println("All test cycles passed for LUTGenCombV2")
        } else {
          assert(false, s"$nbFails test cycles failed for LUTGenCombV2")
        }
      }
    }
  }
}

class LUTGenCombV3Tester extends AnyFlatSpec with ChiselScalatestTester with SimulationParameters {

  // parameters for the simulation
  val nbBits         = getNbBits
  val test_cycles    = getTestCycles
  val startGroupSize = getStartGroupSize
  val endGroupSize   = getEndGroupSize
  val verbose        = false

  for (groupSize <- startGroupSize to endGroupSize if groupSize >= 3) {
    if (verbose) println(s"LUTGenCombV3Tester: groupSize=$groupSize, nbBits=$nbBits, cycles=$test_cycles")
    val name = s"lutgen_v3_comb_gs_$groupSize"

    s"LUTGenCombV3_GS_${groupSize}" should "work" in {
      test(
        new LUTGenCombV3(name, groupSize, nbBits)
      ).withAnnotations(Seq(VerilatorBackendAnnotation, WriteVcdAnnotation)) { c =>
        // generate the LUT in scala
        val table         = TernaryTruthTable.generateTernaryTruthTable(groupSize)
        var nbFails       = 0
        val print_summary = 100
        for (i <- 0 until test_cycles) {

          // apply groupSize random values to input
          val input = (0 until groupSize).map { _ =>
            scala.util.Random.nextInt((pow(2, nbBits - 1).toInt) * 2) - pow(2, nbBits - 1).toInt
          }
          if (verbose)
            println(s"$i. Activation input: $input")

          (0 until groupSize).foreach(j => c.io.in(j).poke(input(j).S))

          // calculate the expected value with the generated LUT from the previous cell
          val expected_lut_output = table.map { row =>
            row.zip(input).map { case (x, a) => x * a }.sum
          }

          if (verbose) {
            println(s"$i. Expected value: $expected_lut_output")
            (0 until expected_lut_output.length).foreach(j => print(s"${c.io.out(j).peek().litValue} "))
            println("")
          }

          // manually check with peek() such that you can show more details when it fails
          var failed = false
          (0 until expected_lut_output.length).foreach(j =>
            failed |= (c.io.out(j).peek().litValue != expected_lut_output(j))
          )

          if (failed) {
            println("Error for test cycle: " + i)
            println(s"$i. Activation input: $input")
            print(s"$i. Actual output:  List(")
            (0 until expected_lut_output.length).foreach(j => print(s"${c.io.out(j).peek().litValue}, "))
            println(")")
            println(s"$i. Expected value: $expected_lut_output")
            println("*********************************")
            nbFails += 1
          }

          // print the summary every print_summary cycles
          if (i % print_summary == 0 && i != 0) {
            println(s"$i test cycles done")
          }

          c.clock.step(1)
        }
        if (nbFails == 0) {
          println("All test cycles passed for LUTGenCombV3")
        } else {
          assert(false, s"$nbFails test cycles failed for LUTGenCombV3")
        }
      }
    }
  }
}

class LUTGenPipelinedV1Tester extends AnyFlatSpec with ChiselScalatestTester with SimulationParameters {

  // parameters for the simulation
  val nbBits         = getNbBits
  val test_cycles    = getTestCycles
  val startGroupSize = getStartGroupSize
  val endGroupSize   = getEndGroupSize
  val verbose        = false

  def inLastCol(groupSize: Int, col: Int): Boolean = col == groupSize - 1

  def inFirstThreeColumns(col: Int): Boolean = col <= 2

  for (groupSize <- startGroupSize to endGroupSize if groupSize >= 3) {
    if (verbose) println(s"LUTGenPipelinedV1: groupSize=$groupSize, nbBits=$nbBits, cycles=$test_cycles")

    s"LUTGenPipelinedV1_GS_${groupSize}" should "work" in {
      test(new LUTGenPipelinedV1(groupSize, nbBits)).withAnnotations(Seq(WriteVcdAnnotation)) { c =>

        var nbFails       = 0
        val print_summary = 100
        var latency       = 0
        val table         = TernaryTruthTable.generateTernaryTruthTable(groupSize)

        if (groupSize == 3) {
          latency = 2
        } else {
          latency = groupSize - 2
        }

        // create a 2d array to store ALL the input test vectors
        // for all the test cycles
        val input_test_vectors = Array.fill(test_cycles, groupSize) {
          scala.util.Random.nextInt((pow(2, nbBits - 1).toInt) * 2) - pow(2, nbBits - 1).toInt
        }

        // ready to use
        var input_test_vectors_transformed = Array.empty[Array[Int]]

        // transform the input_test_vectors to an order that matches
        // the datapath of the V1 pipelined implementation
        // only needed if the group size is NOT 3 or 4
        if (groupSize != 3 && groupSize != 4) {
          // the dimension of the input_test_vectors is (test_cycles, groupSize)
          // the dimension of the input_test_vectors_transformed is (test_cycles + latency - 1, groupSize) (see notes)

          // make an zero transformed array of the correct size
          input_test_vectors_transformed = Array.fill(test_cycles + latency - 1, groupSize) { 0 }
          var shift = 0

          for (col <- groupSize - 1 to 0 by -1) {
            for (row <- 0 until test_cycles) {
              input_test_vectors_transformed(row + shift)(col) = input_test_vectors(row)(col)
            }

            // do not increase the shift when leaving the last column or the first three columns
            if (!inFirstThreeColumns(col) && !inLastCol(groupSize, col)) {
              shift += 1
            }
          }
        } else {
          // just append an extra zero row to the input_test_vectors
          input_test_vectors_transformed = input_test_vectors :+ Array.fill(groupSize)(0)
        }

        // print the input test vectors and transformed input test vectors
        if (verbose) {
          println("Input Test Vectors:")
          input_test_vectors.foreach(row => println(row.mkString(", ")))

          println("Transformed Input Test Vectors:")
          input_test_vectors_transformed.foreach(row => println(row.mkString(", ")))
        }

        val nb_cycles = input_test_vectors_transformed.length

        for (i <- 0 until nb_cycles) {

          // apply them to the input
          (0 until groupSize).foreach(j => c.io.in(j).poke(input_test_vectors_transformed(i)(j).S))

          c.clock.step(1)

          val input_idx = i - (latency - 1)

          if (input_idx >= 0) {
            // calculate the expected value with the generated LUT from the previous cell
            val expected_lut_output = table.map { row =>
              row.zip(input_test_vectors(input_idx)).map { case (x, a) => x * a }.sum
            }

            if (verbose) {
              println(s"$i. Expected value: $expected_lut_output")
              (0 until expected_lut_output.length).foreach(j => print(s"${c.io.out(j).peek().litValue} "))
              println("")
            }

            // manually check with peek() such that you can show more details when it fails
            var failed = false
            (0 until expected_lut_output.length).foreach(j =>
              failed |= (c.io.out(j).peek().litValue != expected_lut_output(j))
            )

            if (failed) {
              println(s"Error for test cycle: $input_idx (cycle $i)")
              print(s"$input_idx. Activation input: ")
              (0 until groupSize).foreach(j => print(s"${input_test_vectors(input_idx)(j)}, "))
              println("")
              print(s"$input_idx. Actual output:  List(")
              (0 until expected_lut_output.length).foreach(j => print(s"${c.io.out(j).peek().litValue}, "))
              println(")")
              println(s"$input_idx. Expected value: $expected_lut_output")
              println("*********************************")
              nbFails += 1
            }
          }
          // print the summary every print_summary cycles
          if (i          % print_summary == 0 && i != 0) {
            println(s"$input_idx test cycles done")
          }
        }

        if (nbFails == 0) {
          println("All test cycles passed for LUTGenPipelinedV1")
        } else {
          assert(false, s"$nbFails test cycles failed for LUTGenPipelinedV1")
        }
      }
    }
  }
}

class LUTGenPipelinedV2Tester extends AnyFlatSpec with ChiselScalatestTester with SimulationParameters {

  // parameters for the simulation
  val nbBits         = getNbBits
  val test_cycles    = getTestCycles
  val startGroupSize = getStartGroupSize
  val endGroupSize   = getEndGroupSize
  val verbose        = false

  for (groupSize <- startGroupSize to endGroupSize if groupSize >= 3) {
    if (verbose) println(s"LUTGenPipelinedV2: groupSize=$groupSize, nbBits=$nbBits, cycles=$test_cycles")

    s"LUTGenPipelinedV2_GS_${groupSize}" should "work" in {
      test(new LUTGenPipelinedV2(groupSize, nbBits)).withAnnotations(Seq(WriteVcdAnnotation)) { c =>
        // generate the LUT in scala
        val table         = TernaryTruthTable.generateTernaryTruthTable(groupSize)
        var nbFails       = 0
        val print_summary = 100
        val latency       = groupSize - 1

        // create a 2d array to store ALL the input test vectors
        // for all the test cycles
        val input_test_vectors = Array.fill(test_cycles, groupSize) {
          scala.util.Random.nextInt((pow(2, nbBits - 1).toInt) * 2) - pow(2, nbBits - 1).toInt
        }

        // Note: LUTGenPipelinedV2 is a naive registered chain that does not align streaming inputs. To check functional
        // correctness, keep the input vector stable for long enough that all pipeline stages observe the same values.
        for (i <- 0 until test_cycles) {
          (0 until groupSize).foreach(j => c.io.in(j).poke(input_test_vectors(i)(j).S))
          c.clock.step(latency + 1)

          val expected_lut_output = table.map { row =>
            row.zip(input_test_vectors(i)).map { case (x, a) => x * a }.sum
          }

          var failed = false
          (0 until expected_lut_output.length).foreach(j =>
            failed |= (c.io.out(j).peek().litValue != expected_lut_output(j))
          )

          if (failed) {
            nbFails += 1
            if (verbose) {
              println(s"Mismatch for test cycle $i")
              println(s"$i. Activation input: ${input_test_vectors(i).mkString(", ")}")
              println(s"$i. Expected value: $expected_lut_output")
              print(s"$i. Actual output:  List(")
              (0 until expected_lut_output.length).foreach(j => print(s"${c.io.out(j).peek().litValue}, "))
              println(")")
              println("*********************************")
            }
          }

          if (i % print_summary == 0 && i != 0) println(s"$i test cycles done")
        }
        if (nbFails == 0) {
          println("All test cycles passed for LUTGenPipelinedV2")
        } else {
          assert(false, s"$nbFails test cycles failed for LUTGenPipelinedV2")
        }

      }
    }
  }
}

class LUTGenPipelinedV3Tester extends AnyFlatSpec with ChiselScalatestTester with SimulationParameters {

  // parameters for the simulation
  val nbBits         = getNbBits
  val test_cycles    = getTestCycles
  val startGroupSize = getStartGroupSize
  val endGroupSize   = getEndGroupSize
  val verbose        = false

  for (groupSize <- startGroupSize to endGroupSize if groupSize >= 3) {
    if (verbose) println(s"LUTGenPipelinedV3: groupSize=$groupSize, nbBits=$nbBits, cycles=$test_cycles")
    val name = s"lutgen_v3_pipelined_gs_$groupSize"

    s"LUTGenPipelinedV3_GS_${groupSize}" should "work" in {
      test(
        new LUTGenPipelinedV3(name, groupSize, nbBits)
      ).withAnnotations(Seq(VerilatorBackendAnnotation, WriteVcdAnnotation)) { c =>
        // generate the LUT in scala
        val table         = TernaryTruthTable.generateTernaryTruthTable(groupSize)
        var nbFails       = 0
        val print_summary = 100
        val latency       = groupSize - 1

        c.clock.step(5)

        // create a 2d array to store ALL the input test vectors
        // for all the test cycles
        val input_test_vectors = Array.fill(test_cycles, groupSize) {
          scala.util.Random.nextInt((pow(2, nbBits - 1).toInt) * 2) - pow(2, nbBits - 1).toInt
        }

        // Same stability requirement as LUTGenPipelinedV2: keep inputs constant long enough for the pipelined design.
        for (i <- 0 until test_cycles) {
          (0 until groupSize).foreach(j => c.io.in(j).poke(input_test_vectors(i)(j).S))
          c.clock.step(latency + 1)

          val expected_lut_output = table.map { row =>
            row.zip(input_test_vectors(i)).map { case (x, a) => x * a }.sum
          }

          var failed = false
          (0 until expected_lut_output.length).foreach(j =>
            failed |= (c.io.out(j).peek().litValue != expected_lut_output(j))
          )

          if (failed) {
            nbFails += 1
            if (verbose) {
              println(s"Mismatch for test cycle $i")
              println(s"$i. Activation input: ${input_test_vectors(i).mkString(", ")}")
              println(s"$i. Expected value: $expected_lut_output")
              print(s"$i. Actual output:  List(")
              (0 until expected_lut_output.length).foreach(j => print(s"${c.io.out(j).peek().litValue}, "))
              println(")")
              println("*********************************")
            }
          }

          if (i % print_summary == 0 && i != 0) println(s"$i test cycles done")
        }
        if (nbFails == 0) {
          println("All test cycles passed for LUTGenPipelinedV3")
        } else {
          assert(false, s"$nbFails test cycles failed for LUTGenPipelinedV3")
        }
      }
    }
  }
}
