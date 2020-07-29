package io.izzel.mesmerize.api.service;

import io.izzel.mesmerize.api.visitor.StatsHolder;

import java.util.Optional;

public interface StatsManager {

    Optional<StatsHolder> get(String id);
}
