package io.izzel.mesmerize.api.data;

import com.google.common.base.Preconditions;
import io.izzel.mesmerize.api.visitor.StatsValueVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractStatsValue;

public class RangeNumberStatsValue extends AbstractStatsValue<Number> {

    private NumberStatsValue lower, higher;

    public NumberStatsValue getLower() {
        return lower;
    }

    public NumberStatsValue getHigher() {
        return higher;
    }

    @Override
    public void accept(StatsValueVisitor visitor) {
        visitor.visitIndex(0);
        lower.dumper.accept(visitor);
        if (higher != null) {
            visitor.visitIndex(1);
            higher.dumper.accept(visitor);
        }
    }

    private NumberStatsValue getCurrent() {
        if (lower == null) {
            return lower = new NumberStatsValue();
        } else if (higher == null) {
            return higher = new NumberStatsValue();
        } else {
            throw new IllegalArgumentException("close");
        }
    }

    @Override
    public void visitIndex(int index) {
    }

    @Override
    public void visitInt(int i) {
        getCurrent().visitInt(i);
    }

    @Override
    public void visitLong(long l) {
        getCurrent().visitLong(l);
    }

    @Override
    public void visitFloat(float f) {
        getCurrent().visitFloat(f);
    }

    @Override
    public void visitDouble(double d) {
        getCurrent().visitDouble(d);
    }

    @Override
    public void visitString(String s) {
        getCurrent().visitString(s);
    }

    @Override
    public void visitEnd() {
        Preconditions.checkNotNull(lower, "lower");
        if (higher != null) {
            Preconditions.checkArgument(lower.isRelative() == higher.isRelative(), "relative");
        }
    }
}
