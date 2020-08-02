package io.izzel.mesmerize.api.visitor;

import java.util.Optional;

public interface StatsValue<T> extends ValueVisitor {

    T get();

    Optional<ValueVisitor> getMutableValue();

    void accept(ValueVisitor visitor, VisitMode mode);
}
