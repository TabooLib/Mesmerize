package io.izzel.mesmerize.api.visitor;

public interface StatsValue<T> extends ValueVisitor {

    T get();

    void accept(ValueVisitor visitor, VisitMode mode);
}
