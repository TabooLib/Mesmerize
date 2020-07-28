package io.izzel.mesmerize.api.visitor.impl;

import io.izzel.mesmerize.api.visitor.StatsHolder;
import io.izzel.mesmerize.api.visitor.StatsValueVisitor;

public class AbstractStatsValueVisitor implements StatsValueVisitor {

    protected StatsValueVisitor visitor;

    public AbstractStatsValueVisitor(StatsValueVisitor visitor) {
        this.visitor = visitor;
    }

    @Override
    public void visitKey(String key) {
        if (visitor != null) {
            visitor.visitKey(key);
        }
    }

    @Override
    public void visitIndex(int index) {
        if (visitor != null) {
            visitor.visitIndex(index);
        }
    }

    @Override
    public void visitBoolean(boolean b) {
        if (visitor != null) {
            visitor.visitBoolean(b);
        }
    }

    @Override
    public void visitInt(int i) {
        if (visitor != null) {
            visitor.visitInt(i);
        }
    }

    @Override
    public void visitLong(long l) {
        if (visitor != null) {
            visitor.visitLong(l);
        }
    }

    @Override
    public void visitFloat(float f) {
        if (visitor != null) {
            visitor.visitFloat(f);
        }
    }

    @Override
    public void visitDouble(double d) {
        if (visitor != null) {
            visitor.visitDouble(d);
        }
    }

    @Override
    public void visitString(String s) {
        if (visitor != null) {
            visitor.visitString(s);
        }
    }

    @Override
    public void visitStatsHolder(StatsHolder holder) {
        if (visitor != null) {
            visitor.visitStatsHolder(holder);
        }
    }

    @Override
    public void visitEnd() {
        if (visitor != null) {
            visitor.visitEnd();
        }
    }
}
