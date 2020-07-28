package io.izzel.mesmerize.api.visitor.util;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import io.izzel.mesmerize.api.visitor.InfoHolder;
import io.izzel.mesmerize.api.visitor.InfoKey;
import io.izzel.mesmerize.api.visitor.InfoVisitor;
import io.izzel.mesmerize.api.visitor.StatsVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class InfoStatsSet extends StatsSet implements InfoHolder {

    private final ListMultimap<InfoKey<?>, ?> info = MultimapBuilder.hashKeys().arrayListValues().build();
    private final MultimapInfoReader reader = new MultimapInfoReader(info);

    @Override
    public InfoVisitor visitInfo() {
        return new MultimapInfoWriter(this.info);
    }

    @Override
    public <T> Optional<T> get(@NotNull InfoKey<T> key) {
        return reader.get(key);
    }

    @Override
    public <T> void add(@NotNull InfoKey<T> key, @NotNull T value) {
        reader.add(key, value);
    }

    @Override
    public <T> List<T> getAll(@NotNull InfoKey<T> key) {
        return reader.getAll(key);
    }

    @Override
    public void accept(StatsVisitor visitor) {
        this.reader.accept(visitor);
        super.accept(visitor);
    }
}
