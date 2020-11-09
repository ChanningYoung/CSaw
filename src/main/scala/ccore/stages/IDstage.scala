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
  val iimm = Cat(Fill(XLEN-11, ds_r.inst(31)), ds_r.inst(30, 20))
  val bimm = Cat(Fill(XLEN-12, ds_r.inst(31)), ds_r.inst(7), ds_r.inst(30, 25), ds_r.inst(11, 8), Fill(1, 0.U))
  val uimm = Cat(Fill(XLEN-31, ds_r.inst(31)), ds_r.inst(30, 12), Fill(12, 0.U))
  val jimm = Cat(Fill(XLEN-20, ds_r.inst(31)), ds_r.inst(19, 12), ds_r.inst(20), ds_r.inst(30, 21), Fill(1, 0.U))

  // control signals
  val csignals =
    ListLookup(ds_r.inst,
                      List(N, BR_N, OP1_X,  OP2_X,    RFR_0, RFR_0, ALU_X,    WB_X,   RFW_0, MEMR_0, MEMW_0, MSK_X, CSR.N, N),
      Array(          /* val | BR | OP1   | OP2     | RS1   | RS2 | ALU     | WB    | RF    | MEM   | MEM   | MEM | CSR | fence
                       inst | type | sel  | sel     | read  | read |  op    | sel   | wen   | read  | wen   | mask | type | I */
        LUI       -> List(Y, BR_N,  OP1_X,  OP2_UIMM, RFR_0, RFR_0, ALU_COPY2,WB_ALU, RFW_1, MEMR_0, MEMW_0, MSK_X, CSR.N, N),
        AUIPC     -> List(Y, BR_N,  OP1_PC, OP2_UIMM, RFR_0, RFR_0, ALU_ADD,  WB_ALU, RFW_1, MEMR_0, MEMW_0, MSK_X, CSR.N, N),

        JAL       -> List(Y, BR_J,  OP1_PC, OP2_LINK, RFR_0, RFR_0, ALU_ADD,  WB_ALU, RFW_1, MEMR_0, MEMW_0, MSK_X, CSR.N, N),
        JALR      -> List(Y, BR_JR, OP1_PC, OP2_LINK, RFR_1, RFR_0, ALU_ADD,  WB_ALU, RFW_1, MEMR_0, MEMW_0, MSK_X, CSR.N, N),

        BEQ       -> List(Y, BR_EQ, OP1_X,  OP2_X,    RFR_1, RFR_1, ALU_X,    WB_X,   RFW_0, MEMR_0, MEMW_0, MSK_X, CSR.N, N),
        BNE       -> List(Y, BR_NE, OP1_X,  OP2_X,    RFR_1, RFR_1, ALU_X,    WB_X,   RFW_0, MEMR_0, MEMW_0, MSK_X, CSR.N, N),
        BLT       -> List(Y, BR_LT, OP1_X,  OP2_X,    RFR_1, RFR_1, ALU_X,    WB_X,   RFW_0, MEMR_0, MEMW_0, MSK_X, CSR.N, N),
        BGE       -> List(Y, BR_GE, OP1_X,  OP2_X,    RFR_1, RFR_1, ALU_X,    WB_X,   RFW_0, MEMR_0, MEMW_0, MSK_X, CSR.N, N),
        BLTU      -> List(Y, BR_LTU,OP1_X,  OP2_X,    RFR_1, RFR_1, ALU_X,    WB_X,   RFW_0, MEMR_0, MEMW_0, MSK_X, CSR.N, N),
        BGEU      -> List(Y, BR_GEU,OP1_X,  OP2_X,    RFR_1, RFR_1, ALU_X,    WB_X,   RFW_0, MEMR_0, MEMW_0, MSK_X, CSR.N, N),

        LD        -> List(Y, BR_N,  OP1_RS1,OP2_IIMM, RFR_1, RFR_0, ALU_ADD,  WB_MEM, RFW_1, MEMR_1, MEMW_0, MSK_D, CSR.N, N)
      ))

  val (val_inst: Bool) :: br_type :: op1_sel :: op2_sel :: (rs1_read: Bool) :: (rs2_read: Bool) :: (alu_op) :: cs0 = csignals
  val wb_sel :: (rf_wen: Bool) :: (mem_rd: Bool) :: (mem_wr: Bool) :: mem_msk :: csr_type :: (fence_i: Bool) :: Nil = cs0

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
  // Branch control
  io.br.taken := dsValid && (
    (br_type === BR_J) || (br_type === BR_JR)
    || (br_type === BR_EQ && rs1_value === rs2_value)
    || (br_type === BR_NE && rs1_value =/= rs2_value)
    || (br_type === BR_LT && rs1_value.asSInt() < rs2_value.asSInt())
    || (br_type === BR_GE && rs1_value.asSInt() >= rs2_value.asSInt())
    || (br_type === BR_LTU && rs1_value < rs2_value)
    || (br_type === BR_GEU && rs1_value >= rs2_value)
    )

  val jalr_target = rs1_value + iimm
  val br_target = ds_r.pc + bimm
  io.br.target := MuxCase(DontCare, Array(
    (br_type === BR_J)  -> (ds_r.pc + jimm),
    (br_type === BR_JR) -> Cat(jalr_target(XLEN-1, 1), Fill(1, 0.U)),
    (br_type === BR_EQ) -> br_target,
    (br_type === BR_NE) -> br_target,
    (br_type === BR_LT) -> br_target,
    (br_type === BR_GE) -> br_target,
    (br_type === BR_LTU)-> br_target,
    (br_type === BR_GEU)-> br_target
  ))

  // stage output to exe stage
  io.ds.bits.pc := ds_r.pc
  io.ds.bits.regDest := rd
  io.ds.bits.imm := MuxCase(DontCare, Array(
    (op2_sel === OP2_IIMM)  -> iimm,
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