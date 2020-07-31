package io.izzel.mesmerize.impl.service;

import io.izzel.mesmerize.api.service.StatsManager;
import io.izzel.mesmerize.api.visitor.StatsHolder;

import java.util.Optional;

public class SimpleStatsManager implements StatsManager {

    @Override
    public Optional<StatsHolder> get(String id) {
        return Optional.empty();
    }
}
