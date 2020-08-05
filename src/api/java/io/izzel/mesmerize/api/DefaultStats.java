package io.izzel.mesmerize.api;

import io.izzel.mesmerize.api.data.MarkerValue;
import io.izzel.mesmerize.api.data.NumberValue;
import io.izzel.mesmerize.api.data.RangeNumberValue;
import io.izzel.mesmerize.api.data.StatsNumber;
import io.izzel.mesmerize.api.data.StatsSetValue;
import io.izzel.mesmerize.api.data.UUIDValue;
import io.izzel.mesmerize.api.data.complex.PermissionValue;
import io.izzel.mesmerize.api.service.ElementFactory;
import io.izzel.mesmerize.api.visitor.StatsHolder;
import io.izzel.mesmerize.api.visitor.StatsValue;
import org.bukkit.NamespacedKey;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class DefaultStats {

    public static final Stats<List<NumberValue<Double>>> DAMAGE = rangeRelativeStats("damage");
    public static final Stats<List<NumberValue<Double>>> PVP_DAMAGE = rangeRelativeStats("pvp_damage");
    public static final Stats<List<NumberValue<Double>>> PVE_DAMAGE = rangeRelativeStats("pve_damage");
    public static final Stats<List<NumberValue<Double>>> RANGE_DAMAGE = rangeRelativeStats("range_damage");
    public static final Stats<StatsNumber<Double>> REAL_DAMAGE = singleRelativeStats("real_damage");
    public static final Stats<List<NumberValue<Double>>> DEFENSE = rangeRelativeStats("defense");
    public static final Stats<List<NumberValue<Double>>> PVP_DEFENSE = rangeRelativeStats("pvp_defense");
    public static final Stats<List<NumberValue<Double>>> PVE_DEFENSE = rangeRelativeStats("pve_defense");
    public static final Stats<List<NumberValue<Double>>> RANGE_DEFENSE = rangeRelativeStats("range_defense");
    public static final Stats<StatsNumber<Double>> CRIT_DAMAGE = singleRelativeStats("crit_damage");
    public static final Stats<StatsNumber<Double>> CRIT_CHANCE = singleRelativeStats("crit_chance");
    public static final Stats<StatsNumber<Double>> THORNS = singleRelativeStats("thorns");
    public static final Stats<StatsNumber<Double>> MELEE_THORNS = singleRelativeStats("melee_thorns");
    public static final Stats<StatsNumber<Double>> RANGE_THORNS = singleRelativeStats("range_thorns");
    public static final Stats<StatsNumber<Double>> THORNS_CHANCE = singleRelativeStats("thorns_chance");
    public static final Stats<StatsNumber<Double>> LIFESTEAL = singleRelativeStats("lifesteal");
    public static final Stats<StatsNumber<Double>> LIFESTEAL_CHANCE = singleRelativeStats("lifesteal_chance");
    public static final Stats<StatsNumber<Double>> HEALTH = singleRelativeStats("health");
    public static final Stats<List<NumberValue<Double>>> REGENERATION = rangeRelativeStats("regeneration");
    public static final Stats<StatsNumber<Integer>> COMBAT_EXP_BONUS = absoluteInt("combat_exp_bonus");
    public static final Stats<StatsNumber<Integer>> OTHER_EXP_BONUS = absoluteInt("other_exp_bonus");
    public static final Stats<UUID> SOULBIND =
        Stats.builder().key(key("soulbind")).supplying(UUIDValue::new).displaying(UUIDValue.displayName("soulbind")).build();
    public static final Stats<Void> AUTO_BIND =
        Stats.builder().key(key("auto_bind")).supplying(MarkerValue::new).build();
    public static final Stats<StatsNumber<Double>> MOVE_SPEED = singleRelativeStats("move_speed");
    public static final Stats<StatsNumber<Double>> FLY_SPEED = singleRelativeStats("fly_speed");
    public static final Stats<StatsNumber<Double>> ATTACK_SPEED = singleRelativeStats("attack_speed");
    public static final Stats<StatsNumber<Double>> ATTACK_RANGE = singleRelativeStats("attack_range");
    public static final Stats<StatsNumber<Double>> DODGE = singleRelativeStats("dodge");
    public static final Stats<StatsNumber<Double>> ACCURACY = singleRelativeStats("accuracy");
    public static final Stats<Map<String, StatsValue<?>>> PERMISSION = PermissionValue.STATS;
    public static final Stats<StatsNumber<Double>> TRACING = singleRelativeStats("tracing");
    public static final Stats<StatsNumber<Double>> ACCELERATE = singleRelativeStats("accelerate");
    public static final Stats<StatsHolder> STATS_SET =
        Stats.builder().key(key("stats_set")).supplying(StatsSetValue::new).merging(StatsSetValue.defaultMerger())
            .displaying((value, pane) -> ElementFactory.instance().displayHolder(value.get(), pane)).build();

    @SuppressWarnings("deprecation")
    private static NamespacedKey key(String id) {
        return new NamespacedKey("mesmerize", id);
    }

    private static Stats<StatsNumber<Integer>> absoluteInt(String key) {
        return Stats.builder().key(key(key))
            .supplying(
                NumberValue.builder().valueType(int.class).buildSupplier()
            )
            .merging(NumberValue.defaultMerger())
            .displaying(NumberValue.defaultDisplay("stats." + key))
            .build();
    }

    private static Stats<StatsNumber<Double>> singleRelativeStats(String key) {
        return Stats.builder().key(key(key))
            .supplying(
                NumberValue.builder().valueType(double.class).allowRelative().buildSupplier()
            )
            .merging(NumberValue.defaultMerger())
            .displaying(NumberValue.defaultDisplay("stats." + key))
            .build();
    }

    private static Stats<List<NumberValue<Double>>> rangeRelativeStats(String key) {
        return Stats.builder().key(key(key))
            .supplying(RangeNumberValue.rangeValueSupplier(
                NumberValue.builder()
                    .valueType(double.class)
                    .allowRelative().buildSupplier()
            ))
            .merging(RangeNumberValue.sumMerger())
            .displaying(RangeNumberValue.defaultDisplay("stats." + key))
            .build();
    }

    private DefaultStats() {
    }
}
