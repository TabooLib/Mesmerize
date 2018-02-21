package it.alian.gun.mesmerize.compat.hook;

import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public abstract class MesmerizeVault {

    private static MesmerizeVault impl;

    public abstract boolean take0(OfflinePlayer player, double amount);

    public abstract boolean give0(OfflinePlayer player, double amount);

    public abstract double get0(OfflinePlayer player);

    public static void init() {
        try {
            impl = new VaultImpl();
        } catch (Throwable t) {
            impl = new AbstractImpl();
        }
    }

    public static boolean take(OfflinePlayer player, double amount) {
        return impl.take0(player, amount);
    }

    public static boolean give(OfflinePlayer player, double amount) {
        return impl.give0(player, amount);
    }

    public static double get(OfflinePlayer player) {
        return impl.get0(player);
    }

    private static class VaultImpl extends MesmerizeVault {

        private Economy economy;

        private VaultImpl() {
            RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
            Validate.notNull(rsp);
            economy = rsp.getProvider();
            Validate.notNull(economy);
        }

        @Override
        public boolean take0(OfflinePlayer player, double amount) {
            return economy.withdrawPlayer(player, amount).transactionSuccess();
        }

        @Override
        public boolean give0(OfflinePlayer player, double amount) {
            return economy.depositPlayer(player, amount).transactionSuccess();
        }

        @Override
        public double get0(OfflinePlayer player) {
            return economy.getBalance(player);
        }
    }

    private static class AbstractImpl extends MesmerizeVault {

        @Override
        public boolean take0(OfflinePlayer player, double amount) {
            return false;
        }

        @Override
        public boolean give0(OfflinePlayer player, double amount) {
            return false;
        }

        @Override
        public double get0(OfflinePlayer player) {
            return 0;
        }
    }
}
