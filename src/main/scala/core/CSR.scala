package core

import chisel3._

object CSR {
  // size of decoder info
  val SZ = 3.W

  val N = 0.U(SZ)
  val W = 1.U(SZ)
  val S = 2.U(SZ)
  val C = 3.U(SZ)
  val I = 4.U(SZ)
  val R = 5.U(SZ)
}
