package it.alian.gun.mesmerize

import java.util.{HashMap => JMap}

import it.alian.gun.mesmerize.MesmerizeDelegate._
import it.alian.gun.mesmerize.scalaapi.Prelude.{config, _}

import scala.collection.generic.FilterMonadic
import scala.collection.mutable

package object lore {

  object Ticker {
    var tick: Long = 0

    runTask(0, 1)(tick += 1)
  }

  class Info private[lore](map: mutable.Map[String, Either[Between, String]]) {

    private var lastTick: Long = Ticker.tick

    private val calculatedNums = new JMap[String, Double](32)

    def apply(x: String): Either[Between, String] = map(x)

    def update(x: String, v: Either[Between, String]): Unit = map(x) = v

    def num(x: String): Double = {
      if (lastTick != Ticker.tick) {
        lastTick = Ticker.tick
        calculatedNums.clear()
      }
      calculatedNums.putIfAbsent(x, map(x).left.get.random)
      calculatedNums.get(x)
    }

    def str(x: String): String = map(x).right.get

    def has(key: String): Boolean = map.contains(key)

    def foreach[U](f: ((String, Either[Between, String])) => U): Unit = map.foreach[U](f)

    def withFilter(p: ((String, Either[Between, String])) => Boolean): FilterMonadic[(String, Either[Between, String]), mutable.Map[String, Either[Between, String]]] = map.withFilter(p)

    override def toString: String = map.toString()
  }

  type LoreInfo = Info

  def newInfo: LoreInfo = new Info(mutable.Map[String, Either[Between, String]]()
    .withDefault(name => config(s"prefix.$name.type", "number") match {
      case "number" => config(s"prefix.$name.collect", "sum") match {
        case "sum" => Left(0)
        case "max" => Left(Double.MinValue)
        case "min" => Left(Double.MaxValue)
        case "replace" => Left(0)
      }
      case "string" => Right(config(s"prefix.$name.default", ""))
    }))
}

case class Between(lo: Double, hi: Double) {
  def this(x: Double) = this(x, x)

  def +(x: Between): Between = Between(lo + x.lo, hi + x.hi)

  def max(x: Between): Between = Between(lo max x.lo, hi max x.hi)

  def min(x: Between): Between = Between(lo min x.lo, hi min x.hi)

  override def toString: String = "%.2f - %.2f".format(lo, hi)

  def random: Double = math.random() * (hi - lo) + lo
}

case object Between {
  implicit def double2range(x: Double): Between = Between(x, x)

  implicit def int2range(x: Int): Between = Between(x, x)
}
