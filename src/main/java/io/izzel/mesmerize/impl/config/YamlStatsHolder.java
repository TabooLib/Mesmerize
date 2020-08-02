package io.izzel.mesmerize.impl.config;

import io.izzel.mesmerize.api.Stats;
import io.izzel.mesmerize.api.service.StatsService;
import io.izzel.mesmerize.api.visitor.StatsHolder;
import io.izzel.mesmerize.api.visitor.StatsValue;
import io.izzel.mesmerize.api.visitor.StatsVisitor;
import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.VisitMode;
import io.izzel.mesmerize.api.visitor.impl.AbstractValueVisitor;
import io.izzel.mesmerize.api.visitor.util.StatsSet;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class YamlStatsHolder implements StatsHolder {

    private final ConfigurationSection section;

    public YamlStatsHolder(ConfigurationSection section) {
        this.section = section;
    }

    @Override
    public <T> Optional<StatsValue<T>> get(Stats<T> stats) {
        List<StatsValue<T>> list = getAll(stats);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public <T> List<StatsValue<T>> getAll(Stats<T> stats) {
        StatsSet statsSet = new StatsSet() {
            @Override
            public <S> ValueVisitor visitStats(@NotNull Stats<S> s) {
                if (stats != s) {
                    return AbstractValueVisitor.EMPTY;
                } else {
                    return super.visitStats(stats);
                }
            }
        };
        this.accept(statsSet, VisitMode.VALUE);
        return statsSet.getAll(stats);
    }

    @Override
    public Set<Stats<?>> keySet() {
        Set<Stats<?>> set = new HashSet<>();
        for (String key : this.section.getKeys(false)) {
            Optional<Stats<Object>> optional = StatsService.instance().getRegistry().getStats(key);
            optional.ifPresent(set::add);
        }
        return set;
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
        for (Map.Entry<String, Object> entry : this.section.getValues(false).entrySet()) {
            Optional<Stats<Object>> optional = StatsService.instance().getRegistry().getStats(entry.getKey());
            if (optional.isPresent()) {
                Stats<Object> stats = optional.get();
                ValueVisitor valueVisitor = visitor.visitStats(stats);
                new YamlValueReader(entry.getValue()).accept(valueVisitor, mode);
            }
        }
        visitor.visitEnd();
    }
}
