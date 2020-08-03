package io.izzel.mesmerize.api.data;

import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.VisitMode;
import io.izzel.mesmerize.api.visitor.impl.AbstractValue;

public class MarkerValue extends AbstractValue<Void> {

    @Override
    public Void get() {
        return null;
    }

    @Override
    public void accept(ValueVisitor visitor, VisitMode mode) {
        visitor.visitInt(1);
        visitor.visitEnd();
    }

    @Override
    public void visitBoolean(boolean b) {
    }

    @Override
    public void visitInt(int i) {
    }

    @Override
    public void visitLong(long l) {
    }

    @Override
    public void visitFloat(float f) {
    }

    @Override
    public void visitDouble(double d) {
    }

    @Override
    public void visitString(String s) {
    }
}
