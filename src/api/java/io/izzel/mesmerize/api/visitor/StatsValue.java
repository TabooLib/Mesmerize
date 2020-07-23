package io.izzel.mesmerize.api.visitor;

public interface StatsValue<T> extends StatsValueVisitor {

    T get();

    void accept(StatsValueVisitor visitor);
}
