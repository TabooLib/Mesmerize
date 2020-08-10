package io.izzel.mesmerize.api.visitor.util;

import io.izzel.mesmerize.api.Stats;
import io.izzel.mesmerize.api.service.StatsService;
import io.izzel.mesmerize.api.visitor.ListVisitor;
import io.izzel.mesmerize.api.visitor.MapVisitor;
import io.izzel.mesmerize.api.visitor.StatsHolder;
import io.izzel.mesmerize.api.visitor.StatsValue;
import io.izzel.mesmerize.api.visitor.StatsVisitor;
import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.VisitMode;
import io.izzel.mesmerize.api.visitor.impl.AbstractListVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractMapVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractStatsVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractValueVisitor;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class StatsSet extends AbstractStatsVisitor implements StatsHolder.Modifiable {

    private final Map<Stats<?>, StatsValue<?>> map = new HashMap<>();

    public StatsSet() {
        this(null);
    }

    public StatsSet(StatsVisitor visitor) {
        super(visitor);
    }

    @Override
    public <T, V extends StatsValue<T>> Optional<V> get(Stats<T> stats) {
        List<V> list = getAll(stats);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T, V extends StatsValue<T>> List<V> getAll(Stats<T> stats) {
        ArrayList<V> list = new ArrayList<>();
        StatsValue<?> value = map.get(stats);
        if (value != null) {
            list.add((V) value);
        }
        return list;
    }

    @Override
    public Set<Stats<?>> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<Map.Entry<Stats<?>, StatsValue<?>>> entrySet() {
        return map.entrySet();
    }

    @Override
    public boolean containsKey(Stats<?> stats) {
        return map.containsKey(stats);
    }

    @Override
    public void accept(StatsVisitor visitor, VisitMode mode) {
        for (Map.Entry<Stats<?>, StatsValue<?>> entry : entrySet()) {
            ValueVisitor stats = visitor.visitStats(entry.getKey());
            entry.getValue().accept(stats, mode);
        }
        visitor.visitEnd();
    }

    @Override
    public <T> Iterator<StatsValue<T>> iterator(Stats<T> stats) {
        return getAll(stats).iterator();
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public <T> ValueVisitor visitStats(@NotNull Stats<T> stats) {
        return new MergeVal<T>(stats.newValue(), stats) {

            @SuppressWarnings("unchecked")
            @Override
            public void visitEnd() {
                super.visitEnd();
                StatsValue<T> value = (StatsValue<T>) this.visitor;
                Optional<StatsValue<T>> optional = get(stats);
                StatsValue<T> merged = optional.isPresent() ? stats.mergeValue(optional.get(), value) : value;
                map.put(stats, merged);
            }
        };
    }

    private class MergeVal<T> extends AbstractValueVisitor {

        private final Stats<T> stats;

        public MergeVal(ValueVisitor visitor, Stats<T> stats) {
            super(visitor);
            this.stats = stats;
        }

        @Override
        public StatsVisitor visitStats() {
            return StatsSet.this;
        }

        @Override
        public ListVisitor visitList() {
            return new AbstractListVisitor(super.visitList()) {
                @Override
                public ValueVisitor visit(int index) {
                    return new MergeVal<>(super.visit(index), stats);
                }
            };
        }

        @Override
        public MapVisitor visitMap() {
            return new AbstractMapVisitor(super.visitMap()) {
                @Override
                public ValueVisitor visit(String key) {
                    return new MergeVal<>(super.visit(key), stats);
                }
            };
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void addAll(StatsSet set) {
        for (Map.Entry<Stats<?>, ? extends StatsValue<?>> entry : set.entrySet()) {
            Stats stats = entry.getKey();
            Optional<StatsValue<?>> optional = this.get(stats);
            StatsValue<?> merged = optional.isPresent() ? stats.mergeValue(optional.get(), entry.getValue()) : entry.getValue();
            map.put(stats, merged);
        }
    }

    public static StatsSet of(@NotNull Entity entity) {
        return StatsService.instance().cachedSetFor(entity);
    }

    public static StatsSet of(@NotNull ItemStack itemStack) {
        StatsSet set = new StatsSet();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            StatsService.instance().newStatsHolder(itemMeta).accept(set, VisitMode.VALUE);
        }
        return set;
    }
}
