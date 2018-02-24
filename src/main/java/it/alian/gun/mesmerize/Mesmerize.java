package it.alian.gun.mesmerize;

import it.alian.gun.mesmerize.compat.AttackSpeed;
import it.alian.gun.mesmerize.compat.Dependency;
import it.alian.gun.mesmerize.compat.ShieldBlocking;
import it.alian.gun.mesmerize.compat.SplashParticle;
import it.alian.gun.mesmerize.listeners.BattleListener;
import it.alian.gun.mesmerize.listeners.ItemListener;
import it.alian.gun.mesmerize.listeners.MiscListener;
import it.alian.gun.mesmerize.lore.LoreCalculator;
import it.alian.gun.mesmerize.lore.LoreParser;
import it.alian.gun.mesmerize.util.Updater;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.DecimalFormat;

public final class Mesmerize extends JavaPlugin {

    public static Mesmerize instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        long time = System.currentTimeMillis();
        MLocale.init();
        MLocale.GENERAL_VERSION.console(getServer().getClass().getName().split("\\.")[3],
                getDescription().getVersion());
        MConfig.init();
        MTasks.init();
        MCommand.init();
        Dependency.init();
        AttackSpeed.init();
        LoreCalculator.init();
        LoreParser.init();
        BattleListener.init();
        ItemListener.init();
        SplashParticle.init();
        MiscListener.init();
        ShieldBlocking.init();
        Updater.start();
        new Metrics(this);
        MLocale.GENERAL_LOAD.console(new DecimalFormat("0.00")
                .format(((double) System.currentTimeMillis() - (double) time) / 1000D));
    }

    @Override
    public void onDisable() {
        MTasks.unload();
    }
}
