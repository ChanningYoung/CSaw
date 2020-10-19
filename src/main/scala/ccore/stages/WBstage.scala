package ccore

import chisel3._
import chisel3.util._

import constants.Configurations._
import constants.Constants._

class WriteBackBusIO extends Bundle {
  val wen = Output(Bool())
  val waddr = Output(UInt(5.W))
  val wdata = Output(UInt(XLEN.W))
}

class WBstageIO extends Bundle {
  val ms = Flipped(new DecoupledIO(new Ms2WsIO))
  val wb = new WriteBackBusIO
}

class WBstage extends Module {
  val io = IO(new WBstageIO)

  // WB stage
  val wsValid = RegEnable(next = io.ms.valid, init = false.B, enable = io.ms.ready)
  val wsReadyGo = true.B
  io.ms.ready := !wsValid || wsReadyGo
  val ws_r = RegEnable(next = io.ms.bits, enable = io.ms.valid && io.ms.ready)

  io.wb.wen := wsValid && ws_r.rf_wen
  io.wb.waddr := ws_r.regDest
  io.wb.wdata := ws_r.finalResult
}