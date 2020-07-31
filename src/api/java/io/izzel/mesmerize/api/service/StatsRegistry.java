package io.izzel.mesmerize.api.service;

import io.izzel.mesmerize.api.Stats;
import io.izzel.mesmerize.api.slot.StatsSlot;

import java.util.Collection;
import java.util.Optional;

public interface StatsRegistry {

    void registerStats(Stats<?> stats);

    <T> Optional<Stats<T>> getStats(String id);

    Collection<Stats<?>> getStats();

    void registerSlot(StatsSlot slot);

    Optional<StatsSlot> getSlot(String id);

    Collection<StatsSlot> getSlots();
}
