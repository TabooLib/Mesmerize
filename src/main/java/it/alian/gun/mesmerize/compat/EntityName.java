package it.alian.gun.mesmerize.compat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public abstract class EntityName {

    private static EntityName impl;

    public static String get(Entity entity) {
        return impl.getName(entity);
    }

    public static void init() {
        if (Bukkit.getServer().getClass().getName().split("\\.")[3].equals("v1_7_R4"))
            impl = new Impl_1_7();
        else
            impl = new Impl_1_8();
    }

    public abstract String getName(Entity entity);

    private static class Impl_1_7 extends EntityName {

        @Override
        public String getName(Entity entity) {
            if (entity instanceof Player)
                return ((Player) entity).getDisplayName();
            else
                return entity.getType().getName();
        }
    }

    private static class Impl_1_8 extends EntityName {

        @Override
        public String getName(Entity entity) {
            if (entity instanceof Player)
                return ((Player) entity).getDisplayName();
            else
                return entity.getName();
        }
    }

}
