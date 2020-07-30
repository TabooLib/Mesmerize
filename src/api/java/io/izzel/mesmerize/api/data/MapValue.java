package io.izzel.mesmerize.api.data;

import com.google.common.collect.ImmutableMap;
import io.izzel.mesmerize.api.Stats;
import io.izzel.mesmerize.api.visitor.MapVisitor;
import io.izzel.mesmerize.api.visitor.StatsValue;
import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractMapVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractValue;
import io.izzel.mesmerize.api.visitor.impl.AbstractValueVisitor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MapValue extends AbstractValue<Map<String, StatsValue<?>>> {

    private final Map<String, Supplier<StatsValue<?>>> dataTypes;
    private final Map<String, StatsValue<?>> values = new HashMap<>();

    public MapValue(Map<String, Supplier<StatsValue<?>>> dataTypes) {
        this.dataTypes = ImmutableMap.copyOf(dataTypes);
    }

    @Override
    public Map<String, StatsValue<?>> get() {
        return values;
    }

    @SuppressWarnings("unchecked")
    public <T> StatsValue<T> get(String key) {
        return (StatsValue<T>) values.get(key);
    }

    @Override
    public void accept(ValueVisitor visitor) {
        MapVisitor mapVisitor = visitor.visitMap();
        for (Map.Entry<String, StatsValue<?>> entry : values.entrySet()) {
            ValueVisitor valueVisitor = mapVisitor.visit(entry.getKey());
            entry.getValue().accept(valueVisitor);
        }
        mapVisitor.visitEnd();
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

    public static class MapStatsValueBuilder {

        private final Map<String, Supplier<StatsValue<?>>> map = new HashMap<>();

        public void put(String key, Supplier<StatsValue<?>> supplier) {
            map.put(key, supplier);
        }

        public void put(String key, Stats<?> stats) {
            map.put(key, stats::newValue);
        }

        public MapValue build() {
            return new MapValue(map);
        }

        public Supplier<MapValue> buildSupplier() {
            return this::build;
        }
    }
}
