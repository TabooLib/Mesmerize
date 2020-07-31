package io.izzel.mesmerize.impl.service;

import io.izzel.mesmerize.api.cause.CauseManager;
import io.izzel.mesmerize.api.service.StatsManager;
import io.izzel.mesmerize.api.service.StatsRegistry;
import io.izzel.mesmerize.api.service.StatsService;

public class SimpleStatsService implements StatsService {

    private final StatsManager statsManager = new SimpleStatsManager();
    private final StatsRegistry statsRegistry = new SimpleStatsRegistry();
    private final CauseManager causeManager = new SimpleCauseManager();

    @Override
    public StatsManager getStatsManager() {
        return statsManager;
    }

    @Override
    public StatsRegistry getRegistry() {
        return statsRegistry;
    }

    @Override
    public CauseManager getCauseManager() {
        return causeManager;
    }
}
