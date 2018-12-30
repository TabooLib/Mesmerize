package it.alian.gun.mesmerize;

import com.ilummc.tlib.annotations.TLocalePlugin;
import com.ilummc.tlib.dependency.TDependency;
import me.skymc.taboolib.common.configuration.TConfiguration;
import me.skymc.taboolib.common.inject.TInject;
import org.bukkit.plugin.java.JavaPlugin;

@TLocalePlugin
public class Mesmerize extends JavaPlugin {

    @TInject("config.yml")
    static TConfiguration configuration;

    @Override
    public void onLoad() {
        TDependency.requestLib("org.scala-lang:scala-library:2.12.7", TDependency.MAVEN_REPO, "");
    }

    @Override
    public void onEnable() {
        MesmerizeDelegate.onEnable();
    }
}
