package io.izzel.mesmerize.impl.util;

import io.izzel.mesmerize.api.Stats;
import io.izzel.mesmerize.api.service.StatsService;
import io.izzel.mesmerize.api.visitor.StatsHolder;
import io.izzel.mesmerize.api.visitor.StatsValue;
import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.StatsVisitor;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class PersistentStatsReader implements StatsHolder {

    private final PersistentDataContainer container;
    private Map<Stats<?>, StatsValue<?>> map;

    public PersistentStatsReader(PersistentDataContainer container) {
        this.container = container;
    }

    private Map<Stats<?>, StatsValue<?>> map() {
        if (this.map == null) {
            Set<Stats<?>> keySet = new HashSet<>();
            Set<String> keys = Util.mapOfContainer(container).keySet();
            for (String key : keys) {
                Optional<Stats<?>> optional = StatsService.instance().getRegistry().getStats(Util.fromString(key).getKey());
                optional.ifPresent(keySet::add);
            }
            Map<Stats<?>, StatsValue<?>> map = new HashMap<>();
            for (Stats<?> stats : keySet) {
                int i = Util.typeOfKey(this.container, stats.getKey());
                if (i != 0) {
                    StatsValue<?> statsValue = stats.newValue();
                    Util.dump(this.container, stats.getKey(), statsValue);
                    map.put(stats, statsValue);
                }
            }
            return this.map = map;
        }
        return this.map;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<StatsValue<T>> get(Stats<T> stats) {
        return Optional.ofNullable((StatsValue<T>) map.get(stats));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<StatsValue<T>> getAll(Stats<T> stats) {
        List<StatsValue<T>> ret = new ArrayList<>();
        StatsValue<?> value = map().get(stats);
        if (value != null) ret.add((StatsValue<T>) value);
        return ret;
    }

    @Override
    public Set<Stats<?>> keySet() {
        return map().keySet();
    }

    @Override
    public Collection<Map.Entry<Stats<?>, StatsValue<?>>> entrySet() {
        return map().entrySet();
    }

    @Override
    public boolean containsKey(Stats<?> stats) {
        return map().containsKey(stats);
    }

    @Override
    public void accept(StatsVisitor visitor) {
        for (Map.Entry<Stats<?>, StatsValue<?>> entry : entrySet()) {
            ValueVisitor valueVisitor = visitor.visitStats(entry.getKey());
            entry.getValue().accept(valueVisitor);
        }
    }
}
