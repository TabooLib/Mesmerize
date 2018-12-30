package it.alian.gun.mesmerize.lore

import java.util.concurrent.TimeUnit

import com.google.common.cache.{Cache, CacheBuilder}
import it.alian.gun.mesmerize.Between
import it.alian.gun.mesmerize.scalaapi.Prelude._
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.{LivingEntity, Player}
import org.bukkit.inventory.ItemStack

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.util.matching.Regex

object LoreParser {

  private val name = """(?<name>[a-zA-Z0-9_\u4e00-\u9fa5]+)"""
  private val range = """(?<low>[\+\-]?\d+(\.\d+)?)(\s?\-\s?(?<high>[\+\-]?\d+(\.\d+)?))?"""
  private val str = """(?<str>.*)"""
  private val pattern = f"$name\\s?:\\s*($range|$str)".r

  lazy val names: Map[String, String] = {
    val res = mutable.Map[String, String]()
    val section = config[ConfigurationSection]("prefix", null)
    for (key: String <- section.getKeys(false).asScala) {
      res += section.getConfigurationSection(key).getString("name") -> key
    }
    Map() ++ res
  }

  private val cache: Cache[Int, LoreInfo] = CacheBuilder.newBuilder().asInstanceOf[CacheBuilder[Int, LoreInfo]]
    .concurrencyLevel(2).expireAfterWrite(60, TimeUnit.SECONDS).build()

  def equipments(player: Player): Seq[ItemStack] = {
    val res = ArrayBuffer[ItemStack]()
    res ++ player.getEquipment.getArmorContents
    try {
      res += player.getEquipment.getItemInMainHand
      res += player.getEquipment.getItemInOffHand
    } catch {
      case _: Throwable => res += player.getItemInHand
    }
    res
  }

  def update(e: LivingEntity): LoreInfo = {
    cache.invalidate(e.getEntityId)
    parse(e)
  }

  def parse(e: LivingEntity): LoreInfo = cache.get(e.getEntityId, () =>
    e match {
      case player: Player =>
        val equips = equipments(player)
        val res = parse(equips.head)
        for (item <- equips.tail;
             info = parse(item);
             (k, v) <- info)
          res(k) = merge(k, res(k), v)
        res
      case _ => newInfo
    })


  private def merge(name: String, a: Either[Between, String], b: Either[Between, String]): Either[Between, String] = {
    (a, b) match {
      case (Left(x), Left(y)) => config(s"prefix.$name.collect", "sum") match {
        case "sum" => Left(x + y)
        case "max" => Left(x max y)
        case "min" => Left(x min y)
        case "replace" => b
        case _ => Left(0)
      }
      case (Right(x), Right(y)) => config[String](s"prefix.$name.collect") match {
        case "replace" => b
        case _ => Right("")
      }
      case _ => b
    }
  }

  def parse(item: ItemStack): LoreInfo = {

    def rangeOf(reg: Regex.Match): Between = {
      val high = reg.group("high")
      val low = reg.group("low")
      if (high != null) Between(low.toDouble, high.toDouble)
      else if (low != null) low.toDouble
      else 0
    }

    val info = newInfo
    if (item.hasLore) {
      for (lore <- item.getLore;
           res <- pattern.findAllMatchIn(lore)
           if names.contains(res.group("name"));
           name = names(res.group("name"))) {
        config(s"prefix.$name.type", "number") match {
          case "string" => info(name) = merge(name, info(name), Right(res.group("str")))
          case "number" => info(name) = merge(name, info(name), Left(rangeOf(res)))
          case _ =>
        }
      }
    }
    info
  }

}
