package io.izzel.mesmerize.impl.util;

import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractListVisitor;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PersistentListWriter extends AbstractListVisitor {

    private final PersistentDataContainer owner;
    private final NamespacedKey ownerKey;
    private final PersistentDataContainer container;

    public PersistentListWriter(PersistentDataContainer owner, NamespacedKey ownerKey) {
        super(null);
        this.owner = owner;
        this.ownerKey = ownerKey;
        this.container = owner.getAdapterContext().newPersistentDataContainer();
    }

    @Override
    public void visitLength(int size) {
        this.container.set(Util.ARRAY_LENGTH, PersistentDataType.INTEGER, size);
    }

    @Override
    public ValueVisitor visit(int index) {
        return new PersistentValueWriter(this.container, NamespacedKey.minecraft(String.valueOf(index)));
    }

    @Override
    public void visitEnd() {
        this.owner.set(this.ownerKey, PersistentDataType.TAG_CONTAINER, this.container);
    }
}
