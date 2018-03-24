package it.alian.gun.mesmerize.lore;

import it.alian.gun.mesmerize.MConfig;
import it.alian.gun.mesmerize.MTasks;
import it.alian.gun.mesmerize.Mesmerize;
import it.alian.gun.mesmerize.compat.Equipment;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class LoreParser {

    private static final Map<Integer, LoreInfo> infoMap = Collections.synchronizedMap(new HashMap<>());

    public static void remove(int id) {
        infoMap.remove(id);
    }

    public static LoreInfo getByEntityId(int id) {
        return infoMap.getOrDefault(id, LoreInfo.empty());
    }

    public static ItemInfo parseItem(ItemStack itemStack) {
        if (itemStack != null && itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore())
            for (String lore : itemStack.getItemMeta().getLore()) {
                int i = lore.indexOf(':');
                if (i != -1 && i == lore.lastIndexOf(':')) {
                    lore = ChatColor.stripColor(lore).trim();
                    String[] lores = lore.split(":");
                    if (lores.length == 2) {
                        ItemInfo itemInfo = new ItemInfo();
                        String key = lores[0].trim();
                        String value = lores[1].trim();
                        parseItem(key, value, itemInfo);
                        return itemInfo;
                    }
                }
            }
        return ItemInfo.empty();
    }

    private static void parseItem(String key, String value, ItemInfo info) {
        if (MConfig.Prefixes.unbreakable.getName().equals(key)) {
            info.unbreakable += Number.of(value).get();
            return;
        }
        if (MConfig.Prefixes.soulbound.getName().equals(key)) {
            info.soulbound = value;
            return;
        }
        if (MConfig.Prefixes.levelCap.getName().equals(key)) {
            info.levelCap = (int) Number.of(value).get();
            return;
        }
        if (MConfig.Prefixes.permissionCap.getName().endsWith(key)) {
            info.permission = value;
            return;
        }
    }

    /**
     * Return True if unable to use
     *
     * @param itemStack ItemStack to check
     * @param user      User to check
     * @return if unable to use
     */
    public static boolean check(ItemStack itemStack, Entity user) {
        ItemInfo itemInfo = parseItem(itemStack);
        boolean cancel = false;
        if (itemInfo.soulbound != null && !itemInfo.soulbound.equals(user.getName())) {
            user.sendMessage(MConfig.Message.omSoulboundCheck);
            cancel = true;
        }
        if (user instanceof Player && itemInfo.levelCap > ((Player) user).getLevel()) {
            MTasks.execute(() -> user.sendMessage(String.format(MConfig.Message.onLevelCheck, itemInfo.levelCap)));
            cancel = true;
        }
        if (itemInfo.permission != null && user instanceof Player) {
            if (!user.hasPermission(MConfig.General.permissionAlias.getOrDefault(itemInfo.permission, itemInfo.permission))) {
                MTasks.execute(() -> user.sendMessage(String.format(MConfig.Message.onPermissionCheck, itemInfo.permission)));
                cancel = true;
            }
        }
        return cancel;
    }

    public static LoreInfo parseSingleItem(ItemStack itemStack) {
        LoreInfo info = LoreInfo.empty();
        ParseEntityTask.parseItem(info, itemStack);
        return info;
    }

    public static class ParseEntityTask implements Callable<LoreInfo> {

        private LivingEntity entity;

        public ParseEntityTask(LivingEntity entity) {
            this.entity = entity;
        }

        @Override
        public LoreInfo call() {
            if (entity.isValid() && !entity.isDead()) {
                LoreInfo loreInfo = LoreInfo.empty();
                for (ItemStack itemStack : Equipment.collect(entity)) {
                    parseItem(loreInfo, itemStack);
                }
                return loreInfo;
            }
            return LoreInfo.empty();
        }

        private static void parseItem(LoreInfo loreInfo, ItemStack itemStack) {
            if (itemStack != null && itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore()) {
                LoreInfo info = new LoreInfo();
                for (String lore : itemStack.getItemMeta().getLore()) {
                    int i = lore.indexOf(':');
                    if (i != -1 && i == lore.lastIndexOf(':')) {
                        lore = ChatColor.stripColor(lore).trim();
                        String[] lores = lore.split(":");
                        if (lores.length == 2) {
                            String key = lores[0].trim();
                            String value = lores[1].trim();
                            parse(key, value, info);
                        }
                    }
                }
                LoreInfo.merge(loreInfo, info);
            }
        }

        private static void parse(String key, String value, LoreInfo info) {
            if (MConfig.Prefixes.damage.getName().equals(key)) {
                if (MConfig.Prefixes.damage.isSumUp())
                    info.damage += Number.of(value).get();
                else
                    info.damage = Math.max(Number.of(value).get(), info.damage);
                return;
            }
            if (MConfig.Prefixes.playerDamage.getName().equals(key)) {
                if (MConfig.Prefixes.playerDamage.isSumUp())
                    info.playerDamage += Number.of(value).get();
                else
                    info.playerDamage = Math.max(Number.of(value).get(), info.playerDamage);
                return;
            }
            if (MConfig.Prefixes.entityDamage.getName().equals(key)) {
                if (MConfig.Prefixes.entityDamage.isSumUp())
                    info.entityDamage += Number.of(value).get();
                else
                    info.entityDamage = Math.max(Number.of(value).get(), info.entityDamage);
                return;
            }
            if (MConfig.Prefixes.bowDamage.getName().equals(key)) {
                if (MConfig.Prefixes.bowDamage.isSumUp())
                    info.bowDamage += Number.of(value).get();
                else
                    info.bowDamage = Math.max(Number.of(value).get(), info.bowDamage);
                return;
            }
            if (MConfig.Prefixes.realDamage.getName().equals(key)) {
                if (MConfig.Prefixes.realDamage.isSumUp())
                    info.realDamage += Number.of(value).get();
                else
                    info.realDamage = Math.max(Number.of(value).get(), info.realDamage);
                return;
            }
            if (MConfig.Prefixes.lifeStealChance.getName().equals(key)) {
                Number number = Number.of(value);
                if (MConfig.Prefixes.lifeStealChance.isSumUp()) {
                    info.lifeStealChance += number.get();
                } else {
                    info.lifeStealChance = number.get();
                }
                info.isLifeSteal = Math.random() < info.lifeStealChance;
                return;
            }
            if (info.isLifeSteal && MConfig.Prefixes.lifeSteal.getName().equals(key)) {
                if (MConfig.Prefixes.lifeSteal.isSumUp())
                    info.lifeSteal += Number.of(value).get();
                else
                    info.lifeSteal = Math.max(Number.of(value).get(), info.lifeSteal);
                return;
            }
            if (MConfig.Prefixes.criticalChance.getName().equals(key)) {
                Number number = Number.of(value);
                if (MConfig.Prefixes.criticalChance.isSumUp()) {
                    info.criticalChance += number.get();
                } else {
                    info.criticalChance = number.get();
                }
                info.isCritical = Math.random() < info.criticalChance;
                return;
            }
            if (info.isCritical && MConfig.Prefixes.criticalDamage.getName().equals(key)) {
                if (MConfig.Prefixes.criticalDamage.isSumUp())
                    info.criticalDamage += Number.of(value).get();
                else
                    info.criticalDamage = Math.max(Number.of(value).get(), info.criticalDamage);
                return;
            }
            if (MConfig.Prefixes.defense.getName().equals(key)) {
                if (MConfig.Prefixes.defense.isSumUp())
                    info.defense += Number.of(value).get();
                else
                    info.defense = Math.max(Number.of(value).get(), info.defense);
                return;
            }
            if (MConfig.Prefixes.playerDefense.getName().equals(key)) {
                if (MConfig.Prefixes.playerDefense.isSumUp())
                    info.playerDefense += Number.of(value).get();
                else
                    info.playerDefense = Math.max(Number.of(value).get(), info.playerDefense);
                return;
            }
            if (MConfig.Prefixes.entityDefense.getName().equals(key)) {
                if (MConfig.Prefixes.entityDefense.isSumUp())
                    info.entityDefense += Number.of(value).get();
                else
                    info.entityDefense = Math.max(Number.of(value).get(), info.entityDefense);
                return;
            }
            if (MConfig.Prefixes.bowDefense.getName().equals(key)) {
                if (MConfig.Prefixes.bowDefense.isSumUp())
                    info.bowDefense += Number.of(value).get();
                else
                    info.bowDefense = Math.max(Number.of(value).get(), info.bowDefense);
                return;
            }
            if (MConfig.Prefixes.reflectChance.getName().equals(key)) {
                Number number = Number.of(value);
                if (MConfig.Prefixes.reflectChance.isSumUp()) {
                    info.reflectChance += number.get();
                } else {
                    info.reflectChance = number.get();
                }
                info.isReflect = Math.random() < info.reflectChance;
                return;
            }
            if (info.isReflect) {
                if (MConfig.Prefixes.reflect.getName().equals(key)) {
                    if (MConfig.Prefixes.reflect.isSumUp())
                        info.reflect += Number.of(value).get();
                    else
                        info.reflect = Math.max(Number.of(value).get(), info.reflect);
                    return;
                }
                if (MConfig.Prefixes.meleeReflect.getName().equals(key)) {
                    if (MConfig.Prefixes.meleeReflect.isSumUp())
                        info.meleeReflect += Number.of(value).get();
                    else
                        info.meleeReflect = Math.max(Number.of(value).get(), info.meleeReflect);
                    return;
                }
                if (MConfig.Prefixes.rangeReflect.getName().equals(key)) {
                    if (MConfig.Prefixes.rangeReflect.isSumUp())
                        info.rangeReflect += Number.of(value).get();
                    else
                        info.rangeReflect = Math.max(Number.of(value).get(), info.rangeReflect);
                    return;
                }
            }
            if (MConfig.Prefixes.health.getName().equals(key)) {
                if (MConfig.Prefixes.health.isSumUp())
                    info.health += Number.of(value).get();
                else
                    info.health = Math.max(Number.of(value).get(), info.health);
                return;
            }
            if (MConfig.Prefixes.regeneration.getName().equals(key)) {
                if (MConfig.Prefixes.regeneration.isSumUp())
                    info.regeneration += Number.of(value).get();
                else
                    info.regeneration = Math.max(Number.of(value).get(), info.regeneration);
                return;
            }
            if (MConfig.Prefixes.moveSpeed.getName().equals(key)) {
                if (MConfig.Prefixes.moveSpeed.isSumUp())
                    info.moveSpeed += Number.of(value).get();
                else
                    info.moveSpeed = Math.max(Number.of(value).get(), info.moveSpeed);
                return;
            }
            if (MConfig.Prefixes.flySpeed.getName().equals(key)) {
                if (MConfig.Prefixes.flySpeed.isSumUp())
                    info.flySpeed += Number.of(value).get();
                else
                    info.flySpeed = Math.max(Number.of(value).get(), info.flySpeed);
                return;
            }
            if (MConfig.Prefixes.attackSpeed.getName().equals(key)) {
                if (MConfig.Prefixes.attackSpeed.isSumUp())
                    info.attackSpeed += Number.of(value).get();
                else
                    info.attackSpeed = Math.max(Number.of(value).get(), info.attackSpeed);
                return;
            }
            if (MConfig.Prefixes.attackExpModifier.getName().equals(key)) {
                if (MConfig.Prefixes.attackExpModifier.isSumUp())
                    info.attackExpModifier += Number.of(value).get();
                else
                    info.attackExpModifier = Math.max(Number.of(value).get(), info.attackExpModifier);
                return;
            }
            if (MConfig.Prefixes.otherExpModifier.getName().equals(key)) {
                if (MConfig.Prefixes.otherExpModifier.isSumUp())
                    info.otherExpModifier += Number.of(value).get();
                else
                    info.otherExpModifier = Math.max(Number.of(value).get(), info.otherExpModifier);
                return;
            }
            if (MConfig.Prefixes.accuracy.getName().equals(key)) {
                if (MConfig.Prefixes.accuracy.isSumUp())
                    info.accuracy += Number.of(value).get();
                else
                    info.accuracy = Math.max(Number.of(value).get(), info.accuracy);
                return;
            }
            if (MConfig.Prefixes.dodge.getName().equals(key)) {
                if (MConfig.Prefixes.dodge.isSumUp())
                    info.dodge += Number.of(value).get();
                else
                    info.dodge = Math.max(Number.of(value).get(), info.dodge);
                return;
            }
            if (MConfig.Prefixes.attackRange.getName().equals(key)) {
                if (MConfig.Prefixes.attackRange.isSumUp())
                    info.attackRange += Number.of(value).get();
                else
                    info.attackRange = Math.max(Number.of(value).get(), info.attackRange);
                return;
            }
            if (MConfig.Prefixes.suddenDeath.getName().equals(key)) {
                if (MConfig.Prefixes.suddenDeath.isSumUp())
                    info.suddenDeath += Number.of(value).get();
                else
                    info.suddenDeath = Math.max(Number.of(value).get(), info.suddenDeath);
                return;
            }
        }

    }

    public static void init() {
        Bukkit.getScheduler().runTaskTimer(Mesmerize.instance, () -> {
            Map<Integer, Future<LoreInfo>> map = new HashMap<>();
            for (World world : Bukkit.getWorlds()) {
                for (LivingEntity livingEntity : world.getLivingEntities()) {
                    map.put(livingEntity.getEntityId(), MTasks.submit(new ParseEntityTask((livingEntity))));
                }
            }
            MTasks.execute(() -> {
                synchronized (infoMap) {
                    infoMap.clear();
                    for (Map.Entry<Integer, Future<LoreInfo>> entry : map.entrySet()) {
                        try {
                            infoMap.put(entry.getKey(), entry.getValue().get());
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }, 0, MConfig.Performance.loreUpdateInterval);
    }

}
