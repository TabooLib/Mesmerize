package io.izzel.mesmerize.impl.util.visitor;

import io.izzel.mesmerize.api.Stats;
import io.izzel.mesmerize.api.service.StatsService;
import io.izzel.mesmerize.api.visitor.StatsValue;
import io.izzel.mesmerize.api.visitor.StatsVisitor;
import io.izzel.mesmerize.api.visitor.VisitMode;
import io.izzel.mesmerize.api.visitor.impl.AbstractStatsHolder;
import io.izzel.mesmerize.impl.util.Util;
import io.izzel.mesmerize.impl.util.visitor.external.ExternalReader;
import io.izzel.mesmerize.impl.util.visitor.external.ExternalTrackingVisitor;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class PersistentStatsReader extends AbstractStatsHolder {

    private final PersistentDataContainer container;
    private final ExternalReader externalReader;

    public PersistentStatsReader(PersistentDataContainer container, ExternalReader externalReader) {
        this.container = container;
        this.externalReader = externalReader;
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
                StatsValue<Object> newValue = optional.get().newValue();
                ExternalReader child = externalReader.child(optional.get().getKey());
                if (child.isVirtual()) {
                    new PersistentValueReader(this.container, optional.get().getKey()).accept(newValue, mode);
                    newValue.accept(visitor.visitStats(optional.get()), mode);
                } else {
                    new PersistentValueReader(this.container, optional.get().getKey()).accept(new ExternalTrackingVisitor.ValueTracker(newValue, child), mode);
                    newValue.accept(new ExternalTrackingVisitor.ValueTracker(visitor.visitStats(optional.get()), child), mode);
                }
            }
        }
        visitor.visitEnd();
    }
}
