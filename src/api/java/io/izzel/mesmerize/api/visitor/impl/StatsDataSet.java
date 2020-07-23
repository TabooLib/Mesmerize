package io.izzel.mesmerize.api.visitor.impl;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import io.izzel.mesmerize.api.Stats;
import io.izzel.mesmerize.api.visitor.InfoVisitor;
import io.izzel.mesmerize.api.visitor.StatsHolder;
import io.izzel.mesmerize.api.visitor.StatsValue;
import io.izzel.mesmerize.api.visitor.StatsValueVisitor;
import io.izzel.mesmerize.api.visitor.StatsVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class StatsDataSet implements StatsVisitor, StatsHolder.Modifiable {

    private final ListMultimap<Stats<?>, StatsValue<?>> multimap = MultimapBuilder.hashKeys().arrayListValues().build();

    @Override
    public <T> Optional<StatsValue<T>> get(Stats<T> stats) {
        List<StatsValue<T>> values = getAll(stats);
        return values.isEmpty() ? Optional.empty() : Optional.of(values.get(0));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public <T> List<StatsValue<T>> getAll(Stats<T> stats) {
        return (List) multimap.get(stats);
    }

    @Override
    public Set<Stats<?>> keySet() {
        return multimap.keySet();
    }

    @Override
    public Collection<Map.Entry<Stats<?>, StatsValue<?>>> entrySet() {
        return multimap.entries();
    }

    @Override
    public boolean containsKey(Stats<?> stats) {
        return multimap.containsKey(stats);
    }

    @Override
    public void accept(StatsVisitor visitor) {
        for (Map.Entry<Stats<?>, StatsValue<?>> entry : multimap.entries()) {
            StatsValueVisitor stats = visitor.visitStats(entry.getKey());
            entry.getValue().accept(stats);
        }
        visitor.visitEnd();
    }

    @Override
    public <T> Iterator<StatsValue<T>> iterator(Stats<T> stats) {
        return getAll(stats).iterator();
    }

    @Override
    public void clear() {
        multimap.clear();
    }

    @Override
    public InfoVisitor visitInfo() {
        return AbstractInfoVisitor.EMPTY;
    }

    @Override
    public <T> StatsValueVisitor visitStats(@NotNull Stats<T> stats) {
        return new AdaptiveStatsValue() {
            @Override
            public void visitEnd() {
                super.visitEnd();
                multimap.put(stats, this);
            }
        };
    }

    @Override
    public void visitEnd() {
    }
}
