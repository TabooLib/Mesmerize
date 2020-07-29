package io.izzel.mesmerize.api.data;

import com.google.common.base.Preconditions;
import io.izzel.mesmerize.api.visitor.StatsHolder;
import io.izzel.mesmerize.api.visitor.StatsValueVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractStatsValue;
import io.izzel.mesmerize.api.visitor.util.LazyStatsHolder;

import java.util.Objects;

public class ReferenceHolderValue extends AbstractStatsValue<StatsHolder> {

    private LazyStatsHolder holder;

    @Override
    public void accept(StatsValueVisitor visitor) {
        Preconditions.checkNotNull(holder, "holder");
        visitor.visitString(this.holder.getId());
        visitor.visitEnd();
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
