package io.izzel.mesmerize.impl.util.visitor.external;

import io.izzel.mesmerize.api.visitor.ListVisitor;
import io.izzel.mesmerize.api.visitor.MapVisitor;
import io.izzel.mesmerize.api.visitor.StatsVisitor;
import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractListVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractMapVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractValueVisitor;
import io.izzel.mesmerize.api.visitor.util.StatsAsMapVisitor;
import io.izzel.mesmerize.impl.util.Util;
import io.izzel.mesmerize.impl.util.visitor.PersistentValueWriter;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ExternalWriter extends AbstractMapVisitor {

    private final PersistentDataContainer owner;
    private final NamespacedKey key;
    private final PersistentDataContainer container;
    boolean modified;

    public ExternalWriter(PersistentDataContainer owner, NamespacedKey key) {
        super(null);
        this.owner = owner;
        this.key = key;
        this.container = owner.getOrDefault(key, PersistentDataType.TAG_CONTAINER, owner.getAdapterContext().newPersistentDataContainer());
    }

    @Override
    public ValueVisitor visit(String key) {
        return new ValueWriter(container, Util.fromString(key)) {
            @Override
            public void visitEnd() {
                super.visitEnd();
                ExternalWriter.this.modified |= this.modified;
            }
        };
    }

    @Override
    public void visitEnd() {
        if (modified) {
            this.owner.set(key, PersistentDataType.TAG_CONTAINER, container);
        }
    }

    private static class ValueWriter extends AbstractValueVisitor {

        private final PersistentDataContainer container;
        private final NamespacedKey key;
        boolean modified = false;

        public ValueWriter(PersistentDataContainer container, NamespacedKey key) {
            super(null);
            this.container = container;
            this.key = key;
        }

        @Override
        public ValueVisitor visitExternal() {
            return new External(this.container, this.key) {
                @Override
                public void visitEnd() {
                    super.visitEnd();
                    ValueWriter.this.modified |= this.modified;
                }
            };
        }

        @Override
        public MapVisitor visitMap() {
            return new ExternalWriter(this.container, this.key) {
                @Override
                public void visitEnd() {
                    super.visitEnd();
                    ValueWriter.this.modified |= this.modified;
                }
            };
        }

        @Override
        public ListVisitor visitList() {
            return new ListWriter(this.container, this.key) {
                @Override
                public void visitEnd() {
                    super.visitEnd();
                    ValueWriter.this.modified |= this.modified;
                }
            };
        }

        @Override
        public StatsVisitor visitStats() {
            return new StatsAsMapVisitor(this.visitMap());
        }
    }

    private static class External extends AbstractValueVisitor {

        private final PersistentDataContainer owner;
        private final NamespacedKey key;
        private final PersistentDataContainer container;
        private final ValueVisitor writer;
        boolean modified = false;

        public External(PersistentDataContainer owner, NamespacedKey key) {
            super(null);
            this.owner = owner;
            this.key = key;
            this.container = owner.getOrDefault(key, PersistentDataType.TAG_CONTAINER, owner.getAdapterContext().newPersistentDataContainer());
            this.writer = new PersistentValueWriter(this.container, ExternalReader.VALUE);
        }

        @Override
        public void visitBoolean(boolean b) {
            writer.visitBoolean(b);
            this.modified = true;
        }

        @Override
        public void visitInt(int i) {
            writer.visitInt(i);
            this.modified = true;
        }

        @Override
        public void visitLong(long l) {
            writer.visitLong(l);
            this.modified = true;
        }

        @Override
        public void visitFloat(float f) {
            writer.visitFloat(f);
            this.modified = true;
        }

        @Override
        public void visitDouble(double d) {
            writer.visitDouble(d);
            this.modified = true;
        }

        @Override
        public void visitString(String s) {
            writer.visitString(s);
            this.modified = true;
        }

        @Override
        public MapVisitor visitMap() {
            this.modified = true;
            return writer.visitMap();
        }

        @Override
        public ListVisitor visitList() {
            this.modified = true;
            return writer.visitList();
        }

        @Override
        public StatsVisitor visitStats() {
            this.modified = true;
            return writer.visitStats();
        }

        @Override
        public ValueVisitor visitExternal() {
            return this;
        }

        @Override
        public void visitEnd() {
            if (modified) {
                this.owner.set(key, PersistentDataType.TAG_CONTAINER, container);
            }
        }
    }

    private static class ListWriter extends AbstractListVisitor {

        private final PersistentDataContainer owner;
        private final NamespacedKey key;
        private final PersistentDataContainer container;
        boolean modified = false;

        public ListWriter(PersistentDataContainer container, NamespacedKey key) {
            super(null);
            this.owner = container;
            this.key = key;
            this.container = container.getOrDefault(key, PersistentDataType.TAG_CONTAINER, container.getAdapterContext().newPersistentDataContainer());
        }

        @Override
        public ValueVisitor visit(int index) {
            return new ValueWriter(this.container, NamespacedKey.minecraft(String.valueOf(index))) {
                @Override
                public void visitEnd() {
                    super.visitEnd();
                    ListWriter.this.modified |= this.modified;
                }
            };
        }

        @Override
        public void visitEnd() {
            if (modified) {
                this.owner.set(key, PersistentDataType.TAG_CONTAINER, container);
            }
        }
    }
}
