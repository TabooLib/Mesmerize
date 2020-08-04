package io.izzel.mesmerize.impl.service;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import io.izzel.mesmerize.api.cause.CauseManager;
import io.izzel.mesmerize.api.service.ElementFactory;
import io.izzel.mesmerize.api.service.StatsManager;
import io.izzel.mesmerize.api.service.StatsRegistry;
import io.izzel.mesmerize.api.service.StatsService;
import io.izzel.mesmerize.api.visitor.StatsHolder;
import io.izzel.mesmerize.api.visitor.StatsVisitor;
import io.izzel.mesmerize.api.visitor.VisitMode;
import io.izzel.mesmerize.api.visitor.impl.AbstractStatsHolder;
import io.izzel.mesmerize.api.visitor.util.StatsSet;
import io.izzel.mesmerize.impl.util.EntityReader;
import io.izzel.mesmerize.impl.util.PersistentStatsReader;
import io.izzel.mesmerize.impl.util.PersistentStatsWriter;
import io.izzel.mesmerize.impl.util.Util;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class SimpleStatsService implements StatsService {

    private final StatsManager statsManager = new SimpleStatsManager();
    private final StatsRegistry statsRegistry = new SimpleStatsRegistry();
    private final CauseManager causeManager = new SimpleCauseManager();
    private final ElementFactory elementFactory = new SimpleElementFactory();

    private final LoadingCache<LivingEntity, StatsSet> statsSetCache = Caffeine
        .newBuilder()
        .expireAfterWrite(50, TimeUnit.MILLISECONDS)
        .build(entity -> {
            StatsSet statsSet = new StatsSet();
            newEntityReader(entity).accept(statsSet, VisitMode.VALUE);
            return statsSet;
        });

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

    @Override
    public StatsSet cachedSetFor(@NotNull LivingEntity entity) {
        return statsSetCache.get(entity);
    }

    @Override
    public StatsHolder newPersistentHolder(@NotNull PersistentDataContainer container) {
        if (container.has(Util.STATS_STORE, PersistentDataType.TAG_CONTAINER)) {
            return new PersistentStatsReader(container.get(Util.STATS_STORE, PersistentDataType.TAG_CONTAINER));
        } else {
            return AbstractStatsHolder.EMPTY;
        }
    }

    @Override
    public StatsVisitor newPersistentWriter(@NotNull PersistentDataContainer container) {
        return new PersistentStatsWriter(container);
    }

    @Override
    public StatsHolder newEntityReader(@NotNull LivingEntity entity) {
        return new EntityReader(entity);
    }

    @Override
    public ElementFactory getElementFactory() {
        return elementFactory;
    }
}
