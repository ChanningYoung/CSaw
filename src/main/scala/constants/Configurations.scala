package constants

object Configurations extends RISCVConf
{
}

trait RISCVConf {
  val XLEN = 64
  val IALIGN = 32 // instruction-address alignment
  val InsBytes = IALIGN / 4
  val ILEN = 32 // maximum instruction length

  val ShamtBits = 6
}