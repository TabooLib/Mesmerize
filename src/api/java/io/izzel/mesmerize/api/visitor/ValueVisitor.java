package io.izzel.mesmerize.api.visitor;

import io.izzel.mesmerize.api.cause.CauseManager;
import io.izzel.mesmerize.api.cause.EventContext;

public interface ValueVisitor {

    void visitBoolean(boolean b);

    void visitInt(int i);

    void visitLong(long l);

    void visitFloat(float f);

    void visitDouble(double d);

    void visitString(String s);

    MapVisitor visitMap();

    ListVisitor visitList();

    StatsVisitor visitStats();

    ValueVisitor visitExternal();

    void visitEnd();

    default ExternalVisitor getExternalValue() {
        return null;
    }

    default EventContext context() {
        return CauseManager.instance().currentContext();
    }
}
