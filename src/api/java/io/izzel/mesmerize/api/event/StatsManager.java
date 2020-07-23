package io.izzel.mesmerize.api.event;

import io.izzel.mesmerize.api.visitor.impl.StatsDataSet;

import java.util.Optional;

public interface StatsManager {

    Optional<StatsDataSet> get(String id);
}
