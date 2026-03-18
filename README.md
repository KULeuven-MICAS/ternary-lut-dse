# LUT-Based Ternary Matrix Multiplication Accelerator

> ***Hardware Generation and Exploration of Lookup Table-Based Accelerators for 1.58-bit LLM Inference***  
> Accepted at [IEEE ISPASS 2026](https://ispass.org/ispass2026/).

## Overview

This repository provides an **open-source, parameterizable hardware generator** in [Chisel](https://www.chisel-lang.org/) for lookup table (LUT)-based ternary matrix multiplication accelerators, targeting efficient inference of 1.58-bit quantized Large Language Models (e.g., [BitNet b1.58](https://arxiv.org/abs/2402.17764)).

Instead of using multipliers, LUT-based architectures precompute all ternary combinations (`{-1, 0, +1}`) of a group of input activations and store them in a lookup table. During the fetch phase, encoded weight indices select the corresponding precomputed partial sums from the LUT, which are then accumulated to produce the output. This avoids multiplications entirely, replacing them with additions and MUX lookups.

The generator is parameterized by:

| Parameter | Code name | Description |
|-----------|-----------|-------------|
| LUT group size | `groupSize` (µ) | Number of activations grouped per LUT. Determines LUT depth: `(3^µ - 1)/2` entries. |
| Parallel LUTs | `nbLUTS` (L) | Number of LUT instances, each processing a distinct group of µ activations. |
| Parallel fetchers | `nbKeys` (K) | Number of simultaneous read-out MUXes per LUT. |
| Activation type | `bitWidth` / `fpType` | Data type for activations: integer (e.g., INT8) or floating-point (e.g., FP16). |

A workload of size `N × M` (weight matrix) is processed in tiles of size `n × m = (L · µ) × K` per cycle. When the tile is smaller than the workload, the controller iterates over multiple tiles and accumulates partial results.

## Getting Started

**Requirements:** Java 17, [sbt](https://www.scala-sbt.org/) 1.9+, Verilator (for tests).

```bash
git submodule init
sbt compile
sbt test
sbt "runMain ternarylut.EmitVerilog"
```


## Repository Structure

```
├── src/main/
│   ├── scala/ternarylut/
│   │   ├── EmitVerilog.scala              # Verilog emission entry point; run via sbt runMain
│   │   ├── lutgen/
│   │   │   ├── LUTGenUtil.scala           # Shared helpers and constants for LUT generation
│   │   │   ├── TernaryTruthTable.scala    # Ternary combination / truth-table definitions
│   │   │   ├── LUTGenComb.scala           # Combinational LUT generators (V1, V2, V3)
│   │   │   └── LUTGenPipelined.scala      # Pipelined LUT generators (V1, V2, V3)
│   │   └── matmul/
│   │       ├── MatMulStates.scala         # FSM state definitions for the controller
│   │       ├── MatMulController.scala     # INT controller FSM (idle → apply → shutdown)
│   │       ├── MatMulControllerFP.scala    # FP controller FSM (idle → apply → ramp-down → shutdown)
│   │       ├── MatMulMux.scala            # INT MUX: select LUT entry by encoded weight key
│   │       ├── MatMulMuxFP.scala          # FP MUX: select LUT entry by encoded weight key
│   │       ├── MatMulDatapathSplitted.scala   # INT datapath (LUT build + fetch + accumulate)
│   │       ├── MatMulDatapathSplittedFP.scala # FP datapath (pipelined LUT + fetch + accumulate)
│   │       ├── MatMulTop.scala            # INT top-level accelerator module
│   │       ├── MatMulTopFP.scala          # FP top-level accelerator module
│   │       └── TopMM.scala                # BlackBox wrapper for pre-synthesised MatMul Verilog (INT)
│   └── resources/
│       ├── lutgen_comb/                   # Pre-generated combinational LUT Verilog (V3, µ=3..7)
│       └── lutgen_pipelined/              # Pre-generated pipelined LUT Verilog (V3, µ=3..7)
├── src/test/scala/ternarylut/
│   ├── lutgen/
│   │   └── LutGenTester.scala             # Tests for all LUT gen variants (V1/V2/V3, comb/pipelined, INT/FP)
│   └── matmul/
│       ├── MatmulUtil.scala               # Golden-model reference and test helpers (key encode, ternary matmul)
│       ├── TopTesterINT.scala             # INT top-level integration test
│       └── TopTesterFP.scala              # FP top-level integration test
├── build.sbt                              
└── README.md                              # This file
```

## Architecture

The accelerator has two main parts:

1. **LUT Build** (`lutgen/`): An optimized adder tree computes all `(3^µ - 1)/2` ternary combinations of `µ` input activations. Three optimizations reduce the adder count (e.g., by up to 82% for µ=4):
   - **Symmetry reduction**: Only the positive half of combinations is stored; negatives are derived by sign-flip.
   - **Redundancy elimination**: Intermediate sub-expressions are shared across entries.
   - **Sparsity pruning**: Zero-weighted additions are eliminated entirely.

2. **Fetch & Accumulate** (`ternarylut/matmul/`): `K` multiplexers per LUT select entries using encoded weight indices. Outputs from `L` LUTs are summed via a reduction adder tree and accumulated in output registers.

## Module Hierarchy

**INT** (`MatMulTop`)

```
MatMulTop
├── MatMulController              # FSM
└── MatMulDatapathSplitted
    ├── LUTGenCombV1MuAll         × L      # LUT build (combinational + output reg)
    ├── MatMulMuxLut              × L × K  # Fetch: LUT entry by weight key
    └── MatMulMuxInverse          × L × K  # Sign flip for symmetric entries
```

**FP** (`MatMulTopFP`)

```
MatMulTopFP
├── MatMulControllerFP            # FSM
└── MatMulDatapathSplittedFP
    ├── LUTGenPipelinedV1FP       × L          # LUT build (pipelined, internal FP adders)
    ├── MatMulMuxLutFP            × L × K
    ├── MatMulMuxInverseFP        × L × K
    ├── FpAddFp                   × (L-1) × K  # Tree-reduce L LUT outputs per column (none if L = 1)
    └── FpAddFp                   × M_key      # Accumulate across tiles (only if workload spans >1 cycle)
```

## LUT Variants (V1, V2, V3)

Three implementation strategies for the LUT build-phase adder tree, differing in where optimization happens (Chisel vs. synthesis):

- **V1 — Manually optimized adder tree.** Chisel explicitly minimizes adders via symmetry, redundancy elimination, and sparsity pruning. The generated Verilog adder count closely matches the design; may be slightly above the theoretical minimum. **For FP, V1 is required:** FP adders are opaque to the synthesis tool, so Chisel-level minimization directly sets area and power.

- **V2 — Truth-table-based.** Each LUT entry is assigned as an explicit sum of its ternary combination; the RTL has many redundant additions. The synthesis tool (e.g. Cadence Genus) can eliminate these, so post-synthesis area/power can match or beat V1 for the same group size. Useful to compare Chisel vs. synthesis optimization.

- **V3 — Pre-generated Verilog (BlackBox).** Naive Verilog (e.g. from a parameterized script), wrapped as a Chisel BlackBox. Bypasses Chisel’s RTL generation and serves as a baseline to isolate synthesis-tool optimization in a three-way comparison.

Each variant exists in **combinational** and **pipelined** flavors. V1 also has **FP** variants using hardware FP adder modules (`FpAddFp`).


## Verification

| Component | Status |
|-----------|--------|
| INT LUT generators | Verified (V1/V2/V3) for supported µ ranges |
| FP LUT generators | Verified (small FP rounding differences) |
| INT top (MatMulTop) | Verified |
| FP top (MatMulTopFP) | Verified for `nbRegisters = 1` only; timing alignment for deeper pipelines not implemented |

**Note on FP top-level:** When using more than one pipeline register (`nbRegisters > 1`), the FP top-level produces incorrect results due to a timing alignment issue in the controller — an additional latency offset is needed to synchronize activation reads with the deeper pipeline stages. This was not implemented due to time constraints. However, this does not affect synthesis results for area and power: as long as the number of simulated clock cycles is sufficiently high, the VCD activity files provide representative switching activity regardless of functional correctness.

## Citation

If you use this work in your research, please cite:

```bibtex
@inproceedings{lutgen_ispass2026,
  title     = {{Hardware Generation and Exploration of Lookup Table-Based Accelerators for 1.58-bit LLM Inference}},
  author    = {Geens, Robin and Heldens, Joran and Dumoulin, Joren and Verhelst, Marian},
  booktitle = {2026 IEEE International Symposium on Performance Analysis of Systems and Software (ISPASS)},
  year      = {2026},
  address   = {Seoul, South Korea},
  month     = {April},
  publisher = {IEEE}
}
```
