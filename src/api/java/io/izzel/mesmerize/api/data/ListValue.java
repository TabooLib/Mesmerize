package io.izzel.mesmerize.api.data;

import com.google.common.collect.ImmutableList;
import io.izzel.mesmerize.api.Stats;
import io.izzel.mesmerize.api.visitor.ListVisitor;
import io.izzel.mesmerize.api.visitor.StatsValue;
import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.VisitMode;
import io.izzel.mesmerize.api.visitor.impl.AbstractListVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractValue;
import io.izzel.mesmerize.api.visitor.impl.AbstractValueVisitor;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class ListValue extends AbstractValue<List<StatsValue<?>>> {

    protected final List<Supplier<StatsValue<?>>> dataTypes;
    protected final List<StatsValue<?>> values;

    public ListValue(List<Supplier<StatsValue<?>>> dataTypes) {
        this.dataTypes = dataTypes instanceof ImmutableList ? dataTypes : ImmutableList.copyOf(dataTypes);
        this.values = new ArrayList<>(this.dataTypes.size());
        for (int i = 0; i < this.dataTypes.size(); i++) {
            this.values.add(null);
        }
    }

    public ListValue(List<Supplier<StatsValue<?>>> dataTypes, List<StatsValue<?>> values) {
        this.dataTypes = dataTypes instanceof ImmutableList ? dataTypes : ImmutableList.copyOf(dataTypes);
        this.values = values;
    }

    @Override
    public void accept(ValueVisitor visitor, VisitMode mode) {
        ListVisitor listVisitor = visitor.visitList();
        listVisitor.visitLength(values.size());
        for (int i = 0; i < values.size(); i++) {
            StatsValue<?> value = values.get(i);
            value.accept(listVisitor.visit(i), mode);
        }
        listVisitor.visitEnd();
        visitor.visitEnd();
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

        @Contract("_ -> this")
        public ListStatsValueBuilder add(Supplier<StatsValue<?>> supplier) {
            this.list.add(supplier);
            return this;
        }

        @Contract("_ -> this")
        public ListStatsValueBuilder add(Stats<?> stats) {
            this.list.add(stats::newValue);
            return this;
        }

        @Contract("-> new")
        public ListValue build() {
            return new ListValue(this.list);
        }

        @Contract("-> new")
        public Supplier<ListValue> buildSupplier() {
            return this::build;
        }

        @Contract("_ -> new")
        public <V extends ListValue> Supplier<V> buildSupplier(Function<List<Supplier<StatsValue<?>>>, V> constructor) {
            return () -> constructor.apply(list);
        }
    }

    public static BiFunction<ListValue, ListValue, ListValue> concatMerger() {
        return (a, b) -> new ListValue(
            ImmutableList.<Supplier<StatsValue<?>>>builder().addAll(a.dataTypes).addAll(b.dataTypes).build(),
            ImmutableList.<StatsValue<?>>builder().addAll(a.values).addAll(b.values).build()
        );
    }

    public static ListDeepMergerBuilder deepMerger() {
        return new ListDeepMergerBuilder();
    }

    public static class ListDeepMergerBuilder {

        private final List<BiFunction<StatsValue<?>, StatsValue<?>, StatsValue<?>>> mergers = new ArrayList<>();

        @SuppressWarnings("unchecked")
        public <V extends StatsValue<?>> ListDeepMergerBuilder add(BiFunction<V, V, V> merger) {
            this.mergers.add((BiFunction<StatsValue<?>, StatsValue<?>, StatsValue<?>>) merger);
            return this;
        }

        public BiFunction<ListValue, ListValue, ListValue> build() {
            return build(ListValue::new);
        }

        public <V extends ListValue> BiFunction<V, V, V> build(BiFunction<List<Supplier<StatsValue<?>>>, List<StatsValue<?>>, V> constructor) {
            return (a, b) -> {
                int size = a.dataTypes.size();
                List<StatsValue<?>> list = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    StatsValue<?> left = a.values.get(i);
                    StatsValue<?> right = b.values.get(i);
                    if (left == null) list.set(i, right);
                    else if (right == null) list.set(i, left);
                    else list.set(i, mergers.get(i).apply(left, right));
                }
                return constructor.apply(a.dataTypes, list);
            };
        }
    }
}
