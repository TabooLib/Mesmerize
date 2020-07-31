package io.izzel.mesmerize.api.data;

import com.google.common.base.Preconditions;
import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractValue;

public class StringValue extends AbstractValue<String> {

    private String s;

    public StringValue() {
    }

    public StringValue(String s) {
        this.s = s;
    }

    @Override
    public String get() {
        return s;
    }

    @Override
    public void accept(ValueVisitor visitor) {
        visitor.visitString(s);
        visitor.visitEnd();
    }

    @Override
    public void visitString(String s) {
        Preconditions.checkArgument(this.s == null, "close");
        this.s = s;
    }

    @Override
    public void visitEnd() {
        Preconditions.checkArgument(this.s != null, "empty");
    }
}
