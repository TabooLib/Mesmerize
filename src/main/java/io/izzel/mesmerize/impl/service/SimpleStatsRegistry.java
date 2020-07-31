package io.izzel.mesmerize.impl.service;

import io.izzel.mesmerize.api.Stats;
import io.izzel.mesmerize.api.service.StatsRegistry;
import io.izzel.mesmerize.api.slot.StatsSlot;
import io.izzel.taboolib.module.inject.TInject;
import io.izzel.taboolib.module.locale.TLocale;
import io.izzel.taboolib.module.locale.logger.TLogger;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SimpleStatsRegistry implements StatsRegistry {

    @TInject
    private static TLogger LOGGER;

    private final Map<String, Stats<?>> stats = new HashMap<>();
    private final Map<String, StatsSlot> slots = new HashMap<>();

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
        return Collections.unmodifiableCollection(this.stats.values());
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
