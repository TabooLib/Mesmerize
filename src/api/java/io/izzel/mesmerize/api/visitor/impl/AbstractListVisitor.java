package io.izzel.mesmerize.api.visitor.impl;

import io.izzel.mesmerize.api.visitor.ListVisitor;
import io.izzel.mesmerize.api.visitor.ValueVisitor;

public class AbstractListVisitor implements ListVisitor {

    protected ListVisitor visitor;

    public AbstractListVisitor(ListVisitor visitor) {
        this.visitor = visitor;
    }

    @Override
    public void visitLength(int size) {
        if (this.visitor != null) {
            this.visitor.visitLength(size);
        }
    }

    @Override
    public ValueVisitor visit() {
        if (this.visitor != null) {
            return this.visitor.visit();
        }
        return null;
    }

    @Override
    public void visitEnd() {
        if (this.visitor != null) {
            this.visitor.visitEnd();
        }
    }
}
