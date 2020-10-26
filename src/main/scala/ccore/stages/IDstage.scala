package ccore

import chisel3._
import chisel3.util._

import constants.Configurations._
import constants.Constants._
import Instructions._

class Ds2EsIO extends Bundle {
  val pc = Output(UInt(XLEN.W))
  val regDest = Output(UInt(5.W))
  val imm = Output(UInt(XLEN.W))
  val rs1_value = Output(UInt(XLEN.W))
  val rs2_value = Output(UInt(XLEN.W))
  val op1_sel = Output(UInt(2.W))
  val op2_sel = Output(UInt(3.W))
  val alu_op = Output(UInt(AluOpBits.W))
  val wb_sel = Output(UInt(2.W))
  val rf_wen = Output(Bool())
  val mem_rd = Output(Bool())
  val mem_wr = Output(Bool())
  val mem_msk = Output(UInt(3.W))
  val csr_type = Output(UInt(CSR.SZ))
}

class BranchBusIO extends Bundle {
  val taken = Output(Bool())
  val target = Output(UInt(XLEN.W))
}

class IDstageIO extends Bundle {
  val fs = Flipped(new DecoupledIO(new Fs2DsIO))
  val ds = new DecoupledIO(new Ds2EsIO)
  val br = new BranchBusIO
  val wb = Flipped(new WriteBackBusIO)
}

class IDstage extends Module {
  val io = IO(new IDstageIO)

  // ID stage
  val dsValid = RegEnable(next = io.fs.valid, init = false.B, enable = io.fs.ready)
  val dsReadyGo = true.B
  io.fs.ready := !dsValid || dsReadyGo && io.ds.ready
  io.ds.valid := dsValid && dsReadyGo
  val ds_r = RegEnable(next = io.fs.bits, enable = io.fs.valid && io.fs.ready)

  // instruction fields
  val rs1 = ds_r.inst(19, 15)
  val rs2 = ds_r.inst(24, 20)
  val rd = ds_r.inst(11, 7)

  // immediates
  val uimm = Cat(Fill(33, ds_r.inst(31)), ds_r.inst(30, 12), Fill(12, 0.U))

  // control signals
  val csignals =
    ListLookup(ds_r.inst,
                      List(N, BR_N, OP1_X,  OP2_X,    RFR_0, RFR_0, ALU_X,    WB_X,   RFW_0, MEMR_0, MEMW_0, MSK_X, CSR.N, N),
      Array(          /* val | BR | OP1   | OP2     | RS1   | RS2 | ALU     | WB    | RF    | MEM   | MEM   | MEM | CSR | fence
                       inst | type | sel  | sel     | read  | read |  op    | sel   | wen   | read  | wen   | mask | type | I */
        LUI       -> List(Y, BR_N,  OP1_X,  OP2_UIMM, RFR_0, RFR_0, ALU_COPY2,WB_ALU, RFW_1, MEMR_0, MEMW_0, MSK_X, CSR.N, N),
        AUIPC     -> List(Y, BR_N,  OP1_PC, OP2_UIMM, RFR_0, RFR_0, ALU_ADD,  WB_ALU, RFW_1, MEMR_0, MEMW_0, MSK_X, CSR.N, N)
      ))

  val (val_inst: Bool) :: br_type :: op1_sel :: op2_sel :: (rs1_read: Bool) :: (rs2_read: Bool) :: (alu_op) :: cs0 = csignals
  val wb_sel :: (rf_wen: Bool) :: (mem_rd: Bool) :: (mem_wr: Bool) :: mem_msk :: csr_type :: (fence_i: Bool) :: Nil = cs0

  val pc_sel = PC_4

  // RegFile
  val rf = Module(new RegFile)
  rf.io.wen := io.wb.wen
  rf.io.waddr := io.wb.waddr
  rf.io.wdata := io.wb.wdata
  rf.io.raddr(0) := rs1
  rf.io.raddr(1) := rs2

  val rs1_value = rf.io.rdata(0)
  val rs2_value = rf.io.rdata(1)

  val rs1_eq_rs2 = (rs1_value === rs2_value)

  // TODO
  io.br.taken := false.B
  io.br.target := DontCare

  io.ds.bits.pc := ds_r.pc
  io.ds.bits.regDest := rd
  io.ds.bits.imm := MuxCase(DontCare, Array(
    (op2_sel === OP2_UIMM)  -> uimm
  ))
  io.ds.bits.rs1_value := rs1_value
  io.ds.bits.rs2_value := rs2_value
  io.ds.bits.op1_sel := op1_sel
  io.ds.bits.op2_sel := op2_sel
  io.ds.bits.alu_op := alu_op
  io.ds.bits.wb_sel := wb_sel
  io.ds.bits.rf_wen := rf_wen
  io.ds.bits.mem_rd := mem_rd
  io.ds.bits.mem_wr := mem_wr
  io.ds.bits.mem_msk := mem_msk
  io.ds.bits.csr_type := csr_type
}