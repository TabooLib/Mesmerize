package io.izzel.mesmerize.api.visitor.util;

import io.izzel.mesmerize.api.Stats;
import io.izzel.mesmerize.api.visitor.MapVisitor;
import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractStatsVisitor;
import org.jetbrains.annotations.NotNull;

public class StatsAsMapVisitor extends AbstractStatsVisitor {

    private final MapVisitor mapVisitor;

    public StatsAsMapVisitor(MapVisitor mapVisitor) {
        super(null);
        this.mapVisitor = mapVisitor;
    }

    @Override
    public <T> ValueVisitor visitStats(@NotNull Stats<T> stats) {
        return mapVisitor.visit(stats.getId());
    }

    @Override
    public void visitEnd() {
        mapVisitor.visitEnd();
    }
}
