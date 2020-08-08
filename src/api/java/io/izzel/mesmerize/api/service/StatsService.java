package io.izzel.mesmerize.api.service;

import io.izzel.mesmerize.api.cause.CauseManager;
import io.izzel.mesmerize.api.event.DamageCalculator;
import io.izzel.mesmerize.api.visitor.StatsHolder;
import io.izzel.mesmerize.api.visitor.StatsVisitor;
import io.izzel.mesmerize.api.visitor.util.StatsSet;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public interface StatsService {

    StatsManager getStatsManager();

    StatsRegistry getRegistry();

    CauseManager getCauseManager();

    ElementFactory getElementFactory();

    StatsSet cachedSetFor(@NotNull Entity entity);

    void invalidateCache(@NotNull Entity entity);

    default StatsHolder newStatsHolder(@NotNull PersistentDataHolder holder) {
        return newStatsHolder(holder.getPersistentDataContainer());
    }

    StatsHolder newStatsHolder(@NotNull PersistentDataContainer container);

    default StatsVisitor newStatsWriter(@NotNull PersistentDataHolder holder) {
        return newStatsWriter(holder.getPersistentDataContainer());
    }

    StatsVisitor newStatsWriter(@NotNull PersistentDataContainer container);

    StatsVisitor newExternalWriter(@NotNull PersistentDataContainer container);

    StatsHolder newEntityReader(@NotNull Entity entity);

    DamageCalculator getDamageCalculator();

    static StatsService instance() {
        return Objects.requireNonNull(Bukkit.getServicesManager().load(StatsService.class));
    }
}
