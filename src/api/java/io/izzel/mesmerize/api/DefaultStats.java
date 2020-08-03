package io.izzel.mesmerize.api;

import io.izzel.mesmerize.api.data.NumberValue;
import io.izzel.mesmerize.api.data.RangeNumberValue;
import org.bukkit.NamespacedKey;

import java.util.List;

public final class DefaultStats {

    public static final Stats<List<NumberValue<Double>>> DAMAGE = rangeRelativeStats("damage");
    public static final Stats<List<NumberValue<Double>>> PVP_DAMAGE = rangeRelativeStats("pvp_damage");
    public static final Stats<List<NumberValue<Double>>> PVE_DAMAGE = rangeRelativeStats("pve_damage");
    public static final Stats<List<NumberValue<Double>>> DEFENSE = rangeRelativeStats("defense");
    public static final Stats<List<NumberValue<Double>>> PVP_DEFENSE = rangeRelativeStats("pvp_defense");
    public static final Stats<List<NumberValue<Double>>> PVE_DEFENSE = rangeRelativeStats("pve_defense");

    @SuppressWarnings("deprecation")
    private static NamespacedKey key(String id) {
        return new NamespacedKey("mesmerize", id);
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
