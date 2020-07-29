package io.izzel.mesmerize.api.service;

import org.bukkit.Bukkit;

import java.util.Objects;

public interface StatsService {

    StatsManager getStatsManager();

    StatsRegistry getRegistry();

    static StatsService instance() {
        return Objects.requireNonNull(Bukkit.getServicesManager().load(StatsService.class));
    }
}
