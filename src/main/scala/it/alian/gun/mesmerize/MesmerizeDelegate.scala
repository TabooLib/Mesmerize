package it.alian.gun.mesmerize

import it.alian.gun.mesmerize.compat.{Dependency, SplashParticle}
import it.alian.gun.mesmerize.listener.{BattleListener, ItemListener}
import it.alian.gun.mesmerize.lore.LoreCalculator
import it.alian.gun.mesmerize.scalaapi.Prelude._
import it.alian.gun.mesmerize.util.Updater
import me.skymc.taboolib.common.configuration.TConfiguration
import me.skymc.taboolib.common.inject.TInject
import org.bukkit.plugin.java.JavaPlugin

object MesmerizeDelegate {

  @TInject(Array("config.yml"))
  lazy val conf: TConfiguration = Mesmerize.configuration

  implicit lazy val instance: Mesmerize = JavaPlugin.getPlugin(classOf[Mesmerize])

  def onEnable(): Unit = {
    val time = System.currentTimeMillis()
    info("GENERAL_VERSION", instance.getServer.getClass.getName.split("\\.")(3), instance.getDescription.getVersion)
    instance.getCommand("mes").setExecutor(MesCommand)
    instance.getCommand("mesmerize").setExecutor(MesCommand)
    Dependency.init()
    listen(SplashParticle)
    listen(BattleListener)
    listen(ItemListener)
    try {
      LoreCalculator
      if (config("advanced.enableCustomAttackExpression", false))
        info("GENERAL_CUSTOM_SCRIPT_LOAD")
    } catch {
      case e: Throwable => error("ERROR_LOADING_CUSTOM_SCRIPT", e.toString)
    }
    Updater
    new Metrics(instance)
    val used = (System.currentTimeMillis() - time).toDouble / 1000D
    info("GENERAL_LOAD", used.toString)
  }

}
