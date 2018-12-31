package it.alian.gun.mesmerize.listener

import it.alian.gun.mesmerize.MesmerizeDelegate._
import it.alian.gun.mesmerize.compat.Compat
import it.alian.gun.mesmerize.lore.LoreParser
import it.alian.gun.mesmerize.scalaapi.Prelude._
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.{EntityDeathEvent, EntityPickupItemEvent, PlayerDeathEvent}
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType.SlotType
import org.bukkit.event.player.{PlayerInteractEvent, PlayerItemDamageEvent, PlayerItemHeldEvent}
import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.inventory.ItemStack

import scala.collection.JavaConverters._

object ItemListener extends Listener {

  private def check(stack: ItemStack, player: Player): Boolean = {
    val info = LoreParser.parse(stack)
    if (info.str("soulbound").nonEmpty && info.str("soulbound") != player.getName) {
      runTaskAsync(player.sendMessage(config("message.omSoulboundCheck", "")))
      return false
    }
    if (info.num("levelCap") > player.getLevel) {
      runTaskAsync(player.sendMessage(config("message.onLevelCheck", "").format(info.num("levelCap"))))
      return false
    }
    val cap = info.str("permissionCap")
    if (cap.nonEmpty && !player.hasPermission(config("general.permissionAlias." + cap, cap))) {
      runTaskAsync(player.sendMessage(config("message.onPermissionCheck", "").format(cap)))
      return false
    }
    true
  }

  @EventHandler
  def onOpenInv(event: PlayerInteractEvent): Unit = {
  }

  @EventHandler
  def onDeath(event: PlayerDeathEvent): Unit = {
  }

  @EventHandler
  def onBlockBreak(event: BlockBreakEvent): Unit = {
    if (event.getPlayer != null && event.getExpToDrop != 0)
      event.setExpToDrop((event.getExpToDrop * (LoreParser.parse(event.getPlayer).num("otherExpModifier") + 1)).toInt)
  }

  @EventHandler
  def onEntityDeath(event: EntityDeathEvent): Unit = {
    if (event.getEntity.getKiller != null && event.getDroppedExp != 0)
      event.setDroppedExp((event.getDroppedExp * (LoreParser.parse(event.getEntity.getKiller).num("attackExpModifier") + 1)).toInt)
  }

  @EventHandler
  def onItemChange(event: PlayerItemHeldEvent): Unit = {
    runTaskAsync(LoreParser.update(event.getPlayer): Unit)
    if (!event.getPlayer.getInventory.getItem(event.getNewSlot).empty && !check(event.getPlayer.getInventory.getItem(event.getNewSlot), event.getPlayer))
      event.setCancelled(true)
  }

  @EventHandler
  def onItemUse(event: PlayerItemDamageEvent): Unit = {
    val info = LoreParser.parse(event.getItem)
    if (math.random < info.num("unbreakable")) event.setDamage(0)
    if (event.getItem.getDurability == (event.getItem.getType.getMaxDurability - 1) && !config("general.breakOnDurabilityOff", true)) {
      val itemStack = event.getItem.clone
      event.getPlayer.getWorld.dropItemNaturally(event.getPlayer.getLocation, itemStack)
      event.setCancelled(true)
      event.getPlayer.getInventory.remove(itemStack)
      runTaskAsync(event.getPlayer.sendMessage(config("message.onDurabilityItemDrop", "").format(itemStack.getItemMeta.getDisplayName)))
    } else if (event.getItem.getType.getMaxDurability != 0) {
      val prev = event.getItem.getDurability.toDouble / event.getItem.getType.getMaxDurability.toDouble
      val now = (event.getItem.getDurability + event.getDamage).toDouble / event.getItem.getType.getMaxDurability.toDouble
      for (v <- config.getDoubleList("general.durabilityWarnThreshold").asScala if v > prev && v <= now) {
        event.getPlayer.sendMessage(config("message.onDurabilityWarn", "").format(
          if (event.getItem.hasItemMeta)
            if (event.getItem.getItemMeta.hasDisplayName) event.getItem.getItemMeta.getDisplayName
            else event.getItem.getType.name
          else event.getItem.getType.name, (1D - prev) * 100D))
      }
    }
  }

  @EventHandler
  def onPickup(event: EntityPickupItemEvent): Unit = event.getEntity match {
    case player: Player => if (!check(event.getItem.getItemStack, player)) event.setCancelled(true)
    case _ =>
  }

  @EventHandler
  def onClick(event: InventoryClickEvent): Unit = {
    if (event.getRawSlot == event.getWhoClicked.getInventory.getHeldItemSlot || event.getSlotType == SlotType.ARMOR)
      runTaskAsync(LoreParser.update(event.getWhoClicked): Unit)
  }

  runTask(10, config.getLong("general.regenInterval", 10)) {
    for (player <- Compat.getOnlinePlayers if !player.isDead && player.isValid) {
      val health = (LoreParser.parse(player).num("regeneration") + player.getHealth) min player.getMaxHealth
      player.setHealth(health)
    }
  }

  runTask(5, config.getLong("performance.loreUpdateInterval", 5)) {
    for (livingEntity <- Compat.getOnlinePlayers) {
      val info = LoreParser.parse(livingEntity)
      if (config("general.enableHealthControl", true)) {
        if (config("general.healthScaled", true)) {
          livingEntity.setHealthScaled(true)
          livingEntity.setHealthScale(config("general.healthScale", 20D))
        } else livingEntity.setHealthScaled(false)
        livingEntity.setMaxHealth(config("general.maximumHealth", 20D) min (config("prefix.health.base", 20D) + info.num("health")) max config("general.minimalHealth", 1D))
      }
      livingEntity.setWalkSpeed((0.9999 min (config("prefix.moveSpeed.base", 0.2) + info.num("moveSpeed")) max config("general.minimalMovespeed", 0.0001)).toFloat)
      livingEntity.setFlySpeed((0.9999 min (config("prefix.flySpeed.base", 0.2) + info.num("flySpeed")) max config("general.minimalFlyspeed", 0.0001)).toFloat)
      livingEntity.setAttackSpeed(config("prefix.attackSpeed.base", 18D) + info.num("attackSpeed"))
    }
  }

}