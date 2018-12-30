package it.alian.gun.mesmerize.compat

import it.alian.gun.mesmerize.scalaapi.Prelude._
import org.bukkit.Material
import org.bukkit.entity.Player

object ShieldBlocking extends (Player => Boolean) {
  private val impl = try {
    Material.valueOf("SHIELD")
    new Impl_1_9
  } catch {
    case _: Throwable => new AbstractImpl
  }

  /**
    * @param player the attacker
    * @return true if should ignore shield blocking
    */
  def apply(player: Player): Boolean = impl(player)

  private class Impl_1_9 extends (Player => Boolean) {
    override def apply(player: Player): Boolean = ((player.getEquipment.getItemInMainHand.getType ne Material.SHIELD)
      || !player.isBlocking) && config("general.ignoreShieldBlocking")
  }

  private class AbstractImpl extends (Player => Boolean) {
    override def apply(player: Player) = false
  }

}
