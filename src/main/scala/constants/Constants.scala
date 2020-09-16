package constants

import chisel3._

object Constants extends ALUConstants
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
}
