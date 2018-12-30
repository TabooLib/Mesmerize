package it.alian.gun.mesmerize.compat.hook

import java.text.DecimalFormat

import it.alian.gun.mesmerize.lore.LoreParser
import it.alian.gun.mesmerize.scalaapi.Prelude._
import me.clip.placeholderapi.external.EZPlaceholderHook
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

object MesmerizePlaceholder {
  private val format = new DecimalFormat(config("misc.customDecimalFormat", "0.00"))
}

class MesmerizePlaceholder(val plugin: Plugin, val identifier: String) extends EZPlaceholderHook(plugin, identifier) {
  override def onPlaceholderRequest(player: Player, s: String): String =
    if (s.startsWith("stats_")) {
      val name = s.substring("stats_".length)
      val info = LoreParser.parse(player)
      config(s"prefix.$name.type", "number") match {
        case "number" => MesmerizePlaceholder.format.format(info.num(name))
        case "string" => info.str(name)
      }
    } else ""

}