package io.izzel.mesmerize.api;

import com.google.common.base.Preconditions;
import io.izzel.mesmerize.api.visitor.StatsValue;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Contract;
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
        this.key = key;
        this.onMerge = onMerge;
        this.supplier = supplier;
    }

    public final String getId() {
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

    public static StatsBuilder<?, ?> builder() {
        return new StatsBuilder<>();
    }

    public static class StatsBuilder<E, V extends StatsValue<E>> {

        private NamespacedKey key;
        private Supplier<V> valueSupplier;
        private BiFunction<V, V, V> mergeFunction = (a, b) -> b;

        @Contract("_ -> this")
        public StatsBuilder<E, V> key(@NotNull NamespacedKey key) {
            Preconditions.checkArgument(!PATTERN.matcher(key.toString()).matches());
            this.key = key;
            return this;
        }

        @SuppressWarnings({"unchecked"})
        @Contract("_ -> this")
        public <N_E, N_V extends StatsValue<N_E>> StatsBuilder<N_E, N_V> supplying(@NotNull Supplier<N_V> valueSupplier) {
            this.valueSupplier = (Supplier<V>) valueSupplier;
            return (StatsBuilder<N_E, N_V>) this;
        }

        @Contract("_ -> this")
        public StatsBuilder<E, V> merging(@NotNull BiFunction<V, V, V> function) {
            this.mergeFunction = function;
            return this;
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        public @NotNull Stats<E> build() {
            Preconditions.checkNotNull(this.key, "key");
            Preconditions.checkNotNull(this.valueSupplier, "valueSupplier");
            Preconditions.checkNotNull(this.mergeFunction, "mergeFunction");
            return new Stats<>(key, (BiFunction) mergeFunction, (Supplier) valueSupplier);
        }
    }
}
