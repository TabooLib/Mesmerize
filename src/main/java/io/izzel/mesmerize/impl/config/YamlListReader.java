package io.izzel.mesmerize.impl.config;

import io.izzel.mesmerize.api.visitor.ListVisitor;
import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.VisitMode;
import io.izzel.mesmerize.api.visitor.impl.AbstractValue;

import java.util.Collection;
import java.util.List;

public class YamlListReader extends AbstractValue<List<?>> {

    private final Collection<?> collection;

    public YamlListReader(Collection<?> collection) {
        this.collection = collection;
    }

    @Override
    public void accept(ValueVisitor visitor, VisitMode mode) {
        ListVisitor listVisitor = visitor.visitList();
        listVisitor.visitLength(collection.size());
        int i = 0;
        for (Object o : collection) {
            new YamlValueReader(o).accept(listVisitor.visit(i++), mode);
        }
        listVisitor.visitEnd();
        visitor.visitEnd();
    }
}
