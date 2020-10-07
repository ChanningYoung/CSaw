package core

import chisel3._
import chisel3.util._

import constants.Configurations._
import constants.Constants._
import memory._

class Ms2WsIO extends Bundle {
  val pc = Output(UInt(XLEN.W))
  val regDest = Output(UInt(5.W))
  val finalResult = Output(UInt(XLEN.W))
  val rf_wen = Output(Bool())
  val csr_type = Output(UInt(CSR.SZ))
}

class MEMstageIO extends Bundle {
  val es = Flipped(new DecoupledIO(new Es2MsIO))
  val ms = new DecoupledIO(new Ms2WsIO)
  val dmem = Flipped(new ValidIO(new MemResp))
}

class MEMstage extends Module {
  val io = IO(new MEMstageIO)

  // MEM stage
  val msValid = RegEnable(next = io.es.valid, init = false.B, enable = io.es.ready)
  val msReadyGo = true.B
  io.es.ready := !msValid || msReadyGo && io.ms.ready
  io.ms.valid := msValid && msReadyGo
  val ms_r = RegEnable(next = io.es.bits, enable = io.es.valid && io.es.ready)

  // TODO: dmem.valid
  val memResult = io.dmem.bits.rdata

  val msFinalRes = MuxCase(DontCare, Array(
    (ms_r.wb_sel === WB_ALU)  -> ms_r.aluResult,
    /* TODO */
    (ms_r.wb_sel === WB_PC4)  -> DontCare,
    (ms_r.wb_sel === WB_MEM)  -> memResult,
    /* TODO */
    (ms_r.wb_sel === WB_CSR)  -> DontCare
  ))

  io.ms.bits.pc := ms_r.pc
  io.ms.bits.regDest := ms_r.regDest
  io.ms.bits.finalResult := msFinalRes
  io.ms.bits.rf_wen := ms_r.rf_wen
  io.ms.bits.csr_type := ms_r.csr_type
}
