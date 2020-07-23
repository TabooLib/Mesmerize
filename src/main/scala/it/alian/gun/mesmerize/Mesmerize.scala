package it.alian.gun.mesmerize

import io.izzel.mesmerize.api.event.StatsRegisterEvent
import io.izzel.taboolib.loader.ScalaPlugin
import it.alian.gun.mesmerize.data.{DataManager, SimpleRegistry}
import org.bukkit.Bukkit

import scala.collection.mutable

object Mesmerize extends ScalaPlugin {

  val registry = new SimpleRegistry()
  val dataManager = new DataManager()

  override def onStarting(): Unit = {
    type A = Any
    val map = mutable.Map[A, A]()
  }

  override def onActivated(): Unit = {
    Bukkit.getPluginManager.callEvent(new StatsRegisterEvent(registry, dataManager))
  }
}
