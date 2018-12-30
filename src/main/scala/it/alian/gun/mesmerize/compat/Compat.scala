package it.alian.gun.mesmerize.compat

import org.bukkit.entity.{Entity, Player}
import org.bukkit.{Bukkit, World}

import scala.collection.JavaConverters._

object Compat {

  def getOnlinePlayers: Seq[Player] =
    for {
      world <- Bukkit.getWorlds.asScala
      player <- world.getPlayers.asScala
    } yield player

  def getByEntityId(id: Int, world: World): Entity =
    (for (entity <- world.getLivingEntities.asScala if entity.getEntityId == id
    ) yield entity).headOption.orNull

}