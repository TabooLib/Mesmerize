package it.alian.gun.mesmerize;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class MConfig {

    private static final transient Gson gson = new GsonBuilder().disableHtmlEscaping()
            .excludeFieldsWithModifiers(Modifier.TRANSIENT).create();

    public static boolean debug = false;
    private static Prefixes prefix = new Prefixes();
    private static CombatMessage combatMessage = new CombatMessage();
    private static General general = new General();
    private static Performance performance = new Performance();
    private static Message message = new Message();
    private static Advanced advanced = new Advanced();
    private static Misc misc = new Misc();

    public static void init() {
        Mesmerize.instance.saveDefaultConfig();
        load();
        save();
        MLocale.GENERAL_CONFIG_LOAD.console();
    }

    public static void save() {
        Yaml yaml = new Yaml();
        File configFile = new File(Mesmerize.instance.getDataFolder(), "config.yml");
        Map map;
        try {
            map = gson.fromJson(gson.toJson(new MConfig()), HashMap.class);
            YamlConfiguration yamlConfiguration = new YamlConfiguration();
            yamlConfiguration.loadFromString(yaml.dump(map));
            Files.write(yamlConfiguration.saveToString().getBytes(Charset.forName("utf-8")), configFile);
        } catch (IOException | InvalidConfigurationException e) {
            MLocale.WARN_CONFIG_LOAD.console();
        }
    }

    public static void load() {
        Yaml yaml = new Yaml();
        File configFile = new File(Mesmerize.instance.getDataFolder(), "config.yml");
        Map map;
        try {
            map = (Map) yaml.load(Files.toString(configFile, Charset.forName("utf-8")));
            gson.fromJson(gson.toJson(map), MConfig.class);
        } catch (IOException e) {
            MLocale.WARN_CONFIG_LOAD.console();
        }
    }

    public static class General {
        public static boolean breakOnDurabilityOff = true;
        public static boolean useRPGInventory = true;
        public static boolean useSkillApi = true;
        public static boolean enableHealthControl = true;
        public static boolean healthScaled = true;
        public static int healthScale = 20;
        public static int minimalHealth = 1;
        public static int maximumHealth = Integer.MAX_VALUE;
        public static double minimalMovespeed = 0.03;
        public static double minimalFlyspeed = 0.03;
        public static double baseMovespeed = 0.2;
        public static double baseFlyspeed = 0.3;
        public static float baseHealth = 20.0F;
        public static long regenInterval = 10;
        public static double baseAttackSpeed = 18;
        public static double baseAttackRange = 6;
        public static double baseAccuracy = 0.98;
        public static double baseDodge = 0.02;
        public static double[] durabilityWarnThreshold = {0.25, 0.5, 0.75, 0.9, 0.95};
        public static boolean ignoreShieldBlocking = false;
    }

    public static class Misc {
        public static boolean enableSplashParticles = true;
        public static String[] splashParticles = {"CRIT", "MAGIC_CRIT", "MOBSPAWNER_FLAMES", "COLOURED_DUST", "FLAME",
                "SNOW_SHOVEL", "PORTAL", "LAVA_POP", "PARTICLE_SMOKE", "LARGE_SMOKE"};
        public static int particleAmount = 200;
        public static int particleVisibleRange = 36;
    }

    public static class Performance {
        public static long loreUpdateInterval = 10;
        public static int maxAttackRange = 128;
        public static boolean enableLongerRange = true;
        public static int workerThreads = Runtime.getRuntime().availableProcessors();
    }

    public static class Advanced {
        public static boolean enableCustomAttackExpression = false;
        public static String customAttackExpression = "function max(a, b) { return a > b ? a : b; }\n" +
                "max((base + damage - armor), attack.getRealDamage());";
    }

    public static class Message {
        public static String omSoulboundCheck = "§c你不是这件物品的主人，所以你不能这样做！";
        public static String onDurabilityItemDrop = "§c物品 %s 耐久耗尽，已经掉落在原地！";
        public static String onDurabilityWarn = "§c你的物品 %s 耐久已不足 %.2f%% 。";
        public static String onPriceEvaluate = "§a你手中的物品的价值为§d %.2f §a。";
        public static String onLevelCheck = "§c你的等级没有达到 %d 级，所以你不能使用这件物品！";
    }

    public static class CombatMessage {
        public static boolean showOnDamage = true;
        public static boolean showOnCritical = true;
        public static boolean showOnLifeSteal = true;
        public static boolean showOnReflect = true;
        public static boolean showOnSuddenDeath = true;
        public static boolean showOnDodge = true;
        public static boolean showOnMiss = true;
        public static String onDamage = "§e你对 %s 造成了 %.2f 点伤害。";
        public static String onCritical = "§e你对 %s 造成了 %.2f 的暴击伤害。";
        public static String onLifeSteal = "§e你吸取了 %s 的 %.2f 点生命值。";
        public static String onReflect = "§e你将 %.2f 伤害反弹给了 %s 。";
        public static String onSuddenDeath = "§c你对 %s 造成了 %.2f 的会心一击！";
        public static String onDodge = "§b你闪避了 %s 的攻击！";
        public static String onMiss = "§c你没有命中 %s ！";
    }

    public static class Prefixes {
        public static Stats damage = new Stats("伤害", "§c", 4, true);
        public static Stats playerDamage = new Stats("PVP伤害", "§c", 4, true);
        public static Stats entityDamage = new Stats("PVE伤害", "§c", 4, true);
        public static Stats bowDamage = new Stats("弓箭伤害", "§c", 4, true);
        public static Stats realDamage = new Stats("真实伤害", "§c", 4, true);
        public static Stats criticalDamage = new Stats("暴击加成", "§c", 4, false);
        public static Stats criticalChance = new Stats("暴击几率", "§c", 4, false);
        public static Stats defense = new Stats("护甲", "§e", 4, true);
        public static Stats playerDefense = new Stats("PVP护甲", "§e", 4, true);
        public static Stats entityDefense = new Stats("PVE护甲", "§e", 4, true);
        public static Stats bowDefense = new Stats("弓箭护甲", "§e", 4, true);
        public static Stats reflect = new Stats("反弹", "§3", 4, false);
        public static Stats meleeReflect = new Stats("近战反弹", "§3", 4, false);
        public static Stats rangeReflect = new Stats("远程反弹", "§3", 4, false);
        public static Stats reflectChance = new Stats("反弹几率", "§3", 4, false);
        public static Stats lifeSteal = new Stats("吸血", "§a", 4, false);
        public static Stats lifeStealChance = new Stats("吸血几率", "§a", 4, false);
        public static Stats health = new Stats("生命", "§c", 4, true);
        public static Stats regeneration = new Stats("再生", "§c", 4, true);
        public static Stats attackExpModifier = new Stats("战斗经验加成", "§c", 4, true);
        public static Stats otherExpModifier = new Stats("经验加成", "§b", 4, true);
        public static Stats moveSpeed = new Stats("移动速度", "§b", 4, true);
        public static Stats flySpeed = new Stats("飞行速度", "§b", 4, true);
        public static Stats attackSpeed = new Stats("攻击速度", "§c", 4, true);
        public static Stats soulbound = new Stats("灵魂绑定", "§d", 4, true);
        public static Stats unbreakable = new Stats("不灭", "§d", 4, true);
        public static Stats levelCap = new Stats("等级限制", "§a", 4, true);
        public static Stats attackRange = new Stats("攻击距离", "§3", 6, false);
        public static Stats suddenDeath = new Stats("会心一击", "§c", 6, false);
        public static Stats dodge = new Stats("闪避", "§b", 4, true);
        public static Stats accuracy = new Stats("命中", "§b", 4, true);
    }

    public static class Stats {
        private String name, color;
        private double valuePerPercentage;
        private boolean sumUp;

        Stats(String name, String color, double value, boolean sumUp) {
            this.name = name;
            this.color = color;
            this.valuePerPercentage = value;
            this.sumUp = sumUp;
        }

        public boolean isSumUp() {
            return sumUp;
        }

        public String getName() {
            return name;
        }

        public String getColor() {
            return color;
        }

        public double getValuePerPercentage() {
            return valuePerPercentage;
        }
    }

}
