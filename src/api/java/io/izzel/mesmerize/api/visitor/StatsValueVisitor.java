package io.izzel.mesmerize.api.visitor;

public interface StatsValueVisitor {

    void visitKey(String key);

    void visitBoolean(boolean b);

    void visitInt(int i);

    void visitLong(long l);

    void visitFloat(float f);

    void visitDouble(double d);

    void visitString(String s);

    void visitStatsHolder(StatsHolder holder);

    void visitEnd();
}
