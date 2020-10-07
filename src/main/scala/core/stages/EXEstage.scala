package core

import chisel3._
import chisel3.util._

import constants.Configurations._
import constants.Constants._
import memory._

class Es2MsIO extends Bundle {
  val pc = Output(UInt(XLEN.W))
  val regDest = Output(UInt(5.W))
  val aluResult = Output(UInt(XLEN.W))
  val wb_sel = Output(UInt(2.W))
  val rf_wen = Output(Bool())
  val mem_rd = Output(Bool())
  val mem_wr = Output(Bool())
  val mem_msk = Output(UInt(3.W))
  val csr_type = Output(UInt(CSR.SZ))
}

class EXEstageIO extends Bundle {
  val ds = Flipped(new DecoupledIO(new Ds2EsIO))
  val es = new DecoupledIO(new Es2MsIO)
  val dmem = new DecoupledIO(new MemReq)
}

class EXEstage extends Module {
  val io = IO(new EXEstageIO)

  // EXE stage
  val esValid = RegEnable(next = io.ds.valid, init = false.B, enable = io.ds.ready)
  val esReadyGo = true.B
  io.ds.ready := !esValid || esReadyGo && io.es.ready
  io.es.valid := esValid && esReadyGo
  val es_r = RegEnable(next = io.ds.bits, enable = io.ds.valid && io.ds.ready)

  val aluSrc1 = MuxCase(DontCare, Array(
    (es_r.op1_sel === OP1_RS1)  -> es_r.rs1_value,
    (es_r.op1_sel === OP1_PC)   -> es_r.pc,
    (es_r.op1_sel === OP1_IMZ)  -> es_r.imm
  ))
  val aluSrc2 = MuxCase(DontCare, Array(
    (es_r.op2_sel === OP2_RS2)  -> es_r.rs2_value,
    (es_r.op2_sel === OP2_IIMM) -> es_r.imm,
    (es_r.op2_sel === OP2_SIMM) -> es_r.imm,
    (es_r.op2_sel === OP2_BIMM) -> es_r.imm,
    (es_r.op2_sel === OP2_UIMM) -> es_r.imm,
    (es_r.op2_sel === OP2_JIMM) -> es_r.imm
  ))

  // ALU
  val alu = Module(new ALU)
  alu.io.src1 := aluSrc1
  alu.io.src2 := aluSrc2
  alu.io.op := es_r.alu_op
  val aluResult = alu.io.out

  // Data MEM
  io.dmem.valid := es_r.mem_rd || es_r.mem_wr
  /* TODO */
  io.dmem.bits.wen := false.B
  io.dmem.bits.addr := aluResult
  /* TODO */
  io.dmem.bits.wdata := DontCare

  io.es.bits.pc := es_r.pc
  io.es.bits.regDest := es_r.regDest
  io.es.bits.aluResult := aluResult
  io.es.bits.wb_sel := es_r.wb_sel
  io.es.bits.rf_wen := es_r.rf_wen
  io.es.bits.mem_rd := es_r.mem_rd
  io.es.bits.mem_wr := es_r.mem_wr
  io.es.bits.mem_msk := es_r.mem_msk
  io.es.bits.csr_type := es_r.csr_type
}
