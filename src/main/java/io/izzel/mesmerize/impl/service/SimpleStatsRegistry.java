package io.izzel.mesmerize.impl.service;

import io.izzel.mesmerize.api.DefaultStats;
import io.izzel.mesmerize.api.Stats;
import io.izzel.mesmerize.api.service.StatsRegistry;
import io.izzel.mesmerize.api.slot.StatsSlot;
import io.izzel.mesmerize.api.slot.StatsSlots;
import io.izzel.taboolib.module.inject.TInject;
import io.izzel.taboolib.module.locale.TLocale;
import io.izzel.taboolib.module.locale.logger.TLogger;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class SimpleStatsRegistry implements StatsRegistry {

    @TInject
    private static TLogger LOGGER;

    private final Map<String, Stats<?>> stats = new HashMap<>();
    private final Map<String, StatsSlot> slots = new LinkedHashMap<>();

    public SimpleStatsRegistry() {
        this.registerSlot(StatsSlots.HELMET);
        this.registerSlot(StatsSlots.CHESTPLATE);
        this.registerSlot(StatsSlots.LEGGINGS);
        this.registerSlot(StatsSlots.BOOTS);
        this.registerSlot(StatsSlots.MAIN_HAND);
        this.registerSlot(StatsSlots.OFF_HAND);
        try {
            for (Field field : DefaultStats.class.getFields()) {
                Object o = field.get(null);
                if (o instanceof Stats) {
                    this.registerStats((Stats<?>) o);
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void registerStats(Stats<?> stats) {
        this.stats.put(stats.getId(), stats);
        Stats<?> old = this.stats.put(stats.getKey().getKey(), stats);
        if (old != null) {
            LOGGER.warn(TLocale.asString("load.name_override", stats.getKey().getKey(), old.getKey(), stats.getKey()));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<Stats<T>> getStats(String id) {
        return Optional.ofNullable((Stats<T>) this.stats.get(id));
    }

    @Override
    public Collection<Stats<?>> getStats() {
        return new HashSet<>(this.stats.values());
    }

    @Override
    public void registerSlot(StatsSlot slot) {
        this.slots.put(slot.getId(), slot);
    }

    @Override
    public Optional<StatsSlot> getSlot(String id) {
        return Optional.ofNullable(this.slots.get(id));
    }

    @Override
    public Collection<StatsSlot> getSlots() {
        return Collections.unmodifiableCollection(this.slots.values());
    }
}
