package ccore

import chisel3._
import chisel3.util._

import constants.Configurations._
import constants.Constants._
import memory._

class Fs2DsIO extends Bundle {
  val inst = Output(UInt(ILEN.W))
  val pc = Output(UInt(XLEN.W))
}

class IFstageIO extends Bundle {
  val fs = new DecoupledIO(new Fs2DsIO)
  val br = Flipped(new BranchBusIO)
  val imem = new MemPortIO
}

class IFstage extends Module {
  val io = IO(new IFstageIO)

  // trick: to make nextpc be START_ADDR during reset
  val fs_pc_reg = RegInit(START_ADDR - InsBytes.U)

  // pre-IF stage
  val toFsValid = RegNext(next = true.B, init = false.B)
  val seq_pc = fs_pc_reg + 4.U(XLEN.W)
  val nextpc = Mux(io.br.taken, io.br.target, seq_pc)

  // IF stage
  val fsAllowIn = Wire(Bool())
  val fsValid = RegEnable(next = toFsValid, init = false.B, enable = fsAllowIn)
  val fsReadyGo = true.B
  fsAllowIn := !fsValid || fsReadyGo && io.fs.ready
  io.fs.valid := fsValid && fsReadyGo
  when (toFsValid && fsAllowIn) {
    fs_pc_reg := nextpc
  }

  // Inst mem
  io.imem.req.valid := toFsValid && fsAllowIn
  io.imem.req.bits.wen := MW_LD
  io.imem.req.bits.addr := nextpc
  io.imem.req.bits.wdata := DontCare

  // fs -> ds
  /* TODO: response valid */
  io.fs.bits.inst := io.imem.resp.bits.rdata
  io.fs.bits.pc := fs_pc_reg

  /* TODO: clear IF stage if branched */
}
