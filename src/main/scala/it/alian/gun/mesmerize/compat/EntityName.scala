package it.alian.gun.mesmerize.compat

import org.bukkit.Bukkit
import org.bukkit.entity.{Entity, Player}

object EntityName {
  private val impl = if (Bukkit.getServer.getClass.getName.split("\\.")(3) == "v1_7_R4") new Impl_1_7 else new Impl_1_8

  def get(entity: Entity): String = impl(entity)

  private class Impl_1_7 extends (Entity => String) {
    override def apply(entity: Entity): String = entity match {
      case player: Player => player.getDisplayName
      case _ => entity.getType.getName
    }
  }

  private class Impl_1_8 extends (Entity => String) {
    override def apply(entity: Entity): String = entity match {
      case player: Player => player.getDisplayName
      case _ => entity.getName
    }
  }

}