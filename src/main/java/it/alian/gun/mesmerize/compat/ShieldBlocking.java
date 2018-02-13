package it.alian.gun.mesmerize.compat;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public abstract class ShieldBlocking {

    private static ShieldBlocking impl;

    public static void init() {
        try {
            Material.valueOf("SHIELD");
            impl = new Impl_1_9();
        } catch (Throwable t) {
            impl = new AbstractImpl();
        }
    }

    public static boolean check(Player p) {
        return impl.checkShield(p);
    }

    public abstract boolean checkShield(Player player);

    private static class Impl_1_9 extends ShieldBlocking {

        @Override
        public boolean checkShield(Player player) {
            return player.getEquipment().getItemInMainHand().getType() == Material.SHIELD && player.isBlocking();
        }

    }

    private static class AbstractImpl extends ShieldBlocking {

        @Override
        public boolean checkShield(Player player) {
            return false;
        }
    }

}
