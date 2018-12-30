package it.alian.gun.mesmerize.scalaapi

import it.alian.gun.mesmerize.scalaapi.runtime._
import org.bukkit.command.CommandSender
import org.bukkit.entity.{Entity, Player}
import org.bukkit.inventory.ItemStack
import org.bukkit.util.{Vector => Vec}
import org.bukkit.{Location, OfflinePlayer, World}

abstract class Implicits {

  implicit def item2rich(item: ItemStack): RichItem = RichItem item2rich item

  implicit def entity2rich(entity: Entity): RichEntity = RichEntity entity2rich entity

  implicit def player2rich(player: Player): RichPlayer = RichPlayer rich2player player

  implicit def offline2rich(player: OfflinePlayer): RichOfflinePlayer = RichOfflinePlayer rich2player player

  implicit def sender2rich(sender: CommandSender): RichSender = RichSender player2rich sender

  implicit def tuple2location(loc: (World, Double, Double, Double)): Location = new Location(loc._1, loc._2, loc._3, loc._4)

  implicit def tuple2vector(vec: (Double, Double, Double)): Vec = new Vec(vec._1, vec._2, vec._3)

  implicit def location2tuple(loc: Location): (Double, Double, Double) = (loc.getX, loc.getY, loc.getZ)

  implicit def vector2tuple(vec: Vec): (Double, Double, Double) = (vec.getX, vec.getY, vec.getZ)

  implicit def location2rich(loc: Location): RichLocation = RichLocation Location2rich loc

  implicit def vector2rich(vector: Vec): RichVector = RichVector vector2rich vector

}
