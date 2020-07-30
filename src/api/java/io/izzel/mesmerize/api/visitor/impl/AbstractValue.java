package io.izzel.mesmerize.api.visitor.impl;

import io.izzel.mesmerize.api.visitor.ListVisitor;
import io.izzel.mesmerize.api.visitor.MapVisitor;
import io.izzel.mesmerize.api.visitor.StatsValue;
import io.izzel.mesmerize.api.visitor.StatsVisitor;
import io.izzel.mesmerize.api.visitor.ValueVisitor;

public abstract class AbstractValue<T> implements StatsValue<T> {

    @Override
    public T get() {
        throw new UnsupportedOperationException("get");
    }

    @Override
    public void accept(ValueVisitor visitor) {
        throw new UnsupportedOperationException("accept");
    }

    @Override
    public void visitBoolean(boolean b) {
        throw new UnsupportedOperationException("boolean");
    }

    @Override
    public void visitInt(int i) {
        throw new UnsupportedOperationException("int");
    }

    @Override
    public void visitLong(long l) {
        throw new UnsupportedOperationException("long");
    }

    @Override
    public void visitFloat(float f) {
        throw new UnsupportedOperationException("float");
    }

    @Override
    public void visitDouble(double d) {
        throw new UnsupportedOperationException("double");
    }

    @Override
    public void visitString(String s) {
        throw new UnsupportedOperationException("string");
    }

    @Override
    public MapVisitor visitMap() {
        throw new UnsupportedOperationException("map");
    }

    @Override
    public ListVisitor visitList() {
        throw new UnsupportedOperationException("list");
    }

    @Override
    public StatsVisitor visitStats() {
        throw new UnsupportedOperationException("stats");
    }

    @Override
    public void visitEnd() {
    }
}
