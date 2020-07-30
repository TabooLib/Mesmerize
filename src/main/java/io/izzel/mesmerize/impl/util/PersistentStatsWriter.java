package io.izzel.mesmerize.impl.util;

import io.izzel.mesmerize.api.Stats;
import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractValueVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractStatsVisitor;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

public class PersistentStatsWriter extends AbstractStatsVisitor {

    private final PersistentDataContainer container;
    private final PersistentValueWriter writer;

    public PersistentStatsWriter(PersistentDataContainer container) {
        super(null);
        this.container = container;
        this.writer = new PersistentValueWriter(container);
    }

    @Override
    public <T> ValueVisitor visitStats(@NotNull Stats<T> stats) {
        this.writer.visitKey(stats.getKey().toString());
        return new AbstractValueVisitor(this.writer) {

            @Override
            public void visitEnd() {
                super.visitEnd();
                if (this.visitor != null) {
                    container.set();
                }
            }
        };
    }
}
