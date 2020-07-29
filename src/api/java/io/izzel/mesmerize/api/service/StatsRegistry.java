package io.izzel.mesmerize.api.service;

import io.izzel.mesmerize.api.Stats;

import java.util.Optional;

public interface StatsRegistry {

    void registerStats(Stats<?> stats);

    Optional<Stats<?>> getStats(String id);
}
