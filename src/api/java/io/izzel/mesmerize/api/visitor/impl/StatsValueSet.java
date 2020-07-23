package io.izzel.mesmerize.api.visitor.impl;

import io.izzel.mesmerize.api.Stats;
import io.izzel.mesmerize.api.visitor.InfoVisitor;
import io.izzel.mesmerize.api.visitor.StatsHolder;
import io.izzel.mesmerize.api.visitor.StatsValue;
import io.izzel.mesmerize.api.visitor.StatsValueVisitor;
import io.izzel.mesmerize.api.visitor.StatsVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class StatsValueSet implements StatsVisitor, StatsHolder.Modifiable {

    private final Map<Stats<?>, StatsValue<?>> map = new HashMap<>();

    @Override
    public <T> Optional<StatsValue<T>> get(Stats<T> stats) {
        List<StatsValue<T>> list = getAll(stats);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<StatsValue<T>> getAll(Stats<T> stats) {
        ArrayList<StatsValue<T>> list = new ArrayList<>();
        StatsValue<?> value = map.get(stats);
        if (value != null) {
            list.add((StatsValue<T>) value);
        }
        return list;
    }

    @Override
    public Set<Stats<?>> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<Map.Entry<Stats<?>, StatsValue<?>>> entrySet() {
        return map.entrySet();
    }

    @Override
    public boolean containsKey(Stats<?> stats) {
        return map.containsKey(stats);
    }

    @Override
    public void accept(StatsVisitor visitor) {
        for (Map.Entry<Stats<?>, StatsValue<?>> entry : entrySet()) {
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
        map.clear();
    }

    @Override
    public InfoVisitor visitInfo() {
        return AbstractInfoVisitor.EMPTY;
    }

    @Override
    public <T> StatsValueVisitor visitStats(@NotNull Stats<T> stats) {
        return new AbstractStatsValueVisitor(stats.newValue()) {
            @SuppressWarnings("unchecked")
            @Override
            public void visitEnd() {
                super.visitEnd();
                StatsValue<T> value = (StatsValue<T>) this.visitor;
                Optional<StatsValue<T>> optional = get(stats);
                StatsValue<T> merged = optional.isPresent() ? stats.mergeValue(optional.get(), value) : value;
                map.put(stats, merged);
            }
        };
    }

    @Override
    public void visitEnd() {
    }
}
