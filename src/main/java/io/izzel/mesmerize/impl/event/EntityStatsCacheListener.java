package io.izzel.mesmerize.impl.event;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import com.google.common.collect.Sets;
import io.izzel.mesmerize.api.service.StatsService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.Set;

public class EntityStatsCacheListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHotbarChange(InventoryClickEvent event) {
        if (event.getInventory().getType() == InventoryType.CRAFTING) {
            int rawSlot = event.getRawSlot();
            if (rawSlot == event.getWhoClicked().getInventory().getHeldItemSlot() + 36) {
                StatsService.instance().refreshCache(event.getWhoClicked(), false);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemSwap(PlayerSwapHandItemsEvent event) {
        StatsService.instance().refreshCache(event.getPlayer(), false);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemHeld(PlayerItemHeldEvent event) {
        ItemStack oldItem = event.getPlayer().getInventory().getItem(event.getPreviousSlot());
        ItemStack newItem = event.getPlayer().getInventory().getItem(event.getNewSlot());
        if (oldItem == null && newItem != null
            || oldItem != null && (newItem == null || oldItem.getType() != newItem.getType())) {
            StatsService.instance().refreshCache(event.getPlayer(), false);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemDrop(PlayerDropItemEvent event) {
        StatsService.instance().refreshCache(event.getPlayer(), false);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemPick(EntityPickupItemEvent event) {
        ItemStack itemStack = event.getItem().getItemStack();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null && !itemStack.getItemMeta().getPersistentDataContainer().isEmpty()) {
            StatsService.instance().refreshCache(event.getEntity(), false);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        StatsService.instance().refreshCache(event.getPlayer(), true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event) {
        StatsService.instance().refreshCache(event.getPlayer(), false);
    }

    public static class Paper implements Listener {

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onArmourChange(PlayerArmorChangeEvent event) {
            StatsService.instance().refreshCache(event.getPlayer(), false);
        }
    }

    public static class Spigot implements Listener {

        private static final Set<Integer> INVENTORY = Sets.newHashSet(5, 6, 7, 8, 45);

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onEquipmentChange(InventoryClickEvent event) {
            if (event.getInventory().getType() == InventoryType.CRAFTING) {
                int rawSlot = event.getRawSlot();
                if (rawSlot == event.getWhoClicked().getInventory().getHeldItemSlot() + 36
                    || INVENTORY.contains(rawSlot)) {
                    StatsService.instance().refreshCache(event.getWhoClicked(), false);
                }
            }
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onItemDrag(InventoryDragEvent event) {
            Set<Integer> rawSlots = new HashSet<>(event.getRawSlots());
            if (rawSlots.removeAll(INVENTORY)) {
                StatsService.instance().refreshCache(event.getWhoClicked(), false);
            }
        }
    }
}
