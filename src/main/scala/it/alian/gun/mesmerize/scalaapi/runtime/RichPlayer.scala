package it.alian.gun.mesmerize.scalaapi.runtime

import me.skymc.taboolib.anvil.AnvilContainerAPI
import me.skymc.taboolib.scoreboard.ScoreboardUtil
import org.bukkit.entity.Player

import scala.collection.JavaConverters._

class RichPlayer(private val player: Player) extends RichOfflinePlayer(player) {

  def displaySidebar(title: String, elements: Map[String, Integer]): Unit = ScoreboardUtil.rankedSidebarDisplay(player, title, mapAsJavaMap(elements))

  def displaySidebarUnranked(title: String, elements: Array[String]): Unit = ScoreboardUtil.unrankedSidebarDisplay(player, elements: _*)

  def displaySidebarUnranked(title: String, elements: List[String]): Unit = ScoreboardUtil.unrankedSidebarDisplay(player, elements: _*)

  def displaySidebarUnranked(title: String, elements: String*): Unit = ScoreboardUtil.unrankedSidebarDisplay(player, elements: _*)

  def openAnvil(): Unit = AnvilContainerAPI.openAnvil(player)

}

object RichPlayer {

  implicit def player2rich(player: Player): RichPlayer = new RichPlayer(player)

  implicit def rich2player(player: RichPlayer): Player = player.player

}
