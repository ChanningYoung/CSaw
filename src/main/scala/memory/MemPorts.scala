package memory

import chisel3._
import chisel3.util._

import constants.Configurations._
import constants.Constants._

class MemPortIO extends Bundle {
  val req = new DecoupledIO(new MemReq)
  val resp = Flipped(new DecoupledIO(new MemResp))
}

class MemReq extends Bundle {
  val addr = Output(UInt(XLEN.W))
  val wdata = Output(UInt(XLEN.W))
  val func = Output(UInt(MemFuncBits.W))
  val typ = Output(UInt(1.W))
}

class MemResp extends Bundle {
  val rdata = Output(UInt(XLEN.W))
}