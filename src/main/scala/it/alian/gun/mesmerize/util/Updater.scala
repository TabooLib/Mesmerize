package it.alian.gun.mesmerize.util

import java.io.InputStreamReader
import java.net.{HttpURLConnection, URL}

import com.google.gson.{JsonElement, JsonParser}
import com.ilummc.tlib.resources.TLocale
import it.alian.gun.mesmerize.MesmerizeDelegate._
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.event.player.PlayerJoinEvent
import it.alian.gun.mesmerize.scalaapi.Prelude._

import scala.collection.JavaConverters._

object Updater {
  private val UPDATER_API = "https://raw.githubusercontent.com/PluginsCDTribe/Mesmerize/master/version.yumc.json"
  private var packet: UpdatePacket = _

  runTask(20 * 60 * 60, 20 * 60 * 60) {
    try {
      val url = new URL(UPDATER_API)
      val connection = url.openConnection.asInstanceOf[HttpURLConnection]
      connection.setConnectTimeout(10000)
      connection.setReadTimeout(10000)
      connection.connect()
      val json: Iterator[JsonElement] = new JsonParser().parse(
        new InputStreamReader(connection.getInputStream, "utf-8")).getAsJsonArray.iterator().asScala
      val packets = json.map(_.getAsJsonObject).map(el =>
        UpdatePacket(el.get("version").getAsString, el.get("releaseDate").getAsString,
          el.getAsJsonArray("description").iterator().asScala.map(_.getAsString).toArray))
      if (packets.nonEmpty) {
        packet = packets.next
        sendUpdate(Bukkit.getConsoleSender)
      }
    } catch {
      case _: Throwable =>
    }
  }

  listen(classOf[PlayerJoinEvent]) { event =>
    if (event.getPlayer.isOp) sendUpdate(event.getPlayer)
  }

  private def sendUpdate(sender: CommandSender): Unit = {
    packet match {
      case UpdatePacket(version, releaseDate, description) =>
        if (config("checkUpdate", true) && !(version == instance.getDescription.getVersion)) {
          sender.locale("UPDATER_HEADER", version, releaseDate)
          description.foreach(TLocale.sendTo(sender, "UPDATER_BODY", _))
          sender.locale("UPDATER_FOOTER", instance.getDescription.getWebsite)
        }
      case _ =>
    }

  }

  private case class UpdatePacket(version: String, releaseDate: String, description: Array[String])

}
