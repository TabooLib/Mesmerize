package io.izzel.mesmerize.api.visitor.impl;

import io.izzel.mesmerize.api.Stats;
import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.StatsVisitor;
import org.jetbrains.annotations.NotNull;

public class AbstractStatsVisitor implements StatsVisitor {

    protected StatsVisitor visitor;

    public AbstractStatsVisitor(StatsVisitor visitor) {
        this.visitor = visitor;
    }

    @Override
    public <T> ValueVisitor visitStats(@NotNull Stats<T> stats) {
        if (this.visitor != null) {
            return this.visitor.visitStats(stats);
        }
        return null;
    }

    @Override
    public void visitEnd() {
        if (this.visitor != null) {
            this.visitor.visitEnd();
        }
    }
}
