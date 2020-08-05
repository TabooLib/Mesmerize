package io.izzel.mesmerize.api.visitor.util;

import io.izzel.mesmerize.api.Stats;
import io.izzel.mesmerize.api.service.StatsService;
import io.izzel.mesmerize.api.visitor.StatsVisitor;
import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractMapVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractValueVisitor;

import java.util.Optional;

public class MapAsStatsVisitor extends AbstractMapVisitor {

    private final StatsVisitor statsVisitor;

    public MapAsStatsVisitor(StatsVisitor statsVisitor) {
        super(null);
        this.statsVisitor = statsVisitor;
    }

    @Override
    public ValueVisitor visit(String key) {
        Optional<Stats<Object>> optional = StatsService.instance().getRegistry().getStats(key);
        if (optional.isPresent()) {
            return statsVisitor.visitStats(optional.get());
        } else return AbstractValueVisitor.EMPTY;
    }

    @Override
    public void visitEnd() {
        statsVisitor.visitEnd();
    }
}
