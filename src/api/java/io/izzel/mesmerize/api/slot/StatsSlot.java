package io.izzel.mesmerize.api.slot;

import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public interface StatsSlot {

    String getId();

    Optional<ItemStack> get(LivingEntity entity);

    boolean set(LivingEntity entity, ItemStack stack);
}
