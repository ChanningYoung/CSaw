package ccore

import chisel3._
import chisel3.util._

import constants.Configurations._
import constants.Constants._
import memory._

class CSawCoreIO extends Bundle {
  val imem = new MemPortIO
  val dmem = new MemPortIO
}

class CSawCore extends Module {
  val io = IO(new CSawCoreIO)

  val ifStage = Module(new IFstage)
  val idStage = Module(new IDstage)
  val exeStage = Module(new EXEstage)
  val memStage = Module(new MEMstage)
  val wbStage = Module(new WBstage)

  // pipeline
  idStage.io.fs <> ifStage.io.fs
  exeStage.io.ds <> idStage.io.ds
  memStage.io.es <> exeStage.io.es
  wbStage.io.ms <> memStage.io.ms

  // other inter-stage connections
  ifStage.io.br := idStage.io.br

  idStage.io.wb := wbStage.io.wb

  // mem
  io.imem <> ifStage.io.imem
  io.dmem.req <> exeStage.io.dmem
  memStage.io.dmem := io.dmem.resp
}
