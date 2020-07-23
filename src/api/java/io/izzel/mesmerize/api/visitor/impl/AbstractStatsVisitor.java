package io.izzel.mesmerize.api.visitor.impl;

import io.izzel.mesmerize.api.Stats;
import io.izzel.mesmerize.api.visitor.InfoVisitor;
import io.izzel.mesmerize.api.visitor.StatsValueVisitor;
import io.izzel.mesmerize.api.visitor.StatsVisitor;
import org.jetbrains.annotations.NotNull;

public class AbstractStatsVisitor implements StatsVisitor {

    protected StatsVisitor visitor;

    public AbstractStatsVisitor(StatsVisitor visitor) {
        this.visitor = visitor;
    }

    @Override
    public InfoVisitor visitInfo() {
        if (this.visitor != null) {
            return this.visitor.visitInfo();
        }
        return null;
    }

    @Override
    public <T> StatsValueVisitor visitStats(@NotNull Stats<T> stats) {
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
