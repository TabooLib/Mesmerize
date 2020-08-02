package io.izzel.mesmerize.api.data;

import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.VisitMode;
import io.izzel.mesmerize.api.visitor.impl.AbstractValue;

import java.util.function.BiFunction;

public class BooleanValue extends AbstractValue<Boolean> {

    private boolean b;

    public BooleanValue() {
    }

    public BooleanValue(boolean b) {
        this.b = b;
    }

    public boolean getValue() {
        return b;
    }

    @Override
    public void accept(ValueVisitor visitor, VisitMode mode) {
        visitor.visitBoolean(b);
        visitor.visitEnd();
    }

    @Override
    public Boolean get() {
        return b;
    }

    @Override
    public void visitBoolean(boolean b) {
        this.b = b;
    }

    @Override
    public void visitInt(int i) {
        this.b = i != 0;
    }

    public static BiFunction<BooleanValue, BooleanValue, BooleanValue> andMerger() {
        return (a, b) -> new BooleanValue(a.b && b.b);
    }

    public static BiFunction<BooleanValue, BooleanValue, BooleanValue> orMerger() {
        return (a, b) -> new BooleanValue(a.b || b.b);
    }
}
