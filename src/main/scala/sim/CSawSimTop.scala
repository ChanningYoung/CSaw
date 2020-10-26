package sim

import chisel3._
import chisel3.stage.ChiselStage
import chisel3.util._
import constants.Configurations._
import constants.Constants._
import memory._
import ccore._

class CSawSimTopIO extends Bundle {
  val ccore = new CSawCoreIO
}

class CSawSimTop extends Module {
  val io = IO(new CSawSimTopIO)

  val csCore = Module(new CSawCore)
  io.ccore <> csCore.io
}

object CSawSimTopElaborate extends App {
  (new ChiselStage).execute(
    args, Seq(stage.ChiselGeneratorAnnotation(() => new CSawSimTop))
  )
}
