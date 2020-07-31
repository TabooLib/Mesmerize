package io.izzel.mesmerize.api.data;

import com.google.common.collect.ImmutableList;
import io.izzel.mesmerize.api.Stats;
import io.izzel.mesmerize.api.visitor.ListVisitor;
import io.izzel.mesmerize.api.visitor.StatsValue;
import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractListVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractValue;
import io.izzel.mesmerize.api.visitor.impl.AbstractValueVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ListValue extends AbstractValue<List<StatsValue<?>>> {

    private final List<Supplier<StatsValue<?>>> dataTypes;
    private final List<StatsValue<?>> values;

    public ListValue(List<Supplier<StatsValue<?>>> dataTypes) {
        this.dataTypes = ImmutableList.copyOf(dataTypes);
        this.values = new ArrayList<>(this.dataTypes.size());
    }

    @Override
    public void accept(ValueVisitor visitor) {
        ListVisitor listVisitor = visitor.visitList();
        listVisitor.visitLength(values.size());
        for (int i = 0; i < values.size(); i++) {
            StatsValue<?> value = values.get(i);
            value.accept(listVisitor.visit(i));
        }
        listVisitor.visitEnd();
    }

    @Override
    public List<StatsValue<?>> get() {
        return this.values;
    }

    @SuppressWarnings("unchecked")
    public <T extends StatsValue<?>> T get(int i) {
        return (T) this.values.get(i);
    }

    @Override
    public ListVisitor visitList() {
        return new Vis(null);
    }

    private class Vis extends AbstractListVisitor {

        public Vis(ListVisitor visitor) {
            super(visitor);
        }

        @Override
        public ValueVisitor visit(int index) {
            StatsValue<?> value = dataTypes.get(index).get();
            return new AbstractValueVisitor(value) {
                @Override
                public void visitEnd() {
                    super.visitEnd();
                    values.set(index, value);
                }
            };
        }
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

        public ListValue build() {
            return new ListValue(this.list);
        }

        public Supplier<ListValue> buildSupplier() {
            return this::build;
        }
    }
}
