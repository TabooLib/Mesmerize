package it.alian.gun.mesmerize

import java.util.{List => JList}

import it.alian.gun.mesmerize.MesmerizeDelegate._
import it.alian.gun.mesmerize.listener.ItemView
import it.alian.gun.mesmerize.lore.LoreParser
import it.alian.gun.mesmerize.scalaapi.Prelude.{config, _}
import org.bukkit.command.{Command, CommandExecutor, CommandSender, TabCompleter}
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player

import scala.collection.JavaConverters._

object MesCommand extends CommandExecutor with TabCompleter {

  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
    args.map(_.toLowerCase) match {
      case Array("stats" | "s") if sender.isInstanceOf[Player] && sender.hasPermission("mesmerize.showstats") =>
        sender.locale("COMMAND_SHOW_STATS", sender.getName)
        runTask {
          LoreParser.parse(sender.asInstanceOf[Player]).foreach({
            case (name, Left(Between(low: Double, high: Double))) => sender << "%s%s &f> %.2f - %.2f".format(
              config(s"prefix.$name.color", "&f"),
              config(s"prefix.$name.name", name), low, high)
            case (name, Right(str)) => sender << "%s%s &f> %s".format(
              config(s"prefix.$name.color", "&f"),
              config(s"prefix.$name.name", name), str)
          })
        }
      case Array("config" | "c", _*) if sender.hasPermission("mesmerize.config") =>
        val conf = MesmerizeDelegate.conf
        args.tail match {
          case Array("reload") =>
            conf.save(conf.getFile)
            conf.reload()
            updateConfig()
            sender.locale("GENERAL_CONFIG_LOAD")
          case Array("load") =>
            conf.reload()
            updateConfig()
            sender.locale("GENERAL_CONFIG_LOAD")
          case Array("save") =>
            conf.save(conf.getFile)
            sender.locale("GENERAL_CONFIG_SAVE")
          case Array("set", path, value) =>
            def safely[U](action: => U, ret: Boolean = true): Boolean = try {
              action
              ret
            } catch {
              case _: Throwable => false
            }

            val prev = conf.get(path)

            if (safely(conf.set(path, value.toBoolean)) ||
              safely(conf.set(path, value.toInt)) ||
              safely(conf.set(path, value.toDouble)) ||
              safely(conf.set(path, value)) ||
              safely(sender.locale("WARN_CONFIG_SET", path), ret = false))
              sender.locale("CONFIG_SET", path, prev.toString, value)

            updateConfig()
          case Array("list", path) =>
            (if (path == ".") conf else conf.get(path)) match {
              case sec: ConfigurationSection =>
                sender.locale("CONFIG_LIST", path, sec.getKeys(false).size().toString)
                sec.getValues(false).asScala.foreach({
                  case (name, _: ConfigurationSection) => sender.locale("CONFIG_LIST_1", name)
                  case (name, value) => sender.locale("CONFIG_LIST_2", name, value.getClass.getSimpleName, value.toString)
                  case _ =>
                })
              case value => sender << s"$path : $value"
            }
          case _ =>
        }
      case Array("item" | "i") if sender.isInstanceOf[Player] =>
        val player = sender.asInstanceOf[Player]
        if (player.getInventory.getItemInHand.empty) player.locale("inlay.empty-hand")
        else ItemView.openView(player.getInventory.getItemInHand, player)
      case _ => sender.locale("COMMAND_HELP_LIST")
    }
    true
  }

  override def onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array[String]): JList[String] = {
    args.map(_.toLowerCase) match {
      case Array(_) => List("item", "i", "stats", "s", "config", "c").asJava
      case Array("config", _) => List("reload", "load", "save", "set", "list").asJava
      case Array("config", "set" | "list", pref) => config.getKeys(true).asScala.filter(_.startsWith(pref)).toList.asJava
      case _ => null
    }
  }
}
