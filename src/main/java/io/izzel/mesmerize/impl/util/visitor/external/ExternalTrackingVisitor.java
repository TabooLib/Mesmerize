package io.izzel.mesmerize.impl.util.visitor.external;

import io.izzel.mesmerize.api.Stats;
import io.izzel.mesmerize.api.visitor.ListVisitor;
import io.izzel.mesmerize.api.visitor.MapVisitor;
import io.izzel.mesmerize.api.visitor.StatsVisitor;
import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractListVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractMapVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractStatsVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractValueVisitor;
import io.izzel.mesmerize.impl.util.Util;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public interface ExternalTrackingVisitor {

    ExternalReader getExternal();

    class ValueTracker extends AbstractValueVisitor implements ExternalTrackingVisitor {

        private final ExternalReader reader;

        public ValueTracker(ValueVisitor visitor, ExternalReader reader) {
            super(visitor);
            this.reader = reader;
        }

        @Override
        public ExternalReader getExternal() {
            return reader;
        }

        @Override
        public MapVisitor visitMap() {
            return new MapTracker(super.visitMap(), reader);
        }

        @Override
        public ListVisitor visitList() {
            return new ListTracker(super.visitList(), reader);
        }

        @Override
        public StatsVisitor visitStats() {
            return new StatsTracker(super.visitStats(), reader);
        }
    }

    class MapTracker extends AbstractMapVisitor implements ExternalTrackingVisitor {

        private final ExternalReader reader;

        public MapTracker(MapVisitor visitor, ExternalReader reader) {
            super(visitor);
            this.reader = reader;
        }

        @Override
        public ValueVisitor visit(String key) {
            return new ValueTracker(super.visit(key), reader.child(Util.fromString(key)));
        }

        @Override
        public ExternalReader getExternal() {
            return reader;
        }
    }

    class ListTracker extends AbstractListVisitor implements ExternalTrackingVisitor {

        private final ExternalReader reader;

        public ListTracker(ListVisitor visitor, ExternalReader reader) {
            super(visitor);
            this.reader = reader;
        }

        @Override
        public ValueVisitor visit(int index) {
            return new ValueTracker(super.visit(index), reader.child(NamespacedKey.minecraft(String.valueOf(index))));
        }

        @Override
        public ExternalReader getExternal() {
            return reader;
        }
    }

    class StatsTracker extends AbstractStatsVisitor implements ExternalTrackingVisitor {

        private final ExternalReader reader;

        public StatsTracker(StatsVisitor visitor, ExternalReader reader) {
            super(visitor);
            this.reader = reader;
        }

        @Override
        public <T> ValueVisitor visitStats(@NotNull Stats<T> stats) {
            return new ValueTracker(super.visitStats(stats), reader.child(stats.getKey()));
        }

        @Override
        public ExternalReader getExternal() {
            return reader;
        }
    }
}
