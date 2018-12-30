package it.alian.gun.mesmerize.scalaapi.runtime

import me.skymc.taboolib.inventory.builder.ItemBuilder
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.ItemStack

class RichOfflinePlayer(private val offlinePlayer: OfflinePlayer) {

  def getSkullItem: ItemStack = new ItemBuilder(offlinePlayer).build()

}


object RichOfflinePlayer {

  implicit def player2rich(player: OfflinePlayer): RichOfflinePlayer = new RichOfflinePlayer(player)

  implicit def rich2player(player: RichOfflinePlayer): OfflinePlayer = player.offlinePlayer

}