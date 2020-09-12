package core

import chisel3.stage.ChiselStage

object RegFileElaborate extends App {
  (new ChiselStage).execute(
    args, Seq(chisel3.stage.ChiselGeneratorAnnotation(() => new RegFile))
  )
}
