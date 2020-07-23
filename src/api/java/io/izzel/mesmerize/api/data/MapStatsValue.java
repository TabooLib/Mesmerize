package io.izzel.mesmerize.api.data;

import com.google.common.collect.ImmutableMap;
import io.izzel.mesmerize.api.Stats;
import io.izzel.mesmerize.api.visitor.StatsHolder;
import io.izzel.mesmerize.api.visitor.StatsValue;
import io.izzel.mesmerize.api.visitor.StatsValueVisitor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class MapStatsValue implements StatsValue<Map<String, StatsValue<?>>> {

    private final Map<String, Supplier<StatsValue<?>>> dataTypes;
    private final Map<String, StatsValue<?>> underlying = new HashMap<>();
    private String readingKey;
    private StatsValue<?> readingValue;

    public MapStatsValue(Map<String, Supplier<StatsValue<?>>> dataTypes) {
        this.dataTypes = ImmutableMap.copyOf(dataTypes);
    }

    @Override
    public Map<String, StatsValue<?>> get() {
        return underlying;
    }

    @Override
    public void accept(StatsValueVisitor visitor) {
        for (Map.Entry<String, StatsValue<?>> entry : underlying.entrySet()) {
            visitor.visitKey(entry.getKey());
            entry.getValue().accept(visitor);
        }
        visitor.visitEnd();
    }

    @Override
    public void visitKey(String key) {
        if (readingKey != null) {
            if (readingValue == null) {
                throw new IllegalArgumentException("Visiting key " + key + " before visiting value, old key is " + readingKey);
            } else {
                readingValue.visitEnd();
                underlying.put(readingKey, readingValue);
            }
        } else if (readingValue != null) {
            throw new IllegalArgumentException("Expect map, found list.");
        }
        if (!dataTypes.containsKey(key)) {
            throw new IllegalArgumentException("Unknown key " + key + " for map value, known keys are " + dataTypes.keySet());
        }
        readingKey = key;
        readingValue = Objects.requireNonNull(dataTypes.get(key).get());
    }

    @Override
    public void visitBoolean(boolean b) {
        readingValue.visitBoolean(b);
    }

    @Override
    public void visitInt(int i) {
        readingValue.visitInt(i);
    }

    @Override
    public void visitLong(long l) {
        readingValue.visitLong(l);
    }

    @Override
    public void visitFloat(float f) {
        readingValue.visitFloat(f);
    }

    @Override
    public void visitDouble(double d) {
        readingValue.visitDouble(d);
    }

    @Override
    public void visitString(String s) {
        readingValue.visitString(s);
    }

    @Override
    public void visitStatsHolder(StatsHolder holder) {
        readingValue.visitStatsHolder(holder);
    }

    @Override
    public void visitEnd() {
        if (readingKey != null) {
            readingValue.visitEnd();
            underlying.put(readingKey, readingValue);
        }
        readingKey = null;
        readingValue = null;
    }

    @Override
    public String toString() {
        return "MapStatsValue{" +
            "underlying=" + underlying +
            '}';
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

        public MapStatsValue build() {
            return new MapStatsValue(map);
        }

        public Supplier<MapStatsValue> buildSupplier() {
            return this::build;
        }
    }
}
