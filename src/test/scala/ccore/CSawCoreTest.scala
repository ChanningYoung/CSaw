package ccore

import chisel3.iotesters
import chisel3.iotesters.PeekPokeTester
import chisel3.stage.ChiselStage

import constants.Configurations._
import constants.Constants._

import scala.util.Random

class CSawCoreUnitTester(c: CSawCore) extends PeekPokeTester(c) {
  // generate a random LUI instruction
  def genLUI() : Long = {
    val imm = Random.nextInt(1 << 20)
    val rd = Random.nextInt(32)
    val lui = 0x37  // 0110111
    imm.toLong << 12 | rd << 7 | lui
  }

  private val cs = c
  poke(cs.io.imem.req.ready, 1)
  poke(cs.io.imem.resp.valid, 1)
  for (_ <- 0 until 50) {
    poke(cs.io.imem.resp.bits.rdata, genLUI())
    step(1)
  }
}

object CSawCoreMain extends App {
  iotesters.Driver.execute(args, () => new CSawCore) {
    c => new CSawCoreUnitTester(c)
  }
}
