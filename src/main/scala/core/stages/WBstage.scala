package core

import chisel3._
import chisel3.util._

import constants.Configurations._
import constants.Constants._

class WriteBackBusIO extends Bundle {
  val wen = Output(Bool())
  val waddr = Output(UInt(5.W))
  val wdata = Output(UInt(XLEN.W))
}