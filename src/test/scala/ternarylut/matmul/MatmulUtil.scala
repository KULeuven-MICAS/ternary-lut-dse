// Copyright 2026 KU Leuven.
// Solderpad Hardware License, Version 0.51, see LICENSE for details.
// SPDX-License-Identifier: SHL-0.51
// Author: Joran Heldens
//
// Test Utilities for LUT-Based Ternary Matrix Multiplication
//
// Provides golden-model reference implementations and data preparation helpers
// used by the top-level integration tests (TopTesterINT.scala, TopTesterFP.scala).
//
// Key functions:
//   - generateTernaryMatrix:  Creates a random ternary {-1, 0, +1} weight matrix
//                              using the BitNet b1.58 quantization scheme.
//   - generateKeyMatrix:      Encodes a ternary weight matrix into the dense key
//                              format expected by the hardware (paper Section III-D).
//   - goldenMatMulINT / goldenMatMulFP: Scalar golden-model GEMV for INT and FP activations.

package ternarylut.matmul
import scala.util.Random

import chisel3._
import chisel3.util._

import fp_unit._

trait MatmulUtil extends FpUtils {

  /** Generates a random ternary weight matrix of size N × M.
    *
    * Applies the absmean quantization scheme from BitNet b1.58 (Ma et al., 2024): W_q = RoundClip(W / (mean(|W|) +
    * eps), -1, 1)
    *
    * @param N
    *   number of rows
    * @param M
    *   number of columns
    * @param inputMatrix
    *   optional pre-generated matrix to quantize instead of random
    * @return
    *   ternary matrix with values in {-1.0, 0.0, 1.0}
    */
  def generateTernaryMatrix(N: Int, M: Int, inputMatrix: Option[Array[Array[Float]]] = None): Array[Array[Float]] = {

    val lowerBound = -65504
    val upperBound = 65504

    // generate random matrix if it is not provided
    val matrix = inputMatrix.getOrElse {
      Array.fill(N, M) {
        lowerBound + Random
          .nextFloat() * (upperBound - lowerBound) // Generates random floats between lowerBound and upperBound
      }
    }

    // quantize the matrix to ternary values according to:
    // The Era of 1-bit LLMs: All Large Language Models are in 1.58 Bits by Ma et al.
    // determine the mean average value of the matrix
    val avg = matrix.flatten.map(math.abs).sum / (N * M)
    val eps = 0.0001f

    // function that implements the roundClip function
    def roundClip(x: Float): Float = {
      math.max(-1, math.min(1, math.round(x).toFloat))
    }

    // apply the roundClip function on every entry of the matrix
    val qMatrix = matrix.map(_.map(x => roundClip(x / (avg + eps))))

    qMatrix
  }

  /** Encodes a raw ternary index into the dense key format used by the hardware.
    *
    * The encoding maps a group of µ ternary weights to a compact index of width ceil(log2((3^µ-1)/2)) + 1 bits. The MSB
    * is the symmetry flag: if set, the fetched LUT entry must be sign-inverted (paper Section III-D).
    *
    * @param value
    *   raw ternary combination index (0 to 3^µ - 1)
    * @param bitWidth
    *   total encoded key width
    * @param groupSize
    *   LUT group size µ
    * @return
    *   Chisel UInt literal ready to be poked into the DUT
    */
  def encodeTernaryValue(value: Int, bitWidth: Int, groupSize: Int): UInt = {
    // if the value is larger than the border value, then the sign bit is set to 1
    val borderValue = (math.pow(3, groupSize) - 1).toInt / 2

    if (value > borderValue) {
      // lower half of table -> set the sign bit to 1 and flip the value to the upper part
      val upperPartValue = (((value.toFloat - math.pow(3, groupSize) + 1)) * -1).toInt
      val chiselValue    = (upperPartValue + math.pow(2, bitWidth - 1).toInt).U((bitWidth).W)
      chiselValue
    } else {
      // upper half of table -> sign bit to 0 and keep the value
      val chiselValue = value.U((bitWidth).W)
      chiselValue
    }
  }

