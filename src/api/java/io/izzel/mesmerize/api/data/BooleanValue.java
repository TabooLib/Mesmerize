package io.izzel.mesmerize.api.data;

import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractValue;

public class BooleanValue extends AbstractValue<Boolean> {

    private boolean b;

    public boolean getValue() {
        return b;
    }

    @Override
    public void accept(ValueVisitor visitor) {
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
}
