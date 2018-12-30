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
      val key = s.substring("stats_".length)
      val info = LoreParser.parse(player)
      try {
        val field = info.getClass.getDeclaredField(key)
        field.setAccessible(true)
        MesmerizePlaceholder.format.format(field.get(info))
      } catch {
        case _: Exception => "NoElem"
      }
    } else ""

}