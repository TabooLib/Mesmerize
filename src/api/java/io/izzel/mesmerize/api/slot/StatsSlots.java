package io.izzel.mesmerize.api.slot;

import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class StatsSlots {

    public static final StatsSlot MAIN_HAND = of("main_hand", EntityEquipment::getItemInMainHand, EntityEquipment::setItemInMainHand);
    public static final StatsSlot OFF_HAND = of("off_hand", EntityEquipment::getItemInOffHand, EntityEquipment::setItemInOffHand);
    public static final StatsSlot HELMET = of("helmet", EntityEquipment::getHelmet, EntityEquipment::setHelmet);
    public static final StatsSlot CHESTPLATE = of("chestplate", EntityEquipment::getChestplate, EntityEquipment::setChestplate);
    public static final StatsSlot LEGGINGS = of("leggings", EntityEquipment::getLeggings, EntityEquipment::setLeggings);
    public static final StatsSlot BOOTS = of("boots", EntityEquipment::getBoots, EntityEquipment::setBoots);

    private static StatsSlot of(String id, Function<EntityEquipment, ItemStack> getter, BiConsumer<EntityEquipment, ItemStack> setter) {
        return new StatsSlot() {
            @Override
            public String getId() {
                return id;
            }

            @Override
            public Optional<ItemStack> get(LivingEntity entity) {
                EntityEquipment equipment = entity.getEquipment();
                if (equipment != null) {
                    ItemStack stack = getter.apply(equipment);
                    return stack != null && stack.getAmount() > 0 ? Optional.of(stack) : Optional.empty();
                } else {
                    return Optional.empty();
                }
            }

            @Override
            public boolean set(LivingEntity entity, ItemStack stack) {
                EntityEquipment equipment = entity.getEquipment();
                if (equipment != null) {
                    setter.accept(equipment, stack);
                    return true;
                } else {
                    return false;
                }
            }
        };
    }
}
