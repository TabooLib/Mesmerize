package io.izzel.mesmerize.api.event;

import io.izzel.mesmerize.api.Stats;

import java.util.Optional;

public interface StatsRegistry {

    void registerStats(Stats<?> stats);

    Optional<Stats<?>> getStat(String id);
}
