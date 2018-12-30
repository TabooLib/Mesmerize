package it.alian.gun.mesmerize.scalaapi.runtime

import com.ilummc.tlib.resources.TLocale
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

class RichSender(private val sender: CommandSender) {

  def sendLocalizedMessage(node: String, params: String*): Unit = TLocale.sendTo(sender, node, params: _*)

  def locale(node: String, params: String*): Unit = sendLocalizedMessage(node, params: _*)

  def <<(text: String): RichSender = {
    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', text))
    this
  }
}

object RichSender {

  implicit def player2rich(player: CommandSender): RichSender = new RichSender(player)

  implicit def rich2player(player: RichSender): CommandSender = player.sender

}
