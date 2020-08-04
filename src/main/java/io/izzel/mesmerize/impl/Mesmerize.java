package io.izzel.mesmerize.impl;

import io.izzel.mesmerize.api.service.StatsRegistry;
import io.izzel.mesmerize.api.service.StatsService;
import io.izzel.mesmerize.impl.config.LocalRepository;
import io.izzel.mesmerize.impl.service.SimpleStatsService;
import io.izzel.taboolib.loader.Plugin;
import io.izzel.taboolib.module.dependency.Dependency;
import io.izzel.taboolib.module.inject.TInject;
import io.izzel.taboolib.module.locale.TLocale;
import io.izzel.taboolib.module.locale.logger.TLogger;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

@Dependency(maven = "com.github.ben-manes.caffeine:caffeine:2.8.5")
public class Mesmerize extends Plugin {

    @TInject
    private static TLogger LOGGER;

    private final LocalRepository localRepository = new LocalRepository();

    public LocalRepository getLocalRepository() {
        return localRepository;
    }

    @Override
    public void onLoading() {
        Bukkit.getServicesManager().register(StatsService.class, new SimpleStatsService(), this, ServicePriority.Normal);
    }

    @Override
    public void onStarting() {
        new Metrics(this, 2198);
    }

    @Override
    public void onActivated() {
        long begin = System.currentTimeMillis();
        StatsRegistry registry = StatsService.instance().getRegistry();
        LOGGER.info(TLocale.asString("general.register", registry.getStats().size(), registry.getSlots().size()));
        this.localRepository.loadAndValidate();
        LOGGER.info(TLocale.asString("general.load", (System.currentTimeMillis() - begin) / 1000D));
    }

    public static Mesmerize instance() {
        return JavaPlugin.getPlugin(Mesmerize.class);
    }
}
