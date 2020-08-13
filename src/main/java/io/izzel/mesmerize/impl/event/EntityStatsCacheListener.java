package io.izzel.mesmerize.impl.event;

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
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import java.util.HashSet;
import java.util.Set;

public class EntityStatsCacheListener implements Listener {

    private static final Set<Integer> INVENTORY = Sets.newHashSet(100, 101, 102, 103, -106);

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEquipmentChange(InventoryClickEvent event) {
        if (event.getInventory().getType() == InventoryType.PLAYER) {
            int rawSlot = event.getRawSlot();
            if (rawSlot == event.getWhoClicked().getInventory().getHeldItemSlot()
                || INVENTORY.contains(rawSlot)) {
                StatsService.instance().refreshCache(event.getWhoClicked());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemSwap(PlayerSwapHandItemsEvent event) {
        StatsService.instance().refreshCache(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemHeld(PlayerItemHeldEvent event) {
        StatsService.instance().refreshCache(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemDrag(InventoryDragEvent event) {
        Set<Integer> rawSlots = new HashSet<>(event.getRawSlots());
        if (rawSlots.removeAll(INVENTORY)) {
            StatsService.instance().refreshCache(event.getWhoClicked());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemDrop(PlayerDropItemEvent event) {
        StatsService.instance().refreshCache(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemPick(EntityPickupItemEvent event) {
        StatsService.instance().refreshCache(event.getEntity());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        StatsService.instance().refreshCache(event.getPlayer());
    }
}
