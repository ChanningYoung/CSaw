package core.stages

import chisel3._
import chisel3.util._

import constants.Configurations._
import constants.Constants._

class Ds2EsIO extends Bundle {

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

  val fs_pc = io.fs.bits.pc

  // ID stage
  val dsValid = RegEnable(next = io.fs.valid, init = false.B, enable = io.fs.ready)
  val dsReadyGo = true.B
  io.fs.ready := !dsValid || dsReadyGo && io.ds.ready
  io.ds.valid := dsValid && dsReadyGo
  val ds_r = RegEnable(next = io.fs.bits, enable = io.fs.valid && io.fs.ready)

}