package io.izzel.mesmerize.impl.util.visitor;

import io.izzel.mesmerize.api.Stats;
import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractStatsVisitor;
import io.izzel.mesmerize.impl.util.Util;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

public class PersistentStatsWriter extends AbstractStatsVisitor {

    private final PersistentTagWriter writer;

    public PersistentStatsWriter(PersistentDataContainer owner) {
        super(null);
        this.writer = new PersistentTagWriter(owner, Util.STATS_STORE);
    }

    @Override
    public <T> ValueVisitor visitStats(@NotNull Stats<T> stats) {
        return writer.visit(stats.getId());
    }

    @Override
    public void visitEnd() {
        writer.visitEnd();
    }
}
