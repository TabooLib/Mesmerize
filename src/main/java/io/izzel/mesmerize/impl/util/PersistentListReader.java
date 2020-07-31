package io.izzel.mesmerize.impl.util;

import com.google.common.base.Preconditions;
import io.izzel.mesmerize.api.visitor.ListVisitor;
import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractValue;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PersistentListReader extends AbstractValue<PersistentDataContainer> {

    private final PersistentDataContainer container;

    public PersistentListReader(PersistentDataContainer container) {
        Preconditions.checkArgument(container.has(Util.ARRAY_LENGTH, PersistentDataType.INTEGER), "array_length");
        this.container = container;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void accept(ValueVisitor visitor) {
        ListVisitor listVisitor = visitor.visitList();
        int len = this.container.get(Util.ARRAY_LENGTH, PersistentDataType.INTEGER);
        listVisitor.visitLength(len);
        for (int i = 0; i < len; i++) {
            ValueVisitor valueVisitor = listVisitor.visit(i);
            new PersistentValueReader(this.container, NamespacedKey.minecraft(String.valueOf(i))).accept(valueVisitor);
        }
        listVisitor.visitEnd();
        visitor.visitEnd();
    }
}
