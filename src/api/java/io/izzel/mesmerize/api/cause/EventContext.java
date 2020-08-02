package io.izzel.mesmerize.api.cause;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

public final class EventContext {

    public static EventContext create() {
        return new EventContext(MultimapBuilder.hashKeys().arrayListValues().build());
    }

    public static EventContext create(EventContext context) {
        return new EventContext(ArrayListMultimap.create(context.entries));
    }

    private final ListMultimap<ContextKey<?>, Object> entries;

    EventContext(Multimap<ContextKey<?>, Object> values) {
        this.entries = ArrayListMultimap.create(values);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> get(ContextKey<T> key) {
        Preconditions.checkNotNull(key, "key");
        return ImmutableList.copyOf((List<T>) this.entries.get(key));
    }

    public <T> T require(ContextKey<T> key) {
        final List<T> optional = get(key);
        if (!optional.isEmpty()) {
            return optional.get(0);
        }
        throw new NoSuchElementException(key.getId());
    }

    public <T> boolean add(ContextKey<T> key, T value) {
        return this.entries.put(key, value);
    }

    public <T> boolean remove(ContextKey<T> key, T value) {
        return this.entries.remove(key, value);
    }

    public <T> void removeAll(ContextKey<T> key) {
        this.entries.removeAll(key);
    }

    public boolean containsKey(ContextKey<?> key) {
        return this.entries.containsKey(key);
    }

    public Set<ContextKey<?>> keySet() {
        return this.entries.keySet();
    }

    public Multimap<ContextKey<?>, Object> asMap() {
        return this.entries;
    }
}
