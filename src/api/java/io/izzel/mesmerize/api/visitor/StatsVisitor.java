package io.izzel.mesmerize.api.visitor;

import io.izzel.mesmerize.api.Stats;
import org.jetbrains.annotations.NotNull;

public interface StatsVisitor {

    InfoVisitor visitInfo();

    <T> StatsValueVisitor visitStats(@NotNull Stats<T> stats);

    void visitEnd();
}
