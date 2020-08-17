package io.izzel.mesmerize.impl.service;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import io.izzel.mesmerize.api.cause.CauseManager;
import io.izzel.mesmerize.api.event.DamageCalculator;
import io.izzel.mesmerize.api.event.StatsRefreshEvent;
import io.izzel.mesmerize.api.service.ElementFactory;
import io.izzel.mesmerize.api.service.StatsManager;
import io.izzel.mesmerize.api.service.StatsRegistry;
import io.izzel.mesmerize.api.service.StatsService;
import io.izzel.mesmerize.api.visitor.StatsHolder;
import io.izzel.mesmerize.api.visitor.StatsVisitor;
import io.izzel.mesmerize.api.visitor.VisitMode;
import io.izzel.mesmerize.api.visitor.impl.AbstractStatsHolder;
import io.izzel.mesmerize.api.visitor.util.StatsAsMapVisitor;
import io.izzel.mesmerize.api.visitor.util.StatsSet;
import io.izzel.mesmerize.impl.Mesmerize;
import io.izzel.mesmerize.impl.config.spec.ConfigSpec;
import io.izzel.mesmerize.impl.event.SimpleCalculator;
import io.izzel.mesmerize.impl.util.Util;
import io.izzel.mesmerize.impl.util.visitor.EntityReader;
import io.izzel.mesmerize.impl.util.visitor.PersistentStatsReader;
import io.izzel.mesmerize.impl.util.visitor.PersistentStatsWriter;
import io.izzel.mesmerize.impl.util.visitor.external.ExternalReader;
import io.izzel.mesmerize.impl.util.visitor.external.ExternalWriter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class SimpleStatsService implements StatsService {

    private final StatsManager statsManager = new SimpleStatsManager();
    private final StatsRegistry statsRegistry = new SimpleStatsRegistry();
    private final CauseManager causeManager = new SimpleCauseManager();
    private final ElementFactory elementFactory = new SimpleElementFactory();
    private final DamageCalculator calculator = new SimpleCalculator();

    private final Set<Integer> entityLock = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private final CacheLoader<Entity, StatsSet> cacheLoader = entity -> {
        if (!entity.isValid() || entity.isDead()) {
            return new StatsSet();
        }
        Callable<StatsSet> callable = () -> {
            StatsSet statsSet = new StatsSet();
            newEntityReader(entity).accept(statsSet, VisitMode.VALUE);
            Bukkit.getPluginManager().callEvent(new StatsRefreshEvent(entity, statsSet));
            return statsSet;
        };
        if (Bukkit.isPrimaryThread()) {
            return callable.call();
        } else {
            try {
                entityLock.add(entity.getEntityId());
                return Bukkit.getScheduler().callSyncMethod(Mesmerize.instance(), callable).get();
            } finally {
                entityLock.remove(entity.getEntityId());
            }
        }
    };

    private final LoadingCache<Entity, StatsSet> statsSetCache = Caffeine
        .newBuilder()
        .refreshAfterWrite(ConfigSpec.spec().performance().entityStatsCacheMs(), TimeUnit.MILLISECONDS)
        .build(cacheLoader);

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
    public StatsSet cachedSetFor(@NotNull Entity entity) {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("async stats get");
        }
        if (entityLock.contains(entity.getEntityId())) {
            try {
                return cacheLoader.load(entity);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            return statsSetCache.get(entity);
        }
    }

    @Override
    public StatsSet refreshCache(@NotNull Entity entity, boolean immediate) {
        this.statsSetCache.invalidate(entity);
        if (immediate) {
            return this.cachedSetFor(entity);
        } else {
            Bukkit.getScheduler().runTask(Mesmerize.instance(), () -> this.cachedSetFor(entity));
            return null;
        }
    }

    @Override
    public StatsHolder newStatsHolder(@NotNull PersistentDataContainer container) {
        if (container.has(Util.STATS_STORE, PersistentDataType.TAG_CONTAINER)) {
            return new PersistentStatsReader(container.get(Util.STATS_STORE, PersistentDataType.TAG_CONTAINER),
                new ExternalReader(container.get(Util.EXTERNAL_STORE, PersistentDataType.TAG_CONTAINER)));
        } else {
            return AbstractStatsHolder.EMPTY;
        }
    }

    @Override
    public StatsVisitor newStatsWriter(@NotNull PersistentDataContainer container) {
        return new PersistentStatsWriter(container);
    }

    @Override
    public StatsVisitor newExternalWriter(@NotNull PersistentDataContainer container) {
        return new StatsAsMapVisitor(new ExternalWriter(container, Util.EXTERNAL_STORE));
    }

    @Override
    public StatsHolder newEntityReader(@NotNull Entity entity) {
        return new EntityReader(entity);
    }

    @Override
    public DamageCalculator getDamageCalculator() {
        return calculator;
    }

    @Override
    public ElementFactory getElementFactory() {
        return elementFactory;
    }
}
