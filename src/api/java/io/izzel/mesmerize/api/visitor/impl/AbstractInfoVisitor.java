package io.izzel.mesmerize.api.visitor.impl;

import io.izzel.mesmerize.api.visitor.InfoKey;
import io.izzel.mesmerize.api.visitor.InfoVisitor;
import org.jetbrains.annotations.NotNull;

public class AbstractInfoVisitor implements InfoVisitor {

    public static final AbstractInfoVisitor EMPTY = new AbstractInfoVisitor(null);

    protected InfoVisitor visitor;

    public AbstractInfoVisitor(InfoVisitor visitor) {
        this.visitor = visitor;
    }

    @Override
    public <T> void visitInfo(InfoKey<T> key, @NotNull T value) {
        if (visitor != null) {
            visitor.visitInfo(key, value);
        }
    }

    @Override
    public void visitEnd() {
        if (visitor != null) {
            visitor.visitEnd();
        }
    }
}
