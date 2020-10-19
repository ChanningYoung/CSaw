package ccore

import chisel3._
import constants.Configurations._

class RegFile extends Module {
  val io = IO(new Bundle {
    val wen = Input(Bool())
    val waddr = Input(UInt(5.W))
    val wdata = Input(UInt(XLEN.W))
    val raddr = Vec(2, Input(UInt(5.W)))
    val rdata = Vec(2, Output(UInt(XLEN.W)))
  })

  val r = Mem(32, UInt(XLEN.W))

  when (io.wen) {
    r(io.waddr) := io.wdata
  }
  (io.raddr zip io.rdata).map {
    case (a, d) => d := Mux(a =/= 0.U, r(a), 0.U(XLEN.W))   // r(0) gives 0
  }
}
