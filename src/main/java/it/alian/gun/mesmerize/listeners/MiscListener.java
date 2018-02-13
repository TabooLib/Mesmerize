package it.alian.gun.mesmerize.listeners;

import it.alian.gun.mesmerize.Mesmerize;
import it.alian.gun.mesmerize.compat.AttackSpeed;
import it.alian.gun.mesmerize.lore.LoreParser;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class MiscListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        int id = event.getPlayer().getEntityId();
        LoreParser.remove(id);
        AttackSpeed.remove(id);
    }

    public static void init() {
        Bukkit.getPluginManager().registerEvents(new MiscListener(), Mesmerize.instance);
    }
}
