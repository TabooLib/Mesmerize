package it.alian.gun.mesmerize.compat;

import it.alian.gun.mesmerize.MConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.endlesscode.rpginventory.api.InventoryAPI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Equipment {

    private static Equipment instance;

    public static void init() {
        try {
            org.bukkit.inventory.EntityEquipment.class.getMethod("getItemInMainHand");
            instance = new Impl_1_9();
        } catch (NoSuchMethodException e) {
            instance = new Impl_1_8();
        }
        if (MConfig.General.useRPGInventory && Bukkit.getPluginManager().isPluginEnabled("RPGInventory"))
            instance = new Impl_RPGInventory(instance);
    }

    public static List<ItemStack> collect(Entity entity) {
        return instance.collectOf(entity);
    }

    abstract List<ItemStack> collectOf(Entity player);

    private static class Impl_1_8 extends Equipment {

        @Override
        List<ItemStack> collectOf(Entity player) {
            if (player instanceof HumanEntity) {
                List<ItemStack> rev = new ArrayList<>(Arrays.asList(((HumanEntity) player).getEquipment().getArmorContents()));
                rev.add(((HumanEntity) player).getEquipment().getItemInHand());
                return rev;
            }
            return new ArrayList<>();
        }
    }

    private static class Impl_1_9 extends Equipment {

        @Override
        List<ItemStack> collectOf(Entity player) {
            if (player instanceof HumanEntity) {
                List<ItemStack> rev = new ArrayList<>(Arrays.asList(((HumanEntity) player).getEquipment().getArmorContents()));
                rev.add(((HumanEntity) player).getEquipment().getItemInMainHand());
                rev.add(((HumanEntity) player).getEquipment().getItemInOffHand());
                return rev;
            }
            return new ArrayList<>();
        }
    }

    private static class Impl_RPGInventory extends Equipment {

        private Equipment equipment;

        private Impl_RPGInventory(Equipment equipment) {
            this.equipment = equipment;
        }

        @Override
        List<ItemStack> collectOf(Entity player) {
            List<ItemStack> rev = equipment.collectOf(player);
            if (MConfig.General.useRPGInventory && player instanceof Player) {
                rev.addAll(InventoryAPI.getPassiveItems((Player) player));
                rev.addAll(InventoryAPI.getActiveItems((Player) player));
            }
            List<ItemStack> filtered = new ArrayList<>();
            rev.forEach(itemStack -> {
                if (filtered.stream().noneMatch(i -> i.isSimilar(itemStack)))
                    filtered.add(itemStack);
            });
            return filtered;
        }
    }

}
