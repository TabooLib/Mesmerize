package io.izzel.mesmerize.api;

import io.izzel.mesmerize.api.visitor.StatsValue;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class Stats<T> {

    private final String id;
    private final BiFunction<StatsValue<T>, StatsValue<T>, StatsValue<T>> onMerge;
    private final Supplier<StatsValue<T>> supplier;

    protected Stats(String id, BiFunction<StatsValue<T>, StatsValue<T>, StatsValue<T>> onMerge, Supplier<StatsValue<T>> supplier) {
        this.id = id;
        this.onMerge = onMerge;
        this.supplier = supplier;
    }

    public String getId() {
        return id;
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
        return Objects.equals(id, stats.id);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Stats{" +
            "id='" + id + '\'' +
            '}';
    }

    public static <T> Stats<T> of(String id, Supplier<StatsValue<T>> supplier) {
        return new Stats<>(id, (a, b) -> b, supplier);
    }

    public static <T> Stats<T> of(String id, Supplier<StatsValue<T>> supplier, BiFunction<StatsValue<T>, StatsValue<T>, StatsValue<T>> onMerge) {
        return new Stats<>(id, onMerge, supplier);
    }
}
