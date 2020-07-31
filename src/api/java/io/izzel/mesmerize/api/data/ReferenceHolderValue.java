package io.izzel.mesmerize.api.data;

import com.google.common.base.Preconditions;
import io.izzel.mesmerize.api.cause.ContextKeys;
import io.izzel.mesmerize.api.visitor.StatsHolder;
import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractValue;
import io.izzel.mesmerize.api.visitor.util.LazyStatsHolder;

import java.util.Objects;

public class ReferenceHolderValue extends AbstractValue<StatsHolder> {

    private LazyStatsHolder holder;

    @Override
    public void accept(ValueVisitor visitor) {
        if (visitor.context().containsKey(ContextKeys.SOURCE)) {
            this.holder.accept(visitor.visitStats());
        } else {
            visitor.visitString(holder.getId());
            visitor.visitEnd();
        }
    }

    @Override
    public void visitString(String s) {
        Preconditions.checkArgument(holder == null, "initialized");
        this.holder = new LazyStatsHolder(s);
    }

    @Override
    public StatsHolder get() {
        return Objects.requireNonNull(holder);
    }

    @Override
    public void visitEnd() {
        Preconditions.checkNotNull(holder, "holder");
    }
}
