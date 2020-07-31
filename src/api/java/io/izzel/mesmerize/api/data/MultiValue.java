package io.izzel.mesmerize.api.data;

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

public class MultiValue<E, V extends StatsValue<E>> extends AbstractValue<List<V>> {

    private final Supplier<V> supplier;
    private ArrayList<V> values;

    public MultiValue(Supplier<V> supplier) {
        this.supplier = supplier;
    }

    public MultiValue(Supplier<V> supplier, List<V> values) {
        this.supplier = supplier;
        this.values = new ArrayList<>(values);
    }

    @Override
    public void accept(ValueVisitor visitor) {
        ListVisitor listVisitor = visitor.visitList();
        listVisitor.visitLength(values.size());
        for (int i = 0; i < values.size(); i++) {
            V value = values.get(i);
            ValueVisitor valueVisitor = listVisitor.visit(i);
            value.accept(valueVisitor);
        }
        listVisitor.visitEnd();
        visitor.visitEnd();
    }

    @Override
    public List<V> get() {
        return this.values;
    }

    public V get(int index) {
        return this.values.get(index);
    }

    public E getValue(int index) {
        return this.values.get(index).get();
    }

    @Override
    public ListVisitor visitList() {
        return new Vis(null);
    }

    public static <E> Supplier<MultiValue<E, StatsValue<E>>> ofSupplier(Stats<E> stats) {
        return ofSupplier(stats::newValue);
    }

    public static <E, V extends StatsValue<E>> Supplier<MultiValue<E, V>> ofSupplier(Supplier<V> supplier) {
        return () -> new MultiValue<>(supplier);
    }

    private class Vis extends AbstractListVisitor {

        public Vis(ListVisitor visitor) {
            super(visitor);
        }

        @Override
        public void visitLength(int size) {
            if (values == null || values.size() < size) {
                ArrayList<V> list = new ArrayList<>(size);
                if (values != null) {
                    list.addAll(values);
                }
                values = list;
            }
        }

        @Override
        public ValueVisitor visit(int index) {
            V value = supplier.get();
            return new AbstractValueVisitor(value) {
                @Override
                public void visitEnd() {
                    super.visitEnd();
                    values.set(index, value);
                }
            };
        }
    }
}
