package io.izzel.mesmerize.impl.element;

import io.izzel.mesmerize.api.Stats;
import io.izzel.mesmerize.api.display.DisplayPane;
import io.izzel.mesmerize.api.visitor.StatsValue;
import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractStatsVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractValueVisitor;
import org.jetbrains.annotations.NotNull;

public class DisplayVisitor extends AbstractStatsVisitor {

    private final DisplayPane displayPane;

    public DisplayVisitor(DisplayPane displayPane) {
        super(null);
        this.displayPane = displayPane;
    }

    @Override
    public <T> ValueVisitor visitStats(@NotNull Stats<T> stats) {
        StatsValue<T> newValue = stats.newValue();
        return new AbstractValueVisitor(newValue) {
            @Override
            public void visitEnd() {
                super.visitEnd();
                stats.displayValue(newValue, displayPane);
            }
        };
    }
}
