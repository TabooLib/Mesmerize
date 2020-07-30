package io.izzel.mesmerize.impl.util;

import io.izzel.mesmerize.api.visitor.StatsValue;
import io.izzel.mesmerize.api.visitor.ValueVisitor;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;

public class PersistentValueNode extends PersistentValueWriter implements StatsValue<PersistentDataContainer> {

    private static final String PREFIX = "minecraft:";

    public PersistentValueNode(PersistentDataContainer container) {
        super(container);
    }

    @Override
    public PersistentDataContainer get() {
        return this.container;
    }

    @Override
    public void accept(ValueVisitor visitor) {
        Map<String, ?> container = Util.mapOfContainer(this.container);
        for (String s : container.keySet()) {
            s = s.substring(PREFIX.length());
            NamespacedKey key = NamespacedKey.minecraft(s);
            int i = Util.typeOfKey(this.container, key);
            if (i == 2 /* SHORT */) {
                int len = this.container.getOrDefault(key, PersistentDataType.SHORT, Short.MIN_VALUE);
                for (int j = 0; j < len; j++) {
                    visitor.visitIndex(j);
                    Util.dump(this.container, NamespacedKey.minecraft(s + "." + j), visitor);
                }
            } else {
                Util.dump(this.container, key, visitor);
            }
        }
    }

    @Override
    protected NamespacedKey currentKey() {
        if (lastKey != null) {
            if (lastIndex != -1) throw new IllegalStateException("close");
            else return NamespacedKey.minecraft(lastKey);
        } else {
            if (lastIndex == -1) throw new IllegalStateException("empty");
            return NamespacedKey.minecraft(String.valueOf(lastIndex));
        }
    }
}
