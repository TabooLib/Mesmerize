package io.izzel.mesmerize.api.service;

import io.izzel.mesmerize.api.cause.CauseManager;
import io.izzel.mesmerize.api.event.DamageCalculator;
import io.izzel.mesmerize.api.visitor.StatsHolder;
import io.izzel.mesmerize.api.visitor.StatsVisitor;
import io.izzel.mesmerize.api.visitor.util.StatsSet;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public interface StatsService {

    StatsManager getStatsManager();

    StatsRegistry getRegistry();

    CauseManager getCauseManager();

    ElementFactory getElementFactory();

    StatsSet cachedSetFor(@NotNull LivingEntity entity);

    default StatsHolder newPersistentHolder(@NotNull PersistentDataHolder holder) {
        return newPersistentHolder(holder.getPersistentDataContainer());
    }

    StatsHolder newPersistentHolder(@NotNull PersistentDataContainer container);

    default StatsVisitor newPersistentWriter(@NotNull PersistentDataHolder holder) {
        return newPersistentWriter(holder.getPersistentDataContainer());
    }

    StatsVisitor newPersistentWriter(@NotNull PersistentDataContainer container);

    StatsHolder newEntityReader(@NotNull LivingEntity entity);

    DamageCalculator getDamageCalculator();

    static StatsService instance() {
        return Objects.requireNonNull(Bukkit.getServicesManager().load(StatsService.class));
    }
}
