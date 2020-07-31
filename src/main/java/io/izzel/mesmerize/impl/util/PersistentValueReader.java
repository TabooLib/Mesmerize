package io.izzel.mesmerize.impl.util;

import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractValue;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PersistentValueReader extends AbstractValue<Object> {

    private final PersistentDataContainer container;
    private final NamespacedKey key;

    public PersistentValueReader(PersistentDataContainer container, NamespacedKey key) {
        this.container = container;
        this.key = key;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void accept(ValueVisitor visitor) {
        int i = Util.typeOfKey(this.container, this.key);
        switch (i) {
            case 1:
                visitor.visitBoolean(this.container.get(this.key, PersistentDataType.BYTE) != 0);
                visitor.visitEnd();
                break;
            case 3:
                visitor.visitInt(this.container.get(this.key, PersistentDataType.INTEGER));
                visitor.visitEnd();
                break;
            case 4:
                visitor.visitLong(this.container.get(this.key, PersistentDataType.LONG));
                visitor.visitEnd();
                break;
            case 5:
                visitor.visitFloat(this.container.get(this.key, PersistentDataType.FLOAT));
                visitor.visitEnd();
                break;
            case 6:
                visitor.visitDouble(this.container.get(this.key, PersistentDataType.DOUBLE));
                visitor.visitEnd();
                break;
            case 8:
                visitor.visitString(this.container.get(this.key, PersistentDataType.STRING));
                visitor.visitEnd();
                break;
            case 10:
                PersistentDataContainer container = this.container.get(this.key, PersistentDataType.TAG_CONTAINER);
                if (container.has(Util.ARRAY_LENGTH, PersistentDataType.INTEGER)) {
                    new PersistentListReader(container).accept(visitor);
                } else {
                    new PersistentTagReader(container).accept(visitor);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown data " + Util.mapOfContainer(this.container).get(this.key.toString()));
        }
    }
}
