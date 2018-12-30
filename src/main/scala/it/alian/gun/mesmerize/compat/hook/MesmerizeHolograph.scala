package it.alian.gun.mesmerize.compat.hook

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI
import org.bukkit.Bukkit
import org.bukkit.entity.Entity

object MesmerizeHolograph {

  private val impl = if (Bukkit.getPluginManager.isPluginEnabled("HolographicDisplays")) new Impl else new AbstractImpl

  def isHolographEntity(entity: Entity): Boolean = impl(entity)

  private class Impl extends (Entity => Boolean) {
    override def apply(entity: Entity): Boolean = HologramsAPI.isHologramEntity(entity)
  }

  private class AbstractImpl extends (Entity => Boolean) {
    override def apply(entity: Entity): Boolean = false
  }

}
