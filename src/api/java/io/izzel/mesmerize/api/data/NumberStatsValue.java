package io.izzel.mesmerize.api.data;

import com.google.common.base.Preconditions;
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

    @Override
    public void visitEnd() {
        Preconditions.checkNotNull(number, "empty");
    }
}
