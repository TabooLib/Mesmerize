package io.izzel.mesmerize.api.visitor;

import org.jetbrains.annotations.NotNull;

public interface InfoVisitor {

    <T> void visitInfo(InfoKey<T> key, @NotNull T value);

    void visitEnd();
}
