package it.alian.gun.mesmerize.listeners;

import it.alian.gun.mesmerize.MConfig;
import it.alian.gun.mesmerize.Mesmerize;
import it.alian.gun.mesmerize.compat.AttackSpeed;
import it.alian.gun.mesmerize.compat.Compat;
import it.alian.gun.mesmerize.lore.ItemInfo;
import it.alian.gun.mesmerize.lore.LoreInfo;
import it.alian.gun.mesmerize.lore.LoreParser;
import it.alian.gun.mesmerize.util.Math;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class ItemListener implements Listener {

    @EventHandler
    public void onOpenInv(PlayerInteractEvent event) {

    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {

    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        long nano = System.nanoTime();
        if (event.getPlayer() != null && event.getExpToDrop() != 0) {
            event.setExpToDrop((int) (event.getExpToDrop() *
                    (LoreParser.getByEntityId(event.getPlayer().getEntityId()).getOtherExpModifier() + 1)));
        }
        if (MConfig.debug)
            System.out.println(event.getEventName() + " processed in " + (System.nanoTime() - nano) * 1E-6 + " ms.");
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        long nano = System.nanoTime();
        if (event.getEntity().getKiller() != null && event.getDroppedExp() != 0) {
            LoreInfo info = LoreParser.getByEntityId(event.getEntity().getKiller().getEntityId());
            event.setDroppedExp((int) (event.getDroppedExp() * (info.getAttackExpModifier() + 1)));
        }
        if (MConfig.debug)
            System.out.println(event.getEventName() + " processed in " + (System.nanoTime() - nano) * 1E-6 + " ms.");
    }

    @EventHandler
    public void onItemChange(PlayerItemHeldEvent event) {
        long nano = System.nanoTime();
        if (LoreParser.check(event.getPlayer().getInventory().getItem(event.getPreviousSlot()), event.getPlayer())) {
            event.setCancelled(true);
        }
        if (MConfig.debug)
            System.out.println(event.getEventName() + " processed in " + (System.nanoTime() - nano) * 1E-6 + " ms.");
    }

    @EventHandler
    public void onItemUse(PlayerItemDamageEvent event) {
        long nano = System.nanoTime();
        ItemInfo info = LoreParser.parseItem(event.getItem());
        if (Math.random() < info.getUnbreakable()) event.setDamage(0);
        if (event.getItem().getDurability() == (event.getItem().getType().getMaxDurability() - 1) && !MConfig.General.breakOnDurabilityOff) {
            ItemStack itemStack = event.getItem().clone();
            event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), itemStack);
            event.setCancelled(true);
            event.getPlayer().getInventory().remove(itemStack);
            event.getPlayer().sendMessage(String.format(MConfig.Message.onDurabilityItemDrop, itemStack.getItemMeta().getDisplayName()));
        } else if (event.getItem().getType().getMaxDurability() != 0) {
            double prev = ((double) event.getItem().getDurability()) / ((double) event.getItem().getType().getMaxDurability());
            double now = ((double) (event.getItem().getDurability() + event.getDamage())) / ((double) event.getItem().getType().getMaxDurability());
            for (double v : MConfig.General.durabilityWarnThreshold) {
                if (v > prev && v <= now) {
                    event.getPlayer().sendMessage(String.format(MConfig.Message.onDurabilityWarn,
                            event.getItem().hasItemMeta() ? event.getItem().getItemMeta().hasDisplayName() ?
                                    event.getItem().getItemMeta().getDisplayName() : event.getItem().getType().name() :
                                    event.getItem().getType().name(), ((1D - prev) * 100D)));
                }
            }
        }
        if (MConfig.debug)
            System.out.println(event.getEventName() + " processed in " + (System.nanoTime() - nano) * 1E-6 + " ms.");
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        if (LoreParser.check(event.getItem().getItemStack(), event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        /*
        if (LoreParser.check(event.getCurrentItem(), event.getWhoClicked())) {
            event.setCancelled(true);
            event.getWhoClicked().sendMessage(MConfig.Message.omSoulboundCheck);
        }
        */
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (MConfig.General.enableHealthControl)
            for (Player player : Compat.getOnlinePlayers()) {
                if (!player.isDead() && player.isValid()) {
                    double health = Math.min(LoreParser.getByEntityId(player.getEntityId()).getRegeneration() + player.getHealth(),
                            player.getMaxHealth());
                    player.setHealth(health);
                }
            }
    }

    public static void init() {
        Bukkit.getPluginManager().registerEvents(new ItemListener(), Mesmerize.instance);
        Bukkit.getScheduler().runTaskTimer(Mesmerize.instance, () ->
                Bukkit.getPluginManager().callEvent(new TickEvent()), 10, MConfig.General.regenInterval);
        Bukkit.getScheduler().runTaskTimer(Mesmerize.instance, () -> {
            for (Player livingEntity : Compat.getOnlinePlayers()) {
                LoreInfo info = LoreParser.getByEntityId(livingEntity.getEntityId());
                if (MConfig.General.enableHealthControl) {
                    if (MConfig.General.healthScaled) {
                        livingEntity.setHealthScaled(true);
                        livingEntity.setHealthScale(MConfig.General.healthScale);
                    } else {
                        livingEntity.setHealthScaled(false);
                    }
                    livingEntity.setMaxHealth(Math.constraint(MConfig.General.maximumHealth,
                            MConfig.General.minimalHealth, MConfig.General.baseHealth + info.getHealth()));
                }
                livingEntity.setWalkSpeed((float) Math.constraint(0.9999,
                        MConfig.General.minimalMovespeed, MConfig.General.baseMovespeed + info.getMoveSpeed()));
                livingEntity.setFlySpeed((float) Math.constraint(0.999,
                        MConfig.General.minimalFlyspeed, MConfig.General.baseFlyspeed + info.getFlySpeed()));
                AttackSpeed.set(livingEntity, MConfig.General.baseAttackSpeed + info.getAttackSpeed());
            }
        }, 5, MConfig.Performance.loreUpdateInterval);
    }

    public static class TickEvent extends Event {

        private static final HandlerList HANDLER_LIST = new HandlerList();

        @Override
        public HandlerList getHandlers() {
            return HANDLER_LIST;
        }

        public static HandlerList getHandlerList() {
            return HANDLER_LIST;
        }

    }

}
