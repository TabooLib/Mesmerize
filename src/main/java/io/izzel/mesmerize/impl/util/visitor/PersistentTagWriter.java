package io.izzel.mesmerize.impl.util.visitor;

import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractMapVisitor;
import io.izzel.mesmerize.impl.util.Util;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PersistentTagWriter extends AbstractMapVisitor {

    private final PersistentDataContainer owner;
    private final NamespacedKey ownerKey;
    private final PersistentDataContainer container;

    public PersistentTagWriter(PersistentDataContainer owner, NamespacedKey ownerKey) {
        super(null);
        this.owner = owner;
        this.ownerKey = ownerKey;
        this.container = owner.getAdapterContext().newPersistentDataContainer();
    }

    @Override
    public ValueVisitor visit(String key) {
        return new PersistentValueWriter(this.container, Util.fromString(key));
    }

    @Override
    public void visitEnd() {
        this.owner.set(this.ownerKey, PersistentDataType.TAG_CONTAINER, this.container);
    }
}
