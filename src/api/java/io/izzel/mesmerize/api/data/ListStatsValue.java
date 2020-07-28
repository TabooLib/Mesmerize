package io.izzel.mesmerize.api.data;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import io.izzel.mesmerize.api.Stats;
import io.izzel.mesmerize.api.visitor.StatsHolder;
import io.izzel.mesmerize.api.visitor.StatsValue;
import io.izzel.mesmerize.api.visitor.StatsValueVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractStatsValue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ListStatsValue extends AbstractStatsValue<List<StatsValue<?>>> {

    private final List<Supplier<StatsValue<?>>> dataTypes;
    private final List<StatsValue<?>> values;
    private int readingIndex = -1;
    private StatsValue<?> currentValue;

    public ListStatsValue(List<Supplier<StatsValue<?>>> dataTypes) {
        this.dataTypes = ImmutableList.copyOf(dataTypes);
        this.values = new ArrayList<>(this.dataTypes.size());
    }

    @Override
    public void visitIndex(int index) {
        this.values.set(this.readingIndex, this.currentValue);
        this.readingIndex = index;
        this.currentValue = this.dataTypes.get(index).get();
    }

    @Override
    public void accept(StatsValueVisitor visitor) {
        for (int i = 0; i < this.values.size(); i++) {
            StatsValue<?> value = this.values.get(i);
            visitor.visitIndex(i);
            value.accept(visitor);
        }
        visitor.visitEnd();
    }

    @Override
    public void visitBoolean(boolean b) {
        Preconditions.checkNotNull(this.currentValue, "currentValue");
        this.currentValue.visitBoolean(b);
    }

    @Override
    public void visitInt(int i) {
        Preconditions.checkNotNull(this.currentValue, "currentValue");
        this.currentValue.visitInt(i);
    }

    @Override
    public void visitLong(long l) {
        Preconditions.checkNotNull(this.currentValue, "currentValue");
        this.currentValue.visitLong(l);
    }

    @Override
    public void visitFloat(float f) {
        Preconditions.checkNotNull(this.currentValue, "currentValue");
        this.currentValue.visitFloat(f);
    }

    @Override
    public void visitDouble(double d) {
        Preconditions.checkNotNull(this.currentValue, "currentValue");
        this.currentValue.visitDouble(d);
    }

    @Override
    public void visitString(String s) {
        Preconditions.checkNotNull(this.currentValue, "currentValue");
        this.currentValue.visitString(s);
    }

    @Override
    public void visitStatsHolder(StatsHolder holder) {
        Preconditions.checkNotNull(this.currentValue, "currentValue");
        this.currentValue.visitStatsHolder(holder);
    }

    @Override
    public List<StatsValue<?>> get() {
        return this.values;
    }

    @Override
    public void visitEnd() {
        if (this.currentValue != null) {
            this.values.set(this.readingIndex, this.currentValue);
        }
        this.readingIndex = -1;
        this.currentValue = null;
    }

    public static ListStatsValueBuilder builder() {
        return new ListStatsValueBuilder();
    }

    public static class ListStatsValueBuilder {

        private final List<Supplier<StatsValue<?>>> list = new ArrayList<>();

        public void add(Supplier<StatsValue<?>> supplier) {
            this.list.add(supplier);
        }

        public void add(Stats<?> stats) {
            this.list.add(stats::newValue);
        }

        public ListStatsValue build() {
            return new ListStatsValue(this.list);
        }

        public Supplier<ListStatsValue> buildSupplier() {
            return this::build;
        }
    }
}
