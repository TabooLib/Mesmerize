package it.alian.gun.mesmerize.compat.hook;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

public abstract class MesmerizeHolograph {

    private static MesmerizeHolograph impl;

    abstract boolean isHolo(Entity entity);

    public static boolean isHolographEntity(Entity entity) {
        return impl.isHolo(entity);
    }

    public static void init() {
        if (Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
            impl = new Impl();
        } else {
            impl = new AbstractImpl();
        }
    }

    private static class Impl extends MesmerizeHolograph {

        @Override
        boolean isHolo(Entity entity) {
            return HologramsAPI.isHologramEntity(entity);
        }
    }

    private static class AbstractImpl extends MesmerizeHolograph {

        @Override
        boolean isHolo(Entity entity) {
            return false;
        }
    }
}
