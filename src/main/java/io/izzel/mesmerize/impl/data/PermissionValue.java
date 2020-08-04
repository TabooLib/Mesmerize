package io.izzel.mesmerize.impl.data;

import io.izzel.mesmerize.api.data.MapValue;
import io.izzel.mesmerize.api.visitor.StatsValue;
import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.VisitMode;

import java.util.Map;
import java.util.function.Supplier;

public class PermissionValue extends MapValue {

    public PermissionValue(Map<String, Supplier<StatsValue<?>>> dataTypes) {
        super(dataTypes);
    }

    public PermissionValue(Map<String, Supplier<StatsValue<?>>> dataTypes, Map<String, StatsValue<?>> values) {
        super(dataTypes, values);
    }

    @Override
    public void accept(ValueVisitor visitor, VisitMode mode) {
        super.accept(visitor, mode);
    }
}
