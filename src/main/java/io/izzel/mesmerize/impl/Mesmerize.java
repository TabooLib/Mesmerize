package io.izzel.mesmerize.impl;

import io.izzel.mesmerize.api.service.StatsRegistry;
import io.izzel.mesmerize.api.service.StatsService;
import io.izzel.mesmerize.impl.config.LocalRepository;
import io.izzel.mesmerize.impl.config.spec.ConfigSpec;
import io.izzel.mesmerize.impl.event.AttributeListener;
import io.izzel.mesmerize.impl.event.CombatListener;
import io.izzel.mesmerize.impl.event.EntityStatsCacheListener;
import io.izzel.mesmerize.impl.event.PotionApplyTask;
import io.izzel.mesmerize.impl.event.ProjectileListener;
import io.izzel.mesmerize.impl.service.SimpleStatsService;
import io.izzel.mesmerize.impl.util.Updater;
import io.izzel.taboolib.loader.Plugin;
import io.izzel.taboolib.module.dependency.Dependency;
import io.izzel.taboolib.module.inject.TInject;
import io.izzel.taboolib.module.locale.TLocale;
import io.izzel.taboolib.module.locale.logger.TLogger;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;

@Dependency(maven = "com.github.ben-manes.caffeine:caffeine:2.8.5")
public class Mesmerize extends Plugin {

    @TInject
    private static TLogger LOGGER;

    private final LocalRepository localRepository = new LocalRepository();
    private ConfigSpec configSpec;

    public LocalRepository getLocalRepository() {
        return localRepository;
    }

    public ConfigSpec getConfigSpec() {
        return configSpec;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onLoading() {
        this.saveDefaultConfig();
        Yaml yaml = new Yaml(new CustomClassLoaderConstructor(ConfigSpec.class, this.getClassLoader()));
        this.configSpec = yaml.load(this.getConfig().saveToString());
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
        this.getServer().getPluginManager().registerEvents(new CombatListener(), this);
        this.getServer().getPluginManager().registerEvents(new ProjectileListener(), this);
        this.getServer().getPluginManager().registerEvents(new EntityStatsCacheListener(), this);
        this.getServer().getPluginManager().registerEvents(new AttributeListener(), this);
        try {
            Class.forName("com.destroystokyo.paper.event.player.PlayerArmorChangeEvent");
            LOGGER.info(TLocale.asString("load.paper.present"));
            this.getServer().getPluginManager().registerEvents(new EntityStatsCacheListener.Paper(), this);
        } catch (ClassNotFoundException e) {
            LOGGER.info(TLocale.asString("load.paper.absent"));
            this.getServer().getPluginManager().registerEvents(new EntityStatsCacheListener.Spigot(), this);
        }
        new PotionApplyTask().runTaskTimer(this, 0, ConfigSpec.spec().general().potionApplyInterval());
        Updater.start();
        LOGGER.info(TLocale.asString("general.load", (System.currentTimeMillis() - begin) / 1000D));
    }

    @SuppressWarnings("deprecation")
    public void reloadMesmerizeData() {
        this.reloadConfig();
        Yaml yaml = new Yaml(new CustomClassLoaderConstructor(ConfigSpec.class, this.getClassLoader()));
        this.configSpec = yaml.load(this.getConfig().saveToString());
        this.localRepository.loadAndValidate();
    }

    public static Mesmerize instance() {
        return ((Mesmerize) getPlugin());
    }
}
