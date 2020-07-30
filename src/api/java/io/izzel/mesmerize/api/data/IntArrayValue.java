package io.izzel.mesmerize.api.data;

import io.izzel.mesmerize.api.visitor.ListVisitor;
import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractListVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractValue;
import io.izzel.mesmerize.api.visitor.impl.AbstractValueVisitor;

public class IntArrayValue extends AbstractValue<int[]> {

    private int[] array;

    public IntArrayValue() {
    }

    public IntArrayValue(int[] array) {
        this.array = array;
    }

    @Override
    public int[] get() {
        return this.array;
    }

    @Override
    public void accept(ValueVisitor visitor) {
        ListVisitor list = visitor.visitList();
        list.visitLength(array.length);
        for (int i : array) {
            ValueVisitor valueVisitor = list.visit();
            valueVisitor.visitInt(i);
            valueVisitor.visitEnd();
        }
    }

    @Override
    public ListVisitor visitList() {
        return new Vis(null);
    }

    private class Vis extends AbstractListVisitor {

        private int index = 0;

        public Vis(ListVisitor visitor) {
            super(visitor);
        }

        @Override
        public void visitLength(int size) {
            if (array == null || array.length != size) {
                array = new int[size];
            }
        }

        @Override
        public ValueVisitor visit() {
            return new AbstractValueVisitor(null) {
                @Override
                public void visitInt(int i) {
                    array[index++] = i;
                }

                @Override
                public void visitLong(long l) {
                    array[index++] = (int) l;
                }

                @Override
                public void visitFloat(float f) {
                    array[index++] = (int) f;
                }

                @Override
                public void visitDouble(double d) {
                    array[index++] = (int) d;
                }
            };
        }
    }
}
