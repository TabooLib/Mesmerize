package io.izzel.mesmerize.api.visitor.impl;

import com.google.common.collect.ImmutableSet;
import io.izzel.mesmerize.api.Stats;
import io.izzel.mesmerize.api.visitor.StatsHolder;
import io.izzel.mesmerize.api.visitor.StatsValue;
import io.izzel.mesmerize.api.visitor.StatsVisitor;
import io.izzel.mesmerize.api.visitor.VisitMode;
import io.izzel.mesmerize.api.visitor.util.StatsSet;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class AbstractStatsHolder implements StatsHolder {

    public static final AbstractStatsHolder EMPTY = new AbstractStatsHolder();

    @Override
    public <T, V extends StatsValue<T>> Optional<V> get(Stats<T> stats) {
        List<V> list = getAll(stats);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public <T, V extends StatsValue<T>> List<V> getAll(Stats<T> stats) {
        StatsSet statsSet = new StatsSet();
        this.accept(statsSet, VisitMode.VALUE);
        return statsSet.getAll(stats);
    }

    @Override
    public Set<Stats<?>> keySet() {
        return ImmutableSet.of();
    }

    @Override
    public Collection<Map.Entry<Stats<?>, StatsValue<?>>> entrySet() {
        StatsSet statsSet = new StatsSet();
        this.accept(statsSet, VisitMode.VALUE);
        return statsSet.entrySet();
    }

    @Override
    public boolean containsKey(Stats<?> stats) {
        return keySet().contains(stats);
    }

    @Override
    public void accept(StatsVisitor visitor, VisitMode mode) {
        visitor.visitEnd();
    }
}
