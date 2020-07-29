package io.izzel.mesmerize.api.visitor.util;

import io.izzel.mesmerize.api.Stats;
import io.izzel.mesmerize.api.visitor.StatsHolder;
import io.izzel.mesmerize.api.visitor.StatsValue;
import io.izzel.mesmerize.api.visitor.StatsVisitor;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class PersistentDataContainerReader implements StatsHolder {

    private final PersistentDataContainer container;

    public PersistentDataContainerReader(PersistentDataContainer container) {
        this.container = container;
    }

    @Override
    public <T> Optional<StatsValue<T>> get(Stats<T> stats) {
        return Optional.empty();
    }

    @Override
    public <T> List<StatsValue<T>> getAll(Stats<T> stats) {
        return null;
    }


    @Override
    public Set<Stats<?>> keySet() {
        return null;
    }

    @Override
    public Collection<Map.Entry<Stats<?>, StatsValue<?>>> entrySet() {
        return null;
    }

    @Override
    public boolean containsKey(Stats<?> stats) {
        return false;
    }

    @Override
    public void accept(StatsVisitor visitor) {

    }
}
