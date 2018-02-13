package it.alian.gun.mesmerize.compat;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Compat {

    public static List<Player> getOnlinePlayers() {
        List<Player> list = new ArrayList<>();
        for (World world : Bukkit.getWorlds()) {
            list.addAll(world.getPlayers());
        }
        return Collections.unmodifiableList(list);
    }

    public static Entity getByEntityId(int id, World world) {
        for (LivingEntity livingEntity : world.getLivingEntities()) {
            if (livingEntity.getEntityId() == id) return livingEntity;
        }
        return null;
    }

}
