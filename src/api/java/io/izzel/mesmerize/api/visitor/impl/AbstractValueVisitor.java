package io.izzel.mesmerize.api.visitor.impl;

import io.izzel.mesmerize.api.visitor.ListVisitor;
import io.izzel.mesmerize.api.visitor.MapVisitor;
import io.izzel.mesmerize.api.visitor.StatsVisitor;
import io.izzel.mesmerize.api.visitor.ValueVisitor;

public class AbstractValueVisitor implements ValueVisitor {

    protected ValueVisitor visitor;

    public AbstractValueVisitor(ValueVisitor visitor) {
        this.visitor = visitor;
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
    public MapVisitor visitMap() {
        if (visitor != null) {
            return visitor.visitMap();
        }
        return null;
    }

    @Override
    public ListVisitor visitList() {
        if (visitor != null) {
            return visitor.visitList();
        }
        return null;
    }

    @Override
    public StatsVisitor visitStats() {
        if (visitor != null) {
            return visitor.visitStats();
        }
        return null;
    }

    @Override
    public void visitEnd() {
        if (visitor != null) {
            visitor.visitEnd();
        }
    }
}
