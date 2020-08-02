package io.izzel.mesmerize.impl.util;

import io.izzel.mesmerize.api.Stats;
import io.izzel.mesmerize.api.service.StatsService;
import io.izzel.mesmerize.api.visitor.StatsVisitor;
import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.VisitMode;
import io.izzel.mesmerize.api.visitor.impl.AbstractStatsHolder;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class PersistentStatsReader extends AbstractStatsHolder {

    private final PersistentDataContainer container;

    public PersistentStatsReader(PersistentDataContainer container) {
        this.container = container;
    }

    @Override
    public Set<Stats<?>> keySet() {
        Set<Stats<?>> set = new HashSet<>();
        for (String key : Util.mapOfContainer(this.container).keySet()) {
            Optional<Stats<Object>> optional = StatsService.instance().getRegistry().getStats(key);
            optional.ifPresent(set::add);
        }
        return set;
    }

    @Override
    public boolean containsKey(Stats<?> stats) {
        return Util.mapOfContainer(this.container).containsKey(stats.getId());
    }

    @Override
    public void accept(StatsVisitor visitor, VisitMode mode) {
        Map<String, ?> map = Util.mapOfContainer(this.container);
        for (String s : map.keySet()) {
            Optional<Stats<Object>> optional = StatsService.instance().getRegistry().getStats(s);
            if (optional.isPresent()) {
                ValueVisitor valueVisitor = visitor.visitStats(optional.get());
                new PersistentValueReader(this.container, Util.fromString(s)).accept(valueVisitor, mode);
            }
        }
        visitor.visitEnd();
    }
}
