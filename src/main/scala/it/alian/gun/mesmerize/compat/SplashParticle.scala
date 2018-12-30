package it.alian.gun.mesmerize.compat

import java.util

import it.alian.gun.mesmerize.MesmerizeDelegate._
import it.alian.gun.mesmerize.scalaapi.Prelude._
import org.bukkit.Effect
import org.bukkit.entity.{Entity, ExperienceOrb, LivingEntity, Player}
import org.bukkit.event.entity.{EntityDamageByEntityEvent, EntityDeathEvent}
import org.bukkit.event.{EventHandler, EventPriority, Listener}

import scala.collection.JavaConverters._

object SplashParticle extends Listener {

  private val impl = if (config("misc.enableSplashParticles", true)) new PlayEffectImpl else new AbstractImpl
  private val effects = config[util.List[String]]("misc.splashParticles").asScala.map(Effect.valueOf).toArray

  @EventHandler(priority = EventPriority.HIGHEST)
  def onDeath(event: EntityDeathEvent): Unit = {
    if (!event.getEntity.isInstanceOf[Player] && config("misc.removeOnDeath")) {
      runTask(config("misc.removeDelay", 0).toLong)(event.getEntity.remove())
      val orb = event.getEntity.getWorld.spawn(event.getEntity.getLocation, classOf[ExperienceOrb])
      orb.setExperience(event.getDroppedExp)
    }
    impl(event.getEntity)
  }

  private class PlayEffectImpl extends (Entity => Unit) {
    override def apply(entity: Entity): Unit = entity match {
      case _: LivingEntity => entity.getLastDamageCause match {
        case event: EntityDamageByEntityEvent => event.getDamager match {
          case _: Player =>
            val effect = effects((math.random() * effects.length).toInt)
            entity.getWorld.spigot.playEffect(entity.getLocation, effect, 0, 0,
              0.5F, 0.5F, 0.5F, 0.5F,
              config("misc.particleAmount", 100), config("misc.particleVisibleRange", 36))
          case _ =>
        }
        case _ =>
      }
      case _ =>
    }
  }

  private class AbstractImpl extends (Entity => Unit) {
    override def apply(entity: Entity): Unit = ()
  }

}
