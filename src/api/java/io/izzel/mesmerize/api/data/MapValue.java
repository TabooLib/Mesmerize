package io.izzel.mesmerize.api.data;

import com.google.common.collect.ImmutableMap;
import io.izzel.mesmerize.api.Stats;
import io.izzel.mesmerize.api.visitor.MapVisitor;
import io.izzel.mesmerize.api.visitor.StatsValue;
import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.VisitMode;
import io.izzel.mesmerize.api.visitor.impl.AbstractMapVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractValue;
import io.izzel.mesmerize.api.visitor.impl.AbstractValueVisitor;
import org.jetbrains.annotations.Contract;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class MapValue extends AbstractValue<Map<String, StatsValue<?>>> {

    protected final Map<String, Supplier<StatsValue<?>>> dataTypes;
    protected final Map<String, StatsValue<?>> values;

    public MapValue(Map<String, Supplier<StatsValue<?>>> dataTypes) {
        this.dataTypes = dataTypes instanceof ImmutableMap ? dataTypes : ImmutableMap.copyOf(dataTypes);
        values = new HashMap<>();
    }

    public MapValue(Map<String, Supplier<StatsValue<?>>> dataTypes, Map<String, StatsValue<?>> values) {
        this.dataTypes = dataTypes instanceof ImmutableMap ? dataTypes : ImmutableMap.copyOf(dataTypes);
        this.values = values;
    }

    @Override
    public Map<String, StatsValue<?>> get() {
        return values;
    }

    @SuppressWarnings("unchecked")
    public <V extends StatsValue<?>> V get(String key) {
        return (V) values.get(key);
    }

    @Override
    public void accept(ValueVisitor visitor, VisitMode mode) {
        MapVisitor mapVisitor = visitor.visitMap();
        for (Map.Entry<String, StatsValue<?>> entry : values.entrySet()) {
            ValueVisitor valueVisitor = mapVisitor.visit(entry.getKey());
            entry.getValue().accept(valueVisitor, mode);
        }
        mapVisitor.visitEnd();
        visitor.visitEnd();
    }

    @Override
    public MapVisitor visitMap() {
        return new Vis(null);
    }

    private class Vis extends AbstractMapVisitor {

        public Vis(MapVisitor visitor) {
            super(visitor);
        }

        @Override
        public ValueVisitor visit(String key) {
            StatsValue<?> value = dataTypes.get(key).get();
            return new AbstractValueVisitor(value) {
                @Override
                public void visitEnd() {
                    super.visitEnd();
                    values.put(key, value);
                }
            };
        }
    }

    public static MapStatsValueBuilder builder() {
        return new MapStatsValueBuilder();
    }

    public static BiFunction<MapValue, MapValue, MapValue> defaultMerger() {
        return (a, b) -> new MapValue(
            ImmutableMap.<String, Supplier<StatsValue<?>>>builder().putAll(a.dataTypes).putAll(b.dataTypes).build(),
            ImmutableMap.<String, StatsValue<?>>builder().putAll(a.values).putAll(b.values).build()
        );
    }

    public static class MapStatsValueBuilder {

        private final Map<String, Supplier<StatsValue<?>>> map = new HashMap<>();

        @Contract("_, _ -> this")
        @SuppressWarnings("unchecked")
        public <V extends StatsValue<?>> MapStatsValueBuilder put(String key, Supplier<V> supplier) {
            map.put(key, (Supplier<StatsValue<?>>) supplier);
            return this;
        }

        @Contract("_, _ -> this")
        public MapStatsValueBuilder put(String key, Stats<?> stats) {
            map.put(key, stats::newValue);
            return this;
        }

        @Contract("-> new")
        public MapValue build() {
            return new MapValue(map);
        }

        @Contract("-> new")
        public Supplier<MapValue> buildSupplier() {
            return this::build;
        }

        @Contract("_ -> new")
        public <V extends MapValue> Supplier<V> buildSupplier(Function<Map<String, Supplier<StatsValue<?>>>, V> constructor) {
            return () -> constructor.apply(this.map);
        }
    }

    public static MapDeepMergerBuilder deepMerger() {
        return new MapDeepMergerBuilder();
    }

    public static class MapDeepMergerBuilder {

        private final Map<String, BiFunction<StatsValue<?>, StatsValue<?>, StatsValue<?>>> mergers = new HashMap<>();

        @SuppressWarnings("unchecked")
        public <V extends StatsValue<?>> MapDeepMergerBuilder put(String key, BiFunction<V, V, V> function) {
            this.mergers.put(key, (BiFunction<StatsValue<?>, StatsValue<?>, StatsValue<?>>) function);
            return this;
        }

        public BiFunction<MapValue, MapValue, MapValue> build() {
            return build(MapValue::new);
        }

        public <V extends MapValue> BiFunction<V, V, V> build(BiFunction<Map<String, Supplier<StatsValue<?>>>, Map<String, StatsValue<?>>, V> constructor) {
            return (a, b) -> {
                Map<String, StatsValue<?>> map = new HashMap<>(a.values);
                for (Map.Entry<String, StatsValue<?>> entry : b.values.entrySet()) {
                    map.compute(entry.getKey(), (key, oldVal) -> {
                        StatsValue<?> newVal = entry.getValue();
                        if (oldVal == null) {
                            return newVal;
                        } else {
                            BiFunction<StatsValue<?>, StatsValue<?>, StatsValue<?>> function = mergers.get(entry.getKey());
                            if (function != null) {
                                return function.apply(oldVal, newVal);
                            } else {
                                return newVal;
                            }
                        }
                    });
                }
                return constructor.apply(
                    ImmutableMap.<String, Supplier<StatsValue<?>>>builder().putAll(a.dataTypes).putAll(b.dataTypes).build(),
                    map
                );
            };
        }
    }
}
