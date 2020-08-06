package io.izzel.mesmerize.api.data;

import com.google.common.base.Preconditions;
import io.izzel.mesmerize.api.visitor.MapVisitor;
import io.izzel.mesmerize.api.visitor.StatsHolder;
import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.VisitMode;
import io.izzel.mesmerize.api.visitor.impl.AbstractValue;
import io.izzel.mesmerize.api.visitor.util.LazyStatsHolder;
import io.izzel.mesmerize.api.visitor.util.MapAsStatsVisitor;
import io.izzel.mesmerize.api.visitor.util.StatsAsMapVisitor;
import io.izzel.mesmerize.api.visitor.util.StatsSet;

import java.util.function.BiFunction;

public class StatsSetValue extends AbstractValue<StatsHolder> {

    private LazyStatsHolder holder;
    private StatsSet statsSet;

    public StatsSetValue() {
    }

    public StatsSetValue(StatsSet statsSet) {
        this.statsSet = statsSet;
    }

    public StatsSetValue(String key) {
        this.holder = new LazyStatsHolder(key);
    }

    @Override
    public void accept(ValueVisitor visitor, VisitMode mode) {
        if (mode == VisitMode.VALUE) {
            if (holder != null) {
                this.holder.accept(visitor.visitStats(), mode);
            } else {
                this.statsSet.accept(visitor.visitStats(), mode);
            }
            visitor.visitEnd();
        } else if (mode == VisitMode.DATA) {
            if (holder != null) {
                visitor.visitString(holder.getId());
            } else {
                statsSet.accept(new StatsAsMapVisitor(visitor.visitMap()), mode);
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
        return new MapAsStatsVisitor(this.statsSet);
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
