package io.izzel.mesmerize.impl.util;

import io.izzel.mesmerize.api.visitor.StatsHolder;
import io.izzel.mesmerize.api.visitor.ValueVisitor;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PersistentValueWriter implements ValueVisitor {

    protected final PersistentDataContainer container;

    protected String lastKey = null;
    protected int lastIndex = -1;

    public PersistentValueWriter(PersistentDataContainer container) {
        this.container = container;
    }

    public PersistentDataContainer get() {
        return this.container;
    }

    protected NamespacedKey currentKey() {
        if (lastKey != null) {
            if (lastIndex != -1) throw new IllegalStateException("close");
            else return Util.fromString(lastKey);
        }
        throw new IllegalStateException("empty");
    }

    @Override
    public void visitKey(String key) {
        this.lastKey = key;
    }

    @Override
    public void visitIndex(int index) {
        this.lastIndex = index;
    }

    @Override
    public void visitBoolean(boolean b) {
        this.container.set(currentKey(), PersistentDataType.BYTE, (byte) (b ? 1 : 0));
    }

    @Override
    public void visitInt(int i) {
        this.container.set(currentKey(), PersistentDataType.INTEGER, i);
    }

    @Override
    public void visitLong(long l) {
        this.container.set(currentKey(), PersistentDataType.LONG, l);
    }

    @Override
    public void visitFloat(float f) {
        this.container.set(currentKey(), PersistentDataType.FLOAT, f);
    }

    @Override
    public void visitDouble(double d) {
        this.container.set(currentKey(), PersistentDataType.DOUBLE, d);
    }

    @Override
    public void visitString(String s) {
        this.container.set(currentKey(), PersistentDataType.STRING, s);
    }

    @Override
    public ValueVisitor visitStatsValue() {
        return new PersistentValueWriter(this.container.getAdapterContext().newPersistentDataContainer()) {
            @Override
            public void visitEnd() {
                super.visitEnd();
                PersistentValueWriter.this.container.set(currentKey(), PersistentDataType.TAG_CONTAINER, this.container);
            }
        };
    }

    @Override
    public Tristate visitStatsHolder(StatsHolder holder) {
        return Tristate.UNDEFINED;
    }

    @Override
    public void visitEnd() {
    }
}
