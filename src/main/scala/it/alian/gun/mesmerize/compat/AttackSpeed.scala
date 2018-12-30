package it.alian.gun.mesmerize.compat

import java.util

import it.alian.gun.mesmerize.lore.LoreInfo
import it.alian.gun.mesmerize.scalaapi.Prelude._
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player

object AttackSpeed {
  private val impl =
    try {
      classOf[Player].getMethod("getAttribute", classOf[Attribute])
      new Impl_1_9
    } catch {
      case _: Throwable => new Impl_1_8
    }


  def remove(id: Int): Unit = if (impl.isInstanceOf[AttackSpeed.Impl_1_8]) Impl_1_8.cooldown.remove(id)

  def set(player: Player, value: Double): Unit = impl.setSpeed(player, value)

  def check(player: Player, lore: LoreInfo): Boolean = impl.checkSpeed(player, lore)

  def update(player: Player): Unit = impl.updateSpeed(player)

  private class Impl_1_9 extends AttackSpeed {
    override def setSpeed(player: Player, value: Double): Unit = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(value)

    override def checkSpeed(player: Player, lore: LoreInfo) = true

    override def updateSpeed(player: Player): Unit = {}
  }

  private object Impl_1_8 {
    private[AttackSpeed] val cooldown = new util.HashMap[Integer, Long]
  }

  private class Impl_1_8 extends AttackSpeed {
    override def setSpeed(player: Player, value: Double): Unit = {}

    override def checkSpeed(player: Player, lore: LoreInfo): Boolean = {
      val time = Impl_1_8.cooldown.getOrDefault(player.getEntityId, 0L)
      val x = 1000D / Math.max(0.0001D, lore("attackSpeed").left.get.random + config("prefix.attackSpeed.base", 18D))
      (System.currentTimeMillis - time) > x
    }

    override def updateSpeed(player: Player): Unit = Impl_1_8.cooldown.put(player.getEntityId, System.currentTimeMillis)
  }

}

abstract class AttackSpeed {
  def setSpeed(player: Player, value: Double): Unit

  def checkSpeed(player: Player, lore: LoreInfo): Boolean

  def updateSpeed(player: Player): Unit
}