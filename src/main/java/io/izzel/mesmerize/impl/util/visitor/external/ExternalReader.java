package io.izzel.mesmerize.impl.util.visitor.external;

import io.izzel.mesmerize.api.visitor.StatsValue;
import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.VisitMode;
import io.izzel.mesmerize.api.visitor.impl.AbstractValue;
import io.izzel.mesmerize.api.visitor.impl.AbstractValueVisitor;
import io.izzel.mesmerize.impl.util.visitor.PersistentValueReader;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public class ExternalReader extends AbstractValue<PersistentDataContainer> {

    @SuppressWarnings("deprecation")
    static final NamespacedKey VALUE = new NamespacedKey("mesmerize", "_value");

    private final PersistentDataContainer container;

    public ExternalReader(@Nullable PersistentDataContainer container) {
        this.container = container;
    }

    public boolean isVirtual() {
        return container == null;
    }

    public ExternalReader child(NamespacedKey key) {
        if (container == null || key == null) {
            return this;
        } else {
            PersistentDataContainer container = this.container.get(key, PersistentDataType.TAG_CONTAINER);
            return container == null ? null : new ExternalReader(container);
        }
    }

    @Override
    public void accept(ValueVisitor visitor, VisitMode mode) {
        if (container != null) {
            new PersistentValueReader(container, VALUE).accept(new AbstractValueVisitor(visitor) {
                @Override
                public StatsValue<?> getExternalValue() {
                    return null;
                }
            }, mode);
        } else {
            visitor.visitEnd();
        }
    }
}
