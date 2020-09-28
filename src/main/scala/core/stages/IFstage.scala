package core.stages

import chisel3._
import chisel3.util._

import constants.Configurations._
import memory._

class Fs2DsIO extends Bundle {
  val inst = Output(UInt(ILEN.W))
  val pc = Output(UInt(XLEN.W))
}

class IFstageIO extends Bundle {
  val fs = new DecoupledIO(new Fs2DsIO)
  val imem = new MemPortIO
}

class IFstage extends Module {
  val io = IO(new IFstageIO)

  val fsValid = RegInit(false.B)
  val fsReadyGo = true.B
  val fsAllowIn = !fsValid || fsReadyGo || io.fs.ready
}
