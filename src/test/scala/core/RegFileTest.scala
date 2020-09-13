package core

import java.sql.Driver

import chisel3.iotesters
import chisel3.iotesters.PeekPokeTester
import chisel3.stage.ChiselStage

import scala.util.Random

object RegFileElaborate extends App {
  (new ChiselStage).execute(
    args, Seq(chisel3.stage.ChiselGeneratorAnnotation(() => new RegFile))
  )
}

class RegFileUnitTester(c: RegFile) extends PeekPokeTester(c) {
  // software regfile
  var gold = new Array[Long](32)
  def updateGold(en: Boolean, addr: Int, data: Long) : Unit = {
    if (en) gold(addr) = data
  }
  def readGold(addr: Int) : Long = {
    if (addr != 0) gold(addr)
    else 0
  }

  private val rf = c
  // Initialize RegFile
  for (i <- 1 until 32) {
    val data = Random.nextLong()
    poke(rf.io.wen, 1)
    poke(rf.io.waddr, i)
    poke(rf.io.wdata, data)
    step(1)
    updateGold(true, i, data)
  }

  // testing
  var raddr = new Array[Int](2)
  for (_ <- 1 to 50) {
    val wen = Random.nextBoolean()
    val waddr = Random.nextInt(32)
    val wdata = Random.nextLong()
    for (i <- 0 until 2) {
      raddr.update(i, Random.nextInt(32))
    }
    poke(rf.io.wen, wen.toInt)
    poke(rf.io.waddr, waddr)
    poke(rf.io.wdata, wdata)
    (rf.io.raddr zip raddr).map {
      case (sig, addr) => poke(sig, addr)
    }

    /*  Scala has no unsigned long, so we have to convert
        the peeked value to long
     */
    (rf.io.rdata zip raddr).map {
      case (sig, addr) => {
        assert(peek(sig).toLong == readGold(addr))
      }
    }
    step(1)
    updateGold(wen, waddr, wdata)
  }
}

object RegFileMain extends App {
  iotesters.Driver.execute(args, () => new RegFile) {
    c => new RegFileUnitTester(c)
  }
}
