package io.izzel.mesmerize.impl;

import io.izzel.mesmerize.api.service.StatsService;
import io.izzel.mesmerize.impl.config.LocalRepository;
import io.izzel.mesmerize.impl.service.SimpleStatsService;
import io.izzel.taboolib.loader.Plugin;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class Mesmerize extends Plugin {

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
        this.localRepository.loadAndValidate();
    }

    public static Mesmerize instance() {
        return JavaPlugin.getPlugin(Mesmerize.class);
    }
}
