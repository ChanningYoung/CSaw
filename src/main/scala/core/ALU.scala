package core

import chisel3._
import chisel3.stage.ChiselStage
import chisel3.util._
import constants.Configurations._
import constants.Constants._

class ALU extends Module {
  val io = IO(new Bundle {
    val src1 = Input(UInt(XLEN.W))
    val src2 = Input(UInt(XLEN.W))
    val op = Input(UInt(AluOpBits.W))
    val out = Output(UInt(XLEN.W))
  })

  val (src1, src2, op) = (io.src1, io.src2, io.op)

  val shamt = src2(ShamtBits-1, 0)

  // Set invert and carryIn for subtraction
  val subSrc2 = (op === ALU_SUB) || (op === ALU_SLT) || (op === ALU_SLTU)
  val adderSrc2 = Mux(subSrc2, ~src2, src2)
  // Adder (add or sub) result for XLEN - 1 bits (and carry)
  val adderLow = src1(XLEN-2, 0) +& adderSrc2(XLEN-2, 0) + Mux(subSrc2, 1.U, 0.U)
  // Adder result for highest bit (and carry)
  val adderHigh = src1(XLEN-1) +& adderSrc2(XLEN-1) + adderLow(XLEN-1)
  val resultAdder = Cat(adderHigh(0), adderLow(XLEN-2, 0))

  // For signed int
  val overflow = adderHigh(1) ^ adderLow(XLEN-1)
  // For unsigned int
  val carryout = adderHigh(1) ^ subSrc2.asUInt()

  io.out := MuxCase(DontCare, Array(
    (op === ALU_ADD)  -> resultAdder,
    (op === ALU_SUB)  -> resultAdder,
    (op === ALU_SLL)  -> (src1 << shamt),
    (op === ALU_SRL)  -> (src1 >> shamt),
    (op === ALU_SRA)  -> (src1.asSInt() >> shamt).asUInt(),
    (op === ALU_AND)  -> (src1 & src2),
    (op === ALU_OR)   -> (src1 | src2),
    (op === ALU_XOR)  -> (src1 ^ src2),
    (op === ALU_SLT)  -> (resultAdder(XLEN-1) ^ overflow),
    (op === ALU_SLTU) -> carryout
  ))
}

object AluElaborate extends App {
  (new ChiselStage).execute(
    args, Seq(stage.ChiselGeneratorAnnotation(() => new ALU))
  )
}
