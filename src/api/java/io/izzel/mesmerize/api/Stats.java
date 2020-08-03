package io.izzel.mesmerize.api;

import com.google.common.base.Preconditions;
import io.izzel.mesmerize.api.display.DisplayPane;
import io.izzel.mesmerize.api.event.StatsApplyEvent;
import io.izzel.mesmerize.api.visitor.StatsValue;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class Stats<T> implements Keyed {

    private static final Pattern PATTERN = Pattern.compile(".+\\.\\d+$");

    private final NamespacedKey key;
    private final BiFunction<StatsValue<T>, StatsValue<T>, StatsValue<T>> onMerge;
    private final Supplier<StatsValue<T>> supplier;
    private BiConsumer<StatsValue<T>, DisplayPane> onDisplay;

    protected Stats(NamespacedKey key, BiFunction<StatsValue<T>, StatsValue<T>, StatsValue<T>> onMerge, Supplier<StatsValue<T>> supplier, BiConsumer<StatsValue<T>, DisplayPane> onDisplay) {
        this.key = key;
        this.onMerge = onMerge;
        this.supplier = supplier;
        this.onDisplay = onDisplay;
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

    public void setDisplay(BiConsumer<StatsValue<T>, DisplayPane> onDisplay) {
        this.onDisplay = onDisplay;
    }

    public void displayValue(StatsValue<T> value, DisplayPane pane) {
        this.onDisplay.accept(value, pane);
    }

    @SuppressWarnings("unchecked")
    public <V extends StatsValue<T>> void tryApply(V statsValue, @Nullable Event event, Consumer<V> action) {
        StatsApplyEvent applyEvent = new StatsApplyEvent(this, statsValue, event);
        Bukkit.getPluginManager().callEvent(applyEvent);
        if (!applyEvent.isCancelled()) {
            action.accept((V) applyEvent.getValue());
        }
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
        private BiConsumer<V, DisplayPane> displayFunction = (a, b) -> {};

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

        @Contract("_ -> this")
        public StatsBuilder<E, V> displaying(@NotNull BiConsumer<V, DisplayPane> displayFunction) {
            this.displayFunction = displayFunction;
            return this;
        }

        @SuppressWarnings({"unchecked"})
        public @NotNull Stats<E> build() {
            Preconditions.checkNotNull(this.key, "key");
            Preconditions.checkNotNull(this.valueSupplier, "valueSupplier");
            Preconditions.checkNotNull(this.mergeFunction, "mergeFunction");
            Preconditions.checkNotNull(this.displayFunction, "displayFunction");
            return new Stats<>(key,
                (BiFunction<StatsValue<E>, StatsValue<E>, StatsValue<E>>) mergeFunction,
                (Supplier<StatsValue<E>>) valueSupplier,
                (BiConsumer<StatsValue<E>, DisplayPane>) this.displayFunction
            );
        }
    }
}
