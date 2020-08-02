package io.izzel.mesmerize.impl.config;

import io.izzel.mesmerize.api.visitor.MapVisitor;
import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.VisitMode;
import io.izzel.mesmerize.api.visitor.impl.AbstractValue;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

public class YamlMapReader extends AbstractValue<Object> {

    private final Map<String, Object> map;

    public YamlMapReader(ConfigurationSection section) {
        this.map = section.getValues(false);
    }

    public YamlMapReader(Map<String, Object> map) {
        this.map = map;
    }

    @Override
    public void accept(ValueVisitor visitor, VisitMode mode) {
        MapVisitor mapVisitor = visitor.visitMap();
        for (Map.Entry<String, Object> entry : this.map.entrySet()) {
            ValueVisitor valueVisitor = mapVisitor.visit(entry.getKey());
            new YamlValueReader(entry.getValue()).accept(valueVisitor, mode);
        }
        mapVisitor.visitEnd();
        visitor.visitEnd();
    }
}
