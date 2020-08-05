package io.izzel.mesmerize.api.event;

import io.izzel.mesmerize.api.visitor.util.StatsSet;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public interface DamageCalculator {

    double calculate(@Nullable Event event, double baseDamage, StatsSet source, StatsSet target);
}
