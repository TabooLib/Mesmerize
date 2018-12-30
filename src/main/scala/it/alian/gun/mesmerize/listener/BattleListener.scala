package it.alian.gun.mesmerize.listener

import it.alian.gun.mesmerize.MesmerizeDelegate._
import it.alian.gun.mesmerize.compat.Compat
import it.alian.gun.mesmerize.compat.hook.MesmerizeHolograph
import it.alian.gun.mesmerize.lore.{LoreCalculator, LoreParser}
import it.alian.gun.mesmerize.scalaapi.Prelude._
import org.bukkit.Bukkit
import org.bukkit.entity.{LivingEntity, Player, Projectile}
import org.bukkit.event.block.Action
import org.bukkit.event.entity.{EntityDamageByEntityEvent, EntityDamageEvent}
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.{EventHandler, EventPriority, Listener}
import org.bukkit.util.Vector

import scala.collection.JavaConverters._

object BattleListener extends Listener {

  @EventHandler
  def onRangeAttack(event: PlayerInteractEvent): Unit = {
    if (config("performance.enableLongerRange", false) && (event.getAction == Action.LEFT_CLICK_AIR)) {
      if (event.getPlayer != null && !event.getPlayer.checkAttackSpeed(LoreParser.parse(event.getPlayer))) ()
      if (!event.getPlayer.getEquipment.getItemInHand.hasLore) ()
      val map = (for {
        entity <- event.getPlayer.getWorld.getLivingEntities.asScala
        id = entity.getEntityId
        loc = entity.getEyeLocation
      } yield (id, loc)).toMap
      val playerId = event.getPlayer.getEntityId
      val world = event.getPlayer.getWorld
      val source = event.getPlayer.getEyeLocation
      runTaskAsync {
        val info = LoreParser.parse(event.getPlayer)
        val list = (for {
          (entityId, target) <- map
          if ((target.distance(source) < config("performance.maxAttackRange", 6D)
            .min(info.num("attackRange") + config("prefix.attackRange.base", 6D)))
            && (new Vector(target.getX - source.getX, target.getY - source.getY, target.getZ - source.getZ)
            .angle(source.getDirection) < (Math.PI / 48D)))
        } yield entityId).toSeq
        list.sortWith(map(_).distanceSquared(source) < map(_).distanceSquared(source))
        list.headOption match {
          case Some(i) =>
            runTask {
              val other = Compat.getByEntityId(i, world).asInstanceOf[LivingEntity]
              val player = Compat.getByEntityId(playerId, world).asInstanceOf[Player]
              if (other != null && !other.hasMetadata("NPC") && player.hasLineOfSight(other) && !MesmerizeHolograph.isHolographEntity(other)) {
                val e = new EntityDamageByEntityEvent(player, Compat.getByEntityId(i, world), EntityDamageEvent.DamageCause.ENTITY_ATTACK, player.getEquipment.getItemInHand.getAttackDamage)
                Bukkit.getPluginManager.callEvent(e)
                if (!e.isCancelled) {
                  other.setLastDamageCause(e)
                  other.setLastDamage(e.getDamage)
                  other.setHealth(0D max (other.getHealth - e.getDamage) min other.getMaxHealth)
                  val vector = player.getLocation.getDirection.normalize.multiply(0.3).setY(0.2)
                  other.setVelocity(vector)
                }
              }
            }
          case None =>
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
  def onShieldBlocking(event: EntityDamageByEntityEvent): Unit = {
    if (event.isCancelled && !event.getEntity.hasMetadata("NPC") && event.getEntity.ignoreShieldBlocking)
      event.setCancelled(false)
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  def onAttack(event: EntityDamageByEntityEvent): Unit = {
    val time = System.nanoTime()
    (event.getEntity, event.getDamager) match {
      case (entity: LivingEntity, s: LivingEntity) =>
        val (bow, source) = s match {
          case projectile: Projectile => (true, projectile.getShooter.asInstanceOf[LivingEntity])
          case _ => (false, s)
        }
        if (MesmerizeHolograph.isHolographEntity(entity)
          || entity.hasMetadata("NPC")) ()
        val (atk, dfs) = (LoreParser.parse(source), LoreParser.parse(entity))
        if (!entity.checkAttackSpeed(atk)) ()

        // 攻击范围
        if (config("performance.enableAttackRange", false) && (!bow) &&
          (atk.num("attackRange") + config("prefix.attackRange.base", 6D) < source.getLocation.distance(entity.getLocation))) {
          event.setCancelled(true)
          ()
        }

        // 命中及闪避
        if (math.random() > (config("prefix.accuracy.base", 0.98D) + atk.num("accuracy")
          - config("prefix.dodge.base", 0.02) - dfs.num("dodge"))) {
          event.setCancelled(true)
          if (config("combatMessage.showOnMiss", true))
            runTaskAsync(source.sendMessage(config("combatMessage.onMiss", "").format(entity.getDisplayName)))
          if (config("combatMessage.showOnDodge", true))
            runTaskAsync(entity.sendMessage(config("combatMessage.onDodge", "").format(source.getDisplayName)))
          ()
        }

        // 会心一击
        if (math.random < atk.num("suddenDeath")) {
          event.setDamage(entity.getHealth)
          if (config("combatMessage.showOnSuddenDeath", true))
            runTaskAsync(source.sendMessage(config("combatMessage.onSuddenDeath", "").format(entity.getDisplayName, entity.getHealth)))
          ()
        } else
          event.setDamage(LoreCalculator.finalDamage(event.getDamage, atk, dfs, source, entity, bow))
        if (config("combatMessage.showOnDamage", true))
          runTaskAsync(source.sendMessage(config("combatMessage.onDamage", "").format(entity.getDisplayName, event.getDamage)))

      { // 反弹
        val prev = source.getHealth
        val health = (prev - LoreCalculator.finalReflect(event.getDamage, dfs)) max 0D
        source.setHealth(health)
        if (config("combatMessage.showOnReflect", true) && (prev - health).abs > 1E-6)
          runTaskAsync(entity.sendMessage(config("combatMessage.onReflect", "").format(prev - health, source.getDisplayName)))
      }
      { // 吸血
        val prev = source.getHealth
        val health = (prev + atk.num("lifeSteal") * event.getDamage) max source.getMaxHealth
        source.setHealth(health)
        if (config("combatMessage.showOnLifeSteal", true) && (prev - health).abs > 1E-6)
          runTaskAsync(source.sendMessage(config("combatMessage.onLifeSteal", "").format(entity.getDisplayName, health - prev)))
      }

        entity.updateAttackSpeed()
      case _ =>
    }

    if (config("debug", false)) {
      val cur = System.nanoTime - time
      runTaskAsync(println("Done process in " + cur * 1E-6 + " ms."))
    }
  }

}
