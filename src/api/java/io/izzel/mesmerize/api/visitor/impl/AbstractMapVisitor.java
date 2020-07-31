package io.izzel.mesmerize.api.visitor.impl;

import io.izzel.mesmerize.api.visitor.MapVisitor;
import io.izzel.mesmerize.api.visitor.ValueVisitor;

public class AbstractMapVisitor implements MapVisitor {

    public static final AbstractMapVisitor EMPTY = new AbstractMapVisitor(null) {
        @Override
        public ValueVisitor visit(String key) {
            return AbstractValueVisitor.EMPTY;
        }
    };

    protected MapVisitor visitor;

    public AbstractMapVisitor(MapVisitor visitor) {
        this.visitor = visitor;
    }

    @Override
    public ValueVisitor visit(String key) {
        if (this.visitor != null) {
            return this.visitor.visit(key);
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
