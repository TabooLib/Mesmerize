package it.alian.gun.mesmerize.compat;

import it.alian.gun.mesmerize.MConfig;
import it.alian.gun.mesmerize.MLocale;
import it.alian.gun.mesmerize.Mesmerize;
import it.alian.gun.mesmerize.compat.hook.MesmerizeHolograph;
import it.alian.gun.mesmerize.compat.hook.MesmerizePlaceholder;
import it.alian.gun.mesmerize.compat.hook.MesmerizeSkillApi;
import org.bukkit.Bukkit;

public class Dependency {

    public static void init() {
        if (!Bukkit.getPluginManager().isPluginEnabled("PowerNBT")) {
            MLocale.WARN_DEPENDENCY_MISSING.console("PowerNBT");
            throw new RuntimeException(MLocale.WARN_DEPENDENCY_MISSING.msg("PowerNBT"));
        }
        Equipment.init();
        if (MConfig.General.useRPGInventory && Bukkit.getPluginManager().isPluginEnabled("RPGInventory")) {
            MLocale.GENERAL_HOOK.console("RPGInventory");
        }
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new MesmerizePlaceholder(Mesmerize.instance, "mesmerize").hook();
            MLocale.GENERAL_HOOK.console("PlaceholderAPI");
        }
        if (Bukkit.getPluginManager().isPluginEnabled("SkillAPI")) {
            MesmerizeSkillApi.init();
            MLocale.GENERAL_HOOK.console("SkillAPI");
        }
        MesmerizeHolograph.init();
        if (Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
            MLocale.GENERAL_HOOK.console("HolographicDisplays");
        }
    }

}
