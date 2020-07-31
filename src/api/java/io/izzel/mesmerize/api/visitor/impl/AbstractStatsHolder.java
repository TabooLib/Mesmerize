package io.izzel.mesmerize.api.visitor.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import io.izzel.mesmerize.api.Stats;
import io.izzel.mesmerize.api.visitor.StatsHolder;
import io.izzel.mesmerize.api.visitor.StatsValue;
import io.izzel.mesmerize.api.visitor.StatsVisitor;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class AbstractStatsHolder implements StatsHolder {

    @Override
    public <T> Optional<StatsValue<T>> get(Stats<T> stats) {
        return Optional.empty();
    }

    @Override
    public <T> List<StatsValue<T>> getAll(Stats<T> stats) {
        return ImmutableList.of();
    }

    @Override
    public Set<Stats<?>> keySet() {
        return ImmutableSet.of();
    }

    @Override
    public Collection<Map.Entry<Stats<?>, StatsValue<?>>> entrySet() {
        return ImmutableSet.of();
    }

    @Override
    public boolean containsKey(Stats<?> stats) {
        return false;
    }

    @Override
    public void accept(StatsVisitor visitor) {
    }
}
