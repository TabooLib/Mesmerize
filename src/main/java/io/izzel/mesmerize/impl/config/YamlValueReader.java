package io.izzel.mesmerize.impl.config;

import io.izzel.mesmerize.api.visitor.StatsValue;
import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.VisitMode;
import io.izzel.mesmerize.api.visitor.impl.AbstractValue;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collection;
import java.util.Map;

public class YamlValueReader extends AbstractValue<Object> {

    private final Object o;

    public YamlValueReader(ConfigurationSection section, String key) {
        this.o = section.get(key);
    }

    public YamlValueReader(Object o) {
        this.o = o;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void accept(ValueVisitor visitor, VisitMode mode) {
        StatsValue<?> externalValue = visitor.getExternalValue();
        if (externalValue != null) {
            externalValue.accept(visitor, mode);
            return;
        }
        if (o instanceof Boolean) {
            visitor.visitBoolean((Boolean) o);
            visitor.visitEnd();
        } else if (o instanceof Integer) {
            visitor.visitInt(((Integer) o));
            visitor.visitEnd();
        } else if (o instanceof Long) {
            visitor.visitLong(((Long) o));
            visitor.visitEnd();
        } else if (o instanceof Double) {
            visitor.visitDouble(((Double) o));
            visitor.visitEnd();
        } else if (o instanceof String) {
            visitor.visitString(((String) o));
            visitor.visitEnd();
        } else if (o instanceof Collection) {
            new YamlListReader(((Collection<?>) o)).accept(visitor, mode);
        } else if (o instanceof ConfigurationSection) {
            new YamlMapReader(((ConfigurationSection) o)).accept(visitor, mode);
        } else if (o instanceof Map) {
            new YamlMapReader(((Map<String, Object>) o)).accept(visitor, mode);
        } else {
            throw new RuntimeException("unknown data " + o);
        }
    }
}
