package io.izzel.mesmerize.api.cause;

import io.izzel.mesmerize.api.slot.StatsSlot;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public final class ContextKeys {

    public static final ContextKey<LivingEntity> SOURCE = () -> "source";
    public static final ContextKey<Entity> TARGET = () -> "target";
    public static final ContextKey<StatsSlot> SLOT = () -> "slot";

    private ContextKeys() {
        throw new RuntimeException();
    }
}
