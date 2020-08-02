package io.izzel.mesmerize.api.data;

import com.google.common.base.Preconditions;
import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.VisitMode;
import io.izzel.mesmerize.api.visitor.impl.AbstractValue;

import java.util.function.BiFunction;

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
    public void accept(ValueVisitor visitor, VisitMode mode) {
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

    public static BiFunction<StringValue, StringValue, StringValue> joiningMerger(String separator) {
        return (a, b) -> new StringValue(a.s + separator + b.s);
    }
}
