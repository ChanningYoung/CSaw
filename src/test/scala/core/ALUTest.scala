package core

import java.sql.Driver

import chisel3.iotesters
import chisel3.iotesters.PeekPokeTester
import chisel3.stage.ChiselStage

import scala.util.Random

object AluElaborate extends App {
  (new ChiselStage).execute(
    args, Seq(chisel3.stage.ChiselGeneratorAnnotation(() => new ALU))
  )
}
