package io.izzel.mesmerize.api.data;

import com.google.common.base.Preconditions;
import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractValue;

import java.util.function.Consumer;

public class NumberValue extends AbstractValue<Number> {

    private Number number;
    Consumer<ValueVisitor> dumper;
    private boolean relative = false;

    @Override
    public Number get() {
        return number;
    }

    public boolean isRelative() {
        return relative;
    }

    @Override
    public void accept(ValueVisitor visitor) {
        dumper.accept(visitor);
        visitor.visitEnd();
    }

    @Override
    public void visitInt(int i) {
        Preconditions.checkArgument(number == null, "close");
        number = i;
        dumper = visitor -> visitor.visitInt(i);
    }

    @Override
    public void visitLong(long l) {
        Preconditions.checkArgument(number == null, "close");
        number = l;
        dumper = visitor -> visitor.visitLong(l);
    }

    @Override
    public void visitFloat(float f) {
        Preconditions.checkArgument(number == null, "close");
        number = f;
        dumper = visitor -> visitor.visitFloat(f);
    }

    @Override
    public void visitDouble(double d) {
        Preconditions.checkArgument(number == null, "close");
        number = d;
        dumper = visitor -> visitor.visitDouble(d);
    }

    private Number parseNumber(String s) {
        try {
            return Long.parseLong(s);
        } catch (Throwable t) {
            return Double.parseDouble(s);
        }
    }

    @Override
    public void visitString(String s) {
        Preconditions.checkArgument(number == null, "close");
        if (s.endsWith("%")) {
            this.number = parseNumber(s.substring(0, s.length() - 1));
            this.relative = true;
        } else {
            this.number = parseNumber(s);
        }
        this.dumper = visitor -> visitor.visitString(s);
    }

    @Override
    public void visitEnd() {
        Preconditions.checkNotNull(number, "empty");
    }
}
