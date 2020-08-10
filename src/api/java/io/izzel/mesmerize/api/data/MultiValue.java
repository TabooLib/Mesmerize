package io.izzel.mesmerize.api.data;

import com.google.common.collect.Lists;
import io.izzel.mesmerize.api.visitor.ListVisitor;
import io.izzel.mesmerize.api.visitor.MapVisitor;
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
import java.util.function.Supplier;

public class MultiValue<E, V extends StatsValue<E>> extends AbstractValue<List<V>> {

    private final boolean allowSingleNonListValue;
    protected final Supplier<V> supplier;
    protected ArrayList<V> values;

    public MultiValue(boolean allowSingleNonListValue, Supplier<V> supplier) {
        this.allowSingleNonListValue = allowSingleNonListValue;
        this.supplier = supplier;
        if (allowSingleNonListValue) {
            this.values = new ArrayList<>(1);
        }
    }

    public MultiValue(boolean allowSingleNonListValue, Supplier<V> supplier, List<V> values) {
        this.allowSingleNonListValue = allowSingleNonListValue;
        this.supplier = supplier;
        this.values = values instanceof ArrayList ? ((ArrayList<V>) values) : new ArrayList<>(values);
    }

    @Override
    public void accept(ValueVisitor visitor, VisitMode mode) {
        if (this.allowSingleNonListValue && this.values.size() == 1) {
            this.values.get(0).accept(visitor, mode);
        } else {
            ListVisitor listVisitor = visitor.visitList();
            listVisitor.visitLength(values.size());
            for (int i = 0; i < values.size(); i++) {
                V value = values.get(i);
                ValueVisitor valueVisitor = listVisitor.visit(i);
                value.accept(valueVisitor, mode);
            }
            listVisitor.visitEnd();
            visitor.visitEnd();
        }
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

    private V singleValueVisitor(String type) {
        if (this.allowSingleNonListValue) {
            if (this.values.size() == 0) {
                V v = this.supplier.get();
                this.values.add(v);
                return v;
            } else if (this.values.size() == 1) {
                return this.values.get(0);
            }
        }
        throw new UnsupportedOperationException(type);
    }

    @Override
    public void visitBoolean(boolean b) {
        V visitor = singleValueVisitor("boolean");
        visitor.visitBoolean(b);
        visitor.visitEnd();
    }

    @Override
    public void visitInt(int i) {
        V visitor = singleValueVisitor("int");
        visitor.visitInt(i);
        visitor.visitEnd();
    }

    @Override
    public void visitLong(long l) {
        V visitor = singleValueVisitor("long");
        visitor.visitLong(l);
        visitor.visitEnd();
    }

    @Override
    public void visitFloat(float f) {
        V visitor = singleValueVisitor("float");
        visitor.visitFloat(f);
        visitor.visitEnd();
    }

    @Override
    public void visitDouble(double d) {
        V visitor = singleValueVisitor("double");
        visitor.visitDouble(d);
        visitor.visitEnd();
    }

    @Override
    public void visitString(String s) {
        V visitor = singleValueVisitor("string");
        visitor.visitString(s);
        visitor.visitEnd();
    }

    @Override
    public MapVisitor visitMap() {
        V visitor = singleValueVisitor("map");
        return visitor.visitMap();
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
        public void visitLength(int size) {
            values = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                values.add(null);
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

    public static <E, V extends StatsValue<E>> BiFunction<MultiValue<E, V>, MultiValue<E, V>, MultiValue<E, V>> concatMerger() {
        return (a, b) -> {
            ArrayList<V> arrayList = new ArrayList<>(a.values);
            arrayList.addAll(b.values);
            return new MultiValue<>(arrayList.size() == 1, a.supplier, arrayList);
        };
    }

    public static <E, V extends StatsValue<E>> BiFunction<MultiValue<E, V>, MultiValue<E, V>, MultiValue<E, V>> singletonMerger(BiFunction<V, V, V> valueMerger) {
        return (a, b) -> new MultiValue<>(true, a.supplier, Lists.newArrayList(valueMerger.apply(a.get(0), b.get(0))));
    }

    public static MultiValueBuilder<?, ?> builder() {
        return new MultiValueBuilder<>();
    }

    public static class MultiValueBuilder<E, V extends StatsValue<E>> {

        private Supplier<V> supplier;
        private boolean allowSingleNonListValue = false;

        @SuppressWarnings("unchecked")
        @Contract("_ -> this")
        public <N_E, N_V extends StatsValue<N_E>> MultiValueBuilder<N_E, N_V> supplying(Supplier<N_V> supplier) {
            this.supplier = (Supplier<V>) supplier;
            return (MultiValueBuilder<N_E, N_V>) this;
        }

        public MultiValueBuilder<E, V> allowSingleNonListValue() {
            this.allowSingleNonListValue = true;
            return this;
        }

        public MultiValue<E, V> build() {
            return new MultiValue<>(this.allowSingleNonListValue, this.supplier);
        }

        public Supplier<MultiValue<E, V>> buildSupplier() {
            return this::build;
        }
    }
}
