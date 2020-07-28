package io.izzel.mesmerize.api.visitor.impl;

import io.izzel.mesmerize.api.visitor.StatsHolder;
import io.izzel.mesmerize.api.visitor.StatsValue;
import io.izzel.mesmerize.api.visitor.StatsValueVisitor;

public abstract class AbstractStatsValue<T> implements StatsValue<T> {

    @Override
    public T get() {
        throw new NullPointerException();
    }

    @Override
    public void accept(StatsValueVisitor visitor) {
        throw new IllegalArgumentException("Not implemented");
    }

    @Override
    public void visitKey(String key) {
        throw new IllegalArgumentException("key");
    }

    @Override
    public void visitIndex(int index) {
        throw new IllegalArgumentException("index");
    }

    @Override
    public void visitBoolean(boolean b) {
        throw new IllegalArgumentException("boolean");
    }

    @Override
    public void visitInt(int i) {
        throw new IllegalArgumentException("int");
    }

    @Override
    public void visitLong(long l) {
        throw new IllegalArgumentException("long");
    }

    @Override
    public void visitFloat(float f) {
        throw new IllegalArgumentException("float");
    }

    @Override
    public void visitDouble(double d) {
        throw new IllegalArgumentException("double");
    }

    @Override
    public void visitString(String s) {
        throw new IllegalArgumentException("string");
    }

    @Override
    public void visitStatsHolder(StatsHolder holder) {
        throw new IllegalArgumentException("StatsHolder");
    }

    @Override
    public void visitEnd() {
    }
}
