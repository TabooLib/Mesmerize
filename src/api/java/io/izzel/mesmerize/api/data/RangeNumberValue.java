package io.izzel.mesmerize.api.data;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

public class RangeNumberValue extends ListValue {

    public RangeNumberValue() {
        super(ImmutableList.of(NumberValue::new, NumberValue::new));
    }

    public NumberValue getLower() {
        return super.get(0);
    }

    public NumberValue getHigher() {
        return super.get().size() > 1 ? super.get(1) : super.get(0);
    }

    @Override
    public void visitEnd() {
        Preconditions.checkArgument(super.get().size() > 0 && super.get().size() <= 2);
    }
}
