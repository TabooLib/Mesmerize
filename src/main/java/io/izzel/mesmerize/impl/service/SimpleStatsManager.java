package io.izzel.mesmerize.impl.service;

import io.izzel.mesmerize.api.service.StatsManager;
import io.izzel.mesmerize.api.visitor.StatsHolder;
import io.izzel.mesmerize.impl.Mesmerize;

import java.util.Optional;

public class SimpleStatsManager implements StatsManager {

    @Override
    public Optional<StatsHolder> get(String id) {
        return Optional.ofNullable(Mesmerize.instance().getLocalRepository().get(id));
    }
}
