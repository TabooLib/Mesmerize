package it.alian.gun.mesmerize.compat.hook

import com.sucy.skill.api.event.PlayerExperienceGainEvent
import it.alian.gun.mesmerize.MesmerizeDelegate._
import it.alian.gun.mesmerize.lore.LoreParser
import it.alian.gun.mesmerize.scalaapi.Prelude._
import org.bukkit.event.{EventHandler, Listener}

object MesmerizeSkillApi extends Listener {

  def init(): Unit = if (config("general.useSkillApi", false)) listen(this)

  @EventHandler
  def onSkillExpGain(event: PlayerExperienceGainEvent): Unit =
    if (event.getPlayerData.getPlayer != null)
      event.setExp(event.getExp * LoreParser.parse(event.getPlayerData.getPlayer).num("otherExpModifier") + 1)

}