  /** Generates a key matrix of size N_key × M_key (Chisel UInts) from a ternary matrix. */
  def generateKeyMatrix(
    groupSize:   Int,
    N:           Int,
    M:           Int,
    inputMatrix: Option[Array[Array[Float]]] = None,
    printDebug:  Boolean                     = false
  ): (Array[Array[Int]], Array[Array[chisel3.UInt]]) = {

    // generate a ternary matrix
    val ternaryMatrix = generateTernaryMatrix(N, M, inputMatrix)

    // determine the size of the key matrix
    val N_key       = (N.toFloat / groupSize.toFloat).ceil.toInt
    val padding_row = (N_key * groupSize) - N

    // add a padding row to the ternary matrix of zero values
    val ternaryMatrix_padded = ternaryMatrix ++ Array.fill(padding_row, M)(0.0f)

    if (printDebug) {
      println("Ternary matrix (padded):")
      ternaryMatrix_padded.foreach(row => println(row.mkString("\t")))
    }

    // create the key matrix
    // increase every entry of matrix by 1
    val keyMatrix = ternaryMatrix_padded.map(_.map(x => (x * -1.0f) + 1.0f))

    // reshape the matrix to N_key x M_key x groupSize
    val keyMatrix_reshaped = keyMatrix.grouped(groupSize).toArray.map(_.transpose)

    // for every group find the ternary value and store it in a matrix of size N_key x M_key
    val ternaryValues = keyMatrix_reshaped.map { group =>
      group.map { col =>
        col.reverse.zipWithIndex.map { case (value, index) =>
          value * math.pow(3, index).toFloat
        }.sum.toInt
      }
    }

    if (printDebug) {
      println("\nTernary values of matrix:")
      ternaryValues.foreach(row => println(row.mkString("\t")))
    }

    // determine the number of bits required to encode the ternary values
    // ceil(log2(3^groupSize / 2)) + 1 --> the extra bit to indicate which half of the LUT to use
    val bitWidth = log2Ceil(((math.pow(3, groupSize).toInt - 1) / 2) + 1) + 1

    // apply the encoding function to the ternary values
    val keyMatrix_encoded = ternaryValues.map { row =>
      row.map(value => encodeTernaryValue(value, bitWidth, groupSize))
    }

    if (printDebug) {
      println("\nEncoded key matrix:")
      keyMatrix_encoded.foreach { row =>
        row.foreach { value =>
          val msb       = value(bitWidth - 1)
          val otherBits = value(bitWidth - 2, 0)
          if (msb.litValue == 0) {
            print("<0> " + otherBits.litValue + "\t")
          } else {
            print("<1> " + otherBits.litValue + "\t")
          }
        }
        println("")
      }
    }

    // Convert the padded ternary matrix to Int
    val ternaryMatrix_padded_int = ternaryMatrix_padded.map(_.map(_.toInt))

    (ternaryMatrix_padded_int, keyMatrix_encoded)
  }

  /** Golden model for FP vector–matrix multiplication (ternary weights). */
  def goldenMatMulFP(
    inputVector:  Array[Float],
    inputMatrix:  Array[Array[Int]],
    debug_output: Boolean = false,
    fpType:       FpType
  ): (Array[Float], Array[Float]) = {
    val nbRows = inputMatrix.length
    val nbCols = inputMatrix(0).length
    val N      = inputVector.length

    val inputVectorPadded = inputVector ++ Array.fill(nbRows - N)(0.0f)

    if (debug_output) {
      println(s"Dimensions of the input vector: ${inputVectorPadded.length}")
      println(s"Dimensions of the matrix: $nbRows x $nbCols")

      // print input activation vector
      println("\nInput activation vector (padded): ")
      inputVectorPadded.foreach(x => print(s"$x "))
      println("\n")
    }

    // create the result vector
    val resultVector = Array.fill(nbCols)((0.0f))

    // loop over the rows and col of the matrix
    for (i <- 0 until nbCols) {
      for (j <- 0 until nbRows) {
        // multiply the input vector with the matrix
        if (inputMatrix(j)(i) == 1) {
          resultVector(i) = (resultVector(i), fpType) + (inputVectorPadded(j), fpType)
        } else if (inputMatrix(j)(i) == -1) {
          // if the weight is -1, then we subtract it from the result vector
          resultVector(i) = (resultVector(i), fpType) + (-1 * inputVectorPadded(j), fpType)
        } else {
          // weight is zero, do nothing
        }
      }
    }

    (inputVectorPadded, resultVector)
  }

  /** Golden model for INT vector–matrix multiplication (ternary weights). */
  def goldenMatMulINT(
    inputVector:  Array[Int],
    inputMatrix:  Array[Array[Int]],
    debug_output: Boolean = false
  ): (Array[Int], Array[Float]) = {
    val nbRows = inputMatrix.length
    val nbCols = inputMatrix(0).length
    val N      = inputVector.length

    val inputVectorPadded = inputVector ++ Array.fill(nbRows - N)(0)

    if (debug_output) {
      println(s"Dimensions of the input vector: ${inputVectorPadded.length}")
      println(s"Dimensions of the matrix: $nbRows x $nbCols")
      println("\nInput activation vector (padded): ")
      inputVectorPadded.foreach(x => print(s"$x "))
      println("\n")
    }

    val resultVector = Array.fill(nbCols)(0.0f)
    for (i <- 0 until nbCols) {
      for (j <- 0 until nbRows) {
        resultVector(i) += inputMatrix(j)(i) * inputVectorPadded(j)
      }
    }

    (inputVectorPadded, resultVector)
  }
}
