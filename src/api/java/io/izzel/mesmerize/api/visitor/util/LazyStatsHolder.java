package io.izzel.mesmerize.api.visitor.util;

import io.izzel.mesmerize.api.Stats;
import io.izzel.mesmerize.api.service.StatsService;
import io.izzel.mesmerize.api.visitor.StatsHolder;
import io.izzel.mesmerize.api.visitor.StatsValue;
import io.izzel.mesmerize.api.visitor.StatsVisitor;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class LazyStatsHolder implements StatsHolder {

    private final String id;
    private StatsHolder holder;

    public LazyStatsHolder(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    private StatsHolder getHolder() {
        if (holder == null) {
            holder = StatsService.instance().getStatsManager().get(id).orElseThrow(NullPointerException::new);
        }
        return holder;
    }

    @Override
    public <T> Optional<StatsValue<T>> get(Stats<T> stats) {
        return getHolder().get(stats);
    }

    @Override
    public <T> List<StatsValue<T>> getAll(Stats<T> stats) {
        return getHolder().getAll(stats);
    }

    @Override
    public Set<Stats<?>> keySet() {
        return getHolder().keySet();
    }

    @Override
    public Collection<Map.Entry<Stats<?>, StatsValue<?>>> entrySet() {
        return getHolder().entrySet();
    }

    @Override
    public boolean containsKey(Stats<?> stats) {
        return getHolder().containsKey(stats);
    }

    @Override
    public boolean isModifiable() {
        return false;
    }

    @Override
    public void accept(StatsVisitor visitor) {
        getHolder().accept(visitor);
    }

    @Override
    public String toString() {
        return "LazyStatsHolder{" +
            "id='" + id + '\'' +
            '}';
    }
}
