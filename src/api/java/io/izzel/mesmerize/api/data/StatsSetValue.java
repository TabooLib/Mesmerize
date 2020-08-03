package io.izzel.mesmerize.api.data;

import com.google.common.base.Preconditions;
import io.izzel.mesmerize.api.Stats;
import io.izzel.mesmerize.api.service.StatsService;
import io.izzel.mesmerize.api.visitor.MapVisitor;
import io.izzel.mesmerize.api.visitor.StatsHolder;
import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.VisitMode;
import io.izzel.mesmerize.api.visitor.impl.AbstractMapVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractStatsVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractValue;
import io.izzel.mesmerize.api.visitor.impl.AbstractValueVisitor;
import io.izzel.mesmerize.api.visitor.util.LazyStatsHolder;
import io.izzel.mesmerize.api.visitor.util.StatsSet;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.BiFunction;

public class StatsSetValue extends AbstractValue<StatsHolder> {

    private LazyStatsHolder holder;
    private StatsSet statsSet;

    public StatsSetValue() {
    }

    public StatsSetValue(StatsSet statsSet) {
        this.statsSet = statsSet;
    }

    @Override
    public void accept(ValueVisitor visitor, VisitMode mode) {
        if (mode == VisitMode.VALUE) {
            if (holder != null) {
                this.holder.accept(visitor.visitStats(), mode);
            } else {
                this.statsSet.accept(visitor.visitStats(), mode);
            }
        } else if (mode == VisitMode.DATA) {
            if (holder != null) {
                visitor.visitString(holder.getId());
            } else {
                MapVisitor mapVisitor = visitor.visitMap();
                statsSet.accept(new AbstractStatsVisitor(null) {
                    @Override
                    public <T> ValueVisitor visitStats(@NotNull Stats<T> stats) {
                        return mapVisitor.visit(stats.getId());
                    }

                    @Override
                    public void visitEnd() {
                        mapVisitor.visitEnd();
                    }
                }, mode);
            }
            visitor.visitEnd();
        }
    }

    @Override
    public void visitString(String s) {
        Preconditions.checkArgument(holder == null, "initialized");
        this.holder = new LazyStatsHolder(s);
    }

    @Override
    public MapVisitor visitMap() {
        this.statsSet = new StatsSet();
        return new Vis(null);
    }

    private class Vis extends AbstractMapVisitor {

        public Vis(MapVisitor visitor) {
            super(visitor);
        }

        @Override
        public ValueVisitor visit(String key) {
            Optional<Stats<Object>> optional = StatsService.instance().getRegistry().getStats(key);
            if (optional.isPresent()) {
                return statsSet.visitStats(optional.get());
            } else return AbstractValueVisitor.EMPTY;
        }
    }

    @Override
    public StatsHolder get() {
        return holder == null ? statsSet : holder;
    }

    @Override
    public void visitEnd() {
        Preconditions.checkArgument(holder != null || statsSet != null, "empty");
    }

    public static BiFunction<StatsSetValue, StatsSetValue, StatsSetValue> defaultMerger() {
        return (a, b) -> {
            StatsSet set = new StatsSet();
            if (a.holder != null) {
                a.holder.accept(set, VisitMode.VALUE);
            } else {
                set.addAll(a.statsSet);
            }
            if (b.holder != null) {
                b.holder.accept(set, VisitMode.VALUE);
            } else {
                set.addAll(b.statsSet);
            }
            return new StatsSetValue(set);
        };
    }
}
