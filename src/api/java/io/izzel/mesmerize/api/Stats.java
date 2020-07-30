package io.izzel.mesmerize.api;

import com.google.common.base.Preconditions;
import io.izzel.mesmerize.api.visitor.StatsValue;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class Stats<T> implements Keyed {

    private static final Pattern PATTERN = Pattern.compile(".+\\.\\d+$");

    private final NamespacedKey key;
    private final BiFunction<StatsValue<T>, StatsValue<T>, StatsValue<T>> onMerge;
    private final Supplier<StatsValue<T>> supplier;

    protected Stats(NamespacedKey key, BiFunction<StatsValue<T>, StatsValue<T>, StatsValue<T>> onMerge, Supplier<StatsValue<T>> supplier) {
        Preconditions.checkArgument(!PATTERN.matcher(key.toString()).matches());
        this.key = key;
        this.onMerge = onMerge;
        this.supplier = supplier;
    }

    public String getId() {
        return key.toString();
    }

    @Override
    @NotNull
    public NamespacedKey getKey() {
        return key;
    }

    public StatsValue<T> mergeValue(StatsValue<T> oldVal, StatsValue<T> newVal) {
        return onMerge.apply(oldVal, newVal);
    }

    public StatsValue<T> newValue() {
        return supplier.get();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stats)) return false;
        Stats<?> stats = (Stats<?>) o;
        return Objects.equals(key, stats.key);
    }

    @Override
    public final int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        return "Stats{" +
            "key=" + key +
            '}';
    }

    public static <T> Stats<T> of(NamespacedKey key, Supplier<StatsValue<T>> supplier) {
        return new Stats<>(key, (a, b) -> b, supplier);
    }

    public static <T> Stats<T> of(NamespacedKey key, Supplier<StatsValue<T>> supplier, BiFunction<StatsValue<T>, StatsValue<T>, StatsValue<T>> onMerge) {
        return new Stats<>(key, onMerge, supplier);
    }
}
