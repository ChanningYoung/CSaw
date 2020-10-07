package constants

import chisel3._

import Configurations._

object Constants extends ALUConstants
  with PrivilegedConstants
  with MemoryOpConstants
  with DecoderConstants
{
}

trait RISCVConstants {
}

trait ALUConstants {
  val AluOpBits = 4
  // Operation Signal
  val ALU_ADD   = 0.U(AluOpBits.W)
  val ALU_SUB   = 1.U(AluOpBits.W)
  val ALU_SLL   = 2.U(AluOpBits.W)
  val ALU_SRL   = 3.U(AluOpBits.W)
  val ALU_SRA   = 4.U(AluOpBits.W)
  val ALU_AND   = 5.U(AluOpBits.W)
  val ALU_OR    = 6.U(AluOpBits.W)
  val ALU_XOR   = 7.U(AluOpBits.W)
  val ALU_SLT   = 8.U(AluOpBits.W)
  val ALU_SLTU  = 9.U(AluOpBits.W)

  val ALU_COPY1 = 10.U(AluOpBits.W)
  val ALU_COPY2 = 11.U(AluOpBits.W)
  // For decoder
  val ALU_X     = 0.U(AluOpBits.W)
}

trait PrivilegedConstants {
  val START_ADDR = 0x8000000.U(XLEN.W)
}

trait MemoryOpConstants {
  // codes for wen
  val MW_LD = 0x00.U(XBytes.U)
}

trait DecoderConstants {
  val Y = true.B
  val N = false.B

  // PC select
  val PC_4      = 0.U(2.W)
  val PC_BRJMP  = 1.U(2.W)
  val PC_JALR   = 2.U(2.W)
  val PC_EXC    = 3.U(2.W)

  // Branch type
  val BR_N    = 0.U(4.W)
  val BR_EQ   = 1.U(4.W)
  val BR_NE   = 2.U(4.W)
  val BR_LT   = 3.U(4.W)
  val BR_GE   = 4.U(4.W)
  val BR_LTU  = 5.U(4.W)
  val BR_GEU  = 6.U(4.W)
  val BR_J    = 7.U(4.W)
  val BR_JR   = 8.U(4.W)

  // ALU Operand 1 select
  val OP1_RS1 = 0.U(2.W)
  val OP1_PC  = 1.U(2.W)
  val OP1_IMZ = 2.U(2.W)
  val OP1_X   = 0.U(2.W)

  // ALU Operand 2 select
  val OP2_RS2   = 0.U(3.W)
  val OP2_IIMM  = 1.U(3.W)
  val OP2_SIMM  = 2.U(3.W)
  val OP2_BIMM  = 3.U(3.W)
  val OP2_UIMM  = 4.U(3.W)
  val OP2_JIMM  = 5.U(3.W)
  val OP2_X     = 0.U(3.W)

  // Read register (for detecting hazards)
  val RFR_0 = false.B
  val RFR_1 = true.B

  // RegFile write enable
  val RFW_0 = false.B
  val RFW_1 = true.B

  // ALU Operation signals are included in ALUConstants

  // Writeback select
  val WB_ALU  = 0.U(2.W)
  val WB_MEM  = 1.U(2.W)
  val WB_PC4  = 2.U(2.W)
  val WB_CSR  = 3.U(2.W)
  val WB_X    = 0.U(2.W)

  // Memory write
  val MEMW_0 = false.B
  val MEMW_1 = true.B

  // Memory read
  val MEMR_0 = false.B
  val MEMR_1 = true.B

  // Memory mask
  val MSK_B   = 0.U(3.W)
  val MSK_BU  = 1.U(3.W)
  val MSK_H   = 2.U(3.W)
  val MSK_HU  = 3.U(3.W)
  val MSK_W   = 4.U(3.W)
  val MSK_WU  = 5.U(3.W)
  val MSK_D   = 6.U(3.W)
  val MSK_X   = 6.U(3.W)
}
