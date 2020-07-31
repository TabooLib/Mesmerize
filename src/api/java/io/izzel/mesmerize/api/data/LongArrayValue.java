package io.izzel.mesmerize.api.data;

import io.izzel.mesmerize.api.visitor.ListVisitor;
import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractListVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractValue;
import io.izzel.mesmerize.api.visitor.impl.AbstractValueVisitor;

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
