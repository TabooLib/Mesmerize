package io.izzel.mesmerize.api.visitor.util;

import com.google.common.collect.ListMultimap;
import io.izzel.mesmerize.api.visitor.InfoHolder;
import io.izzel.mesmerize.api.visitor.InfoKey;
import io.izzel.mesmerize.api.visitor.InfoVisitor;
import io.izzel.mesmerize.api.visitor.StatsVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MultimapInfoReader implements InfoHolder {

    private final ListMultimap<InfoKey<?>, ?> multimap;

    public MultimapInfoReader(ListMultimap<InfoKey<?>, ?> multimap) {
        this.multimap = multimap;
    }

    @Override
    public <T> Optional<T> get(@NotNull InfoKey<T> key) {
        List<T> list = getAll(key);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public <T> void add(@NotNull InfoKey<T> key, @NotNull T value) {
        this.getAll(key).add(value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> getAll(@NotNull InfoKey<T> key) {
        return (List<T>) this.multimap.get(key);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void accept(StatsVisitor visitor) {
        InfoVisitor infoVisitor = visitor.visitInfo();
        for (Map.Entry<InfoKey<?>, ?> entry : multimap.entries()) {
            infoVisitor.visitInfo((InfoKey) entry.getKey(), entry.getValue());
        }
    }
}
