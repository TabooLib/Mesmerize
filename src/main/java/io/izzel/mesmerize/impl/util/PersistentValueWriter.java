package io.izzel.mesmerize.impl.util;

import io.izzel.mesmerize.api.visitor.ListVisitor;
import io.izzel.mesmerize.api.visitor.MapVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractValueVisitor;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PersistentValueWriter extends AbstractValueVisitor {

    private final PersistentDataContainer container;
    private final NamespacedKey key;

    public PersistentValueWriter(PersistentDataContainer container, NamespacedKey key) {
        super(null);
        this.container = container;
        this.key = key;
    }

    @Override
    public void visitBoolean(boolean b) {
        this.container.set(key, PersistentDataType.BYTE, (byte) (b ? 1 : 0));
    }

    @Override
    public void visitInt(int i) {
        this.container.set(key, PersistentDataType.INTEGER, i);
    }

    @Override
    public void visitLong(long l) {
        this.container.set(key, PersistentDataType.LONG, l);
    }

    @Override
    public void visitFloat(float f) {
        this.container.set(key, PersistentDataType.FLOAT, f);
    }

    @Override
    public void visitDouble(double d) {
        this.container.set(key, PersistentDataType.DOUBLE, d);
    }

    @Override
    public void visitString(String s) {
        this.container.set(key, PersistentDataType.STRING, s);
    }

    @Override
    public MapVisitor visitMap() {
        return new PersistentTagWriter(this.container, this.key);
    }

    @Override
    public ListVisitor visitList() {
        return new PersistentListWriter(this.container, this.key);
    }
}
