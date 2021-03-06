package io.izzel.mesmerize.impl.config;

import io.izzel.mesmerize.api.Stats;
import io.izzel.mesmerize.api.service.StatsService;
import io.izzel.mesmerize.api.visitor.StatsValue;
import io.izzel.mesmerize.api.visitor.StatsVisitor;
import io.izzel.mesmerize.api.visitor.VisitMode;
import io.izzel.mesmerize.api.visitor.impl.AbstractStatsHolder;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class YamlStatsReader extends AbstractStatsHolder {

    private final ConfigurationSection section;

    public YamlStatsReader(ConfigurationSection section) {
        this.section = section;
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
    public boolean containsKey(Stats<?> stats) {
        return this.section.contains(stats.getId());
    }

    @Override
    public void accept(StatsVisitor visitor, VisitMode mode) {
        for (Map.Entry<String, Object> entry : this.section.getValues(false).entrySet()) {
            Optional<Stats<Object>> optional = StatsService.instance().getRegistry().getStats(entry.getKey());
            if (optional.isPresent()) {
                StatsValue<Object> newValue = optional.get().newValue();
                new YamlValueReader(entry.getValue()).accept(newValue, mode);
                newValue.accept(visitor.visitStats(optional.get()), mode);
            }
        }
        visitor.visitEnd();
    }
}
