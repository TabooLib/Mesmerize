package io.izzel.mesmerize.api.data;

import io.izzel.mesmerize.api.visitor.StatsValueVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractStatsValue;

import java.util.function.Consumer;

public class NumberStatsValue extends AbstractStatsValue<Number> {

    private Number number;
    private Consumer<StatsValueVisitor> dumper;

    @Override
    public Number get() {
        return number;
    }

    @Override
    public void accept(StatsValueVisitor visitor) {
        dumper.accept(visitor);
        visitor.visitEnd();
    }

    @Override
    public void visitInt(int i) {
        if (number != null) throw new IllegalArgumentException("close");
        number = i;
        dumper = visitor -> visitor.visitInt(i);
    }

    @Override
    public void visitLong(long l) {
        if (number != null) throw new IllegalArgumentException("close");
        number = l;
        dumper = visitor -> visitor.visitLong(l);
    }

    @Override
    public void visitFloat(float f) {
        if (number != null) throw new IllegalArgumentException("close");
        number = f;
        dumper = visitor -> visitor.visitFloat(f);
    }

    @Override
    public void visitDouble(double d) {
        if (number != null) throw new IllegalArgumentException("close");
        number = d;
        dumper = visitor -> visitor.visitDouble(d);
    }

    @Override
    public void visitEnd() {
        if (number == null) throw new IllegalArgumentException("empty");
    }
}
