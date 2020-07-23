package io.izzel.mesmerize.api.data;

import io.izzel.mesmerize.api.visitor.StatsHolder;
import io.izzel.mesmerize.api.visitor.StatsValueVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractStatsValue;

public class StringStatsValue extends AbstractStatsValue<String> {

    @Override
    public String get() {
        return null;
    }

    @Override
    public void accept(StatsValueVisitor visitor) {

    }

    @Override
    public void visitString(String s) {

    }

    @Override
    public void visitEnd() {

    }
}
