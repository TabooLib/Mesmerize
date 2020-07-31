package io.izzel.mesmerize.api.visitor;

public interface ListVisitor {

    void visitLength(int size);

    ValueVisitor visit(int index);

    void visitEnd();
}
