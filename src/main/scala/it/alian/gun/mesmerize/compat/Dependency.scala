package it.alian.gun.mesmerize.compat

import com.ilummc.tlib.resources.TLocale
import it.alian.gun.mesmerize.MesmerizeDelegate._
import it.alian.gun.mesmerize.compat.hook.{MesmerizePlaceholder, MesmerizeSkillApi, MesmerizeVault}
import it.alian.gun.mesmerize.scalaapi.Prelude._
import org.bukkit.Bukkit

object Dependency {
  def init(): Unit = {
    if (!Bukkit.getPluginManager.isPluginEnabled("PowerNBT")) {
      error("WARN_DEPENDENCY_MISSING", "PowerNBT")
      throw new RuntimeException(TLocale.asString("WARN_DEPENDENCY_MISSING", "PowerNBT"))
    }
    if (Bukkit.getPluginManager.isPluginEnabled("PlaceholderAPI")) {
      new MesmerizePlaceholder(instance, "mesmerize").hook
      info("GENERAL_HOOK", "PlaceholderAPI")
    }
    if (Bukkit.getPluginManager.isPluginEnabled("SkillAPI")) {
      MesmerizeSkillApi.init()
      info("GENERAL_HOOK", "SkillAPI")
    }
    if (Bukkit.getPluginManager.isPluginEnabled("HolographicDisplays"))
      info("GENERAL_HOOK", "HolographicDisplays")
    MesmerizeVault.init()
    if (Bukkit.getPluginManager.isPluginEnabled("Vault"))
      info("GENERAL_HOOK", "Vault")
  }
}