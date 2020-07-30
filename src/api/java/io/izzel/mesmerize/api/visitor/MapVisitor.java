package io.izzel.mesmerize.api.visitor;

public interface MapVisitor {

    ValueVisitor visit(String key);

    void visitEnd();
}
