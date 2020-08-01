package io.izzel.mesmerize.api.data;

import io.izzel.mesmerize.api.visitor.ListVisitor;
import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractListVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractValue;
import io.izzel.mesmerize.api.visitor.impl.AbstractValueVisitor;

import java.util.function.BiFunction;

public class LongArrayValue extends AbstractValue<long[]> {

    private long[] array;

    public LongArrayValue() {
    }

    public LongArrayValue(long[] array) {
        this.array = array;
    }

    @Override
    public long[] get() {
        return this.array;
    }

    @Override
    public void accept(ValueVisitor visitor) {
        ListVisitor list = visitor.visitList();
        list.visitLength(array.length);
        for (int i = 0; i < array.length; i++) {
            long l = array[i];
            ValueVisitor valueVisitor = list.visit(i);
            valueVisitor.visitLong(l);
            valueVisitor.visitEnd();
        }
        list.visitEnd();
        visitor.visitEnd();
    }

    @Override
    public ListVisitor visitList() {
        return new Vis(null);
    }

    public static BiFunction<LongArrayValue, LongArrayValue, LongArrayValue> concatMerger() {
        return (a, b) -> {
            long[] longs = new long[a.array.length + b.array.length];
            System.arraycopy(a.array, 0, longs, 0, a.array.length);
            System.arraycopy(b.array, 0, longs, a.array.length, b.array.length);
            return new LongArrayValue(longs);
        };
    }

    private class Vis extends AbstractListVisitor {

        public Vis(ListVisitor visitor) {
            super(visitor);
        }

        @Override
        public void visitLength(int size) {
            if (array == null || array.length != size) {
                array = new long[size];
            }
        }

        @Override
        public ValueVisitor visit(int index) {
            return new AbstractValueVisitor(null) {
                @Override
                public void visitInt(int i) {
                    array[index] = i;
                }

                @Override
                public void visitLong(long l) {
                    array[index] = l;
                }

                @Override
                public void visitFloat(float f) {
                    array[index] = (long) f;
                }

                @Override
                public void visitDouble(double d) {
                    array[index] = (long) d;
                }
            };
        }
    }
}
