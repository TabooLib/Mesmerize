package io.izzel.mesmerize.api.cause;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

public final class EventContext {

    public static EventContext create() {
        return new EventContext(ArrayListMultimap.create(), ArrayListMultimap.create());
    }

    public static EventContext create(EventContext context) {
        return new EventContext(ArrayListMultimap.create(context.entries), ArrayListMultimap.create(context.stringEntries));
    }

    private final ListMultimap<ContextKey<?>, Object> entries;
    private final ListMultimap<String, Object> stringEntries;

    EventContext(Multimap<ContextKey<?>, Object> values, ListMultimap<String, Object> stringEntries) {
        this.entries = ArrayListMultimap.create(values);
        this.stringEntries = stringEntries;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> get(String contextKey) {
        Preconditions.checkNotNull(contextKey, "key");
        return (List<T>) ImmutableList.copyOf(this.stringEntries.get(contextKey));
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> get(ContextKey<T> key) {
        Preconditions.checkNotNull(key, "key");
        return ImmutableList.copyOf((List<T>) this.entries.get(key));
    }

    public <T> T require(String key) {
        final List<T> optional = get(key);
        if (!optional.isEmpty()) {
            return optional.get(0);
        }
        throw new NoSuchElementException(key);
    }

    public <T> T require(ContextKey<T> key) {
        final List<T> optional = get(key);
        if (!optional.isEmpty()) {
            return optional.get(0);
        }
        throw new NoSuchElementException(key.getId());
    }

    @SuppressWarnings("UnusedReturnValue")
    public <T> boolean add(ContextKey<T> key, T value) {
        this.stringEntries.put(key.getId(), value);
        return this.entries.put(key, value);
    }

    @SuppressWarnings("UnusedReturnValue")
    public <T> boolean remove(ContextKey<T> key, T value) {
        this.stringEntries.remove(key.getId(), value);
        return this.entries.remove(key, value);
    }

    public <T> void removeAll(ContextKey<T> key) {
        this.stringEntries.removeAll(key.getId());
        this.entries.removeAll(key);
    }

    public boolean containsKey(String key) {
        return this.stringEntries.containsKey(key);
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
