package it.alian.gun.mesmerize.compat;

import it.alian.gun.mesmerize.lore.LoreParser;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public abstract class AttackSpeed {

    private static AttackSpeed impl;

    public static void init() {
        try {
            Player.class.getMethod("getAttribute", Attribute.class);
            impl = new Impl_1_9();
        } catch (Throwable e) {
            impl = new Impl_1_8();
        }
    }

    public static void remove(int id) {
        if (impl instanceof Impl_1_8)
            Impl_1_8.cooldown.remove(id);
    }

    public abstract void setSpeed(Player player, double value);

    public abstract boolean checkSpeed(Player player);

    public static void set(Player player, double value) {
        impl.setSpeed(player, value);
    }

    public static boolean check(Player player) {
        return impl.checkSpeed(player);
    }

    private static class Impl_1_9 extends AttackSpeed {

        @Override
        public void setSpeed(Player player, double value) {
            player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(value);
        }

        @Override
        public boolean checkSpeed(Player player) {
            return false;
        }
    }

    private static class Impl_1_8 extends AttackSpeed {

        private static final Map<Integer, Long> cooldown = new HashMap<>();

        @Override
        public void setSpeed(Player player, double value) {

        }

        @Override
        public boolean checkSpeed(Player player) {
            long time = cooldown.getOrDefault(player.getEntityId(), 0L);
            boolean rev = (System.currentTimeMillis() - time) < (1000D / LoreParser.getByEntityId(player.getEntityId()).getAttackSpeed());
            if (!rev)
                cooldown.put(player.getEntityId(), System.currentTimeMillis());
            return rev;
        }
    }

}
