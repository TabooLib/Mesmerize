package io.izzel.mesmerize.api.service;

import io.izzel.mesmerize.api.cause.CauseManager;
import org.bukkit.Bukkit;

import java.util.Objects;

public interface StatsService {

    StatsManager getStatsManager();

    StatsRegistry getRegistry();

    CauseManager getCauseManager();

    static StatsService instance() {
        return Objects.requireNonNull(Bukkit.getServicesManager().load(StatsService.class));
    }
}
