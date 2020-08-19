package io.izzel.mesmerize.impl.util.visitor;

import io.izzel.mesmerize.api.visitor.ExternalVisitor;
import io.izzel.mesmerize.api.visitor.ListVisitor;
import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.VisitMode;
import io.izzel.mesmerize.api.visitor.impl.AbstractValue;
import io.izzel.mesmerize.impl.util.Util;
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
    public void accept(ValueVisitor visitor, VisitMode mode) {
        ExternalVisitor externalVisitor = visitor.getExternalValue();
        if (externalVisitor != null && !externalVisitor.isVirtual()) {
            externalVisitor.accept(visitor, mode);
            return;
        }
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
                    new PersistentListReader(container).accept(visitor, mode);
                } else {
                    new PersistentTagReader(container).accept(visitor, mode);
                }
                break;
            case 11:
                int[] ints = this.container.get(this.key, PersistentDataType.INTEGER_ARRAY);
                ListVisitor intArrayVisitor = visitor.visitList();
                intArrayVisitor.visitLength(ints.length);
                for (int j = 0; j < ints.length; j++) {
                    ValueVisitor valueVisitor = intArrayVisitor.visit(j);
                    valueVisitor.visitInt(ints[j]);
                    valueVisitor.visitEnd();
                }
                intArrayVisitor.visitEnd();
                visitor.visitEnd();
                break;
            case 12:
                long[] longs = this.container.get(this.key, PersistentDataType.LONG_ARRAY);
                ListVisitor longArrayVisitor = visitor.visitList();
                longArrayVisitor.visitLength(longs.length);
                for (int j = 0; j < longs.length; j++) {
                    ValueVisitor valueVisitor = longArrayVisitor.visit(j);
                    valueVisitor.visitLong(longs[j]);
                    valueVisitor.visitEnd();
                }
                longArrayVisitor.visitEnd();
                visitor.visitEnd();
                break;
            default:
                visitor.visitEnd();
        }
    }
}
