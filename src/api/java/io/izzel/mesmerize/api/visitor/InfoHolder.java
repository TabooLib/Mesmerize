package io.izzel.mesmerize.api.visitor;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public interface InfoHolder {

    <T> Optional<T> get(@NotNull InfoKey<T> key);

    <T> void add(@NotNull InfoKey<T> key, @NotNull T value);

    <T> List<T> getAll(@NotNull InfoKey<T> key);

    void accept(StatsVisitor visitor);
}
