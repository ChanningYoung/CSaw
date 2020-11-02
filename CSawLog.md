# 2020/9/12

* Verilator Note：From Verilog to C++

- Chisel Note：
    - Generate Verilog with SBT
    - Run Scala testbench 

# 2020/9/13

* Implement `RegFile` with Scala testbench

# 2020/9/16

* Implement `ALU` with C++ testbench (run with `make aluTest`)

* Integrated `RegFile` test in `Makefile` (`make regfileTest`)

- Update Verilator Note: Using absolute path

# 2020/10/4

- Starting 5-stage pipeline
    - Simple instruction fetch stage
    - Instruction listing of RV64I

# 2020/10/7

- Start implementing decoder

- 5-stage pipeline:
    - Instruction Decode stage
    - Execution stage

# 2020/10/8

- Finished 5-stage pipeline: Memory & Writeback stage

# 2020/10/10

- Completed initial 5-stage core with LUI

- Implemented scala test of LUI and verified with waveform

# 2020/11/2

- Implemented unconditional jump inst JAL & JALR
    - Implemented logic to clear IF stage when wrong inst is fetched because of branching

