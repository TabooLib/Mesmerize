package io.izzel.mesmerize.api.visitor;

import com.google.common.collect.Iterators;
import io.izzel.mesmerize.api.Stats;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface StatsHolder {

    <T> Optional<StatsValue<T>> get(Stats<T> stats);

    <T> List<StatsValue<T>> getAll(Stats<T> stats);

    Set<Stats<?>> keySet();

    Collection<Map.Entry<Stats<?>, StatsValue<?>>> entrySet();

    boolean containsKey(Stats<?> stats);

    void accept(StatsVisitor visitor);

    default boolean isModifiable() {
        return this instanceof Modifiable;
    }

    @Contract("-> this")
    default Modifiable asModifiable() {
        return ((Modifiable) this);
    }

    interface Modifiable extends StatsHolder, StatsVisitor {

        <T> Iterator<StatsValue<T>> iterator(Stats<T> stats);

        void clear();

        default boolean remove(Stats<?> stats) {
            return Iterators.removeIf(iterator(stats), t -> true);
        }

        default <T> boolean remove(Stats<T> stats, @NotNull StatsValue<T> value) {
            return Iterators.removeIf(iterator(stats), value::equals);
        }
    }
}
