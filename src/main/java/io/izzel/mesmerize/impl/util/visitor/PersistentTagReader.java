package io.izzel.mesmerize.impl.util.visitor;

import io.izzel.mesmerize.api.visitor.MapVisitor;
import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.VisitMode;
import io.izzel.mesmerize.api.visitor.impl.AbstractValue;
import io.izzel.mesmerize.impl.util.Util;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.Map;

public class PersistentTagReader extends AbstractValue<PersistentDataContainer> {

    private final PersistentDataContainer container;

    public PersistentTagReader(PersistentDataContainer container) {
        this.container = container;
    }

    @Override
    public void accept(ValueVisitor visitor, VisitMode mode) {
        MapVisitor mapVisitor = visitor.visitMap();
        Map<String, ?> map = Util.mapOfContainer(this.container);
        for (String s : map.keySet()) {
            ValueVisitor valueVisitor = mapVisitor.visit(s);
            new PersistentValueReader(this.container, Util.fromString(s)).accept(valueVisitor, mode);
        }
        mapVisitor.visitEnd();
        visitor.visitEnd();
    }
}
