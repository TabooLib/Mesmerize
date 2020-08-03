package io.izzel.mesmerize.api.event;

import io.izzel.mesmerize.api.Stats;
import io.izzel.mesmerize.api.visitor.StatsValue;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StatsApplyEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Stats<?> stats;
    private final Event sourceEvent;
    private StatsValue<?> value;
    private boolean cancelled;

    public StatsApplyEvent(Stats<?> stats, StatsValue<?> value, Event sourceEvent) {
        this.stats = stats;
        this.value = value;
        this.sourceEvent = sourceEvent;
    }

    public Stats<?> getStats() {
        return stats;
    }

    public @NotNull StatsValue<?> getValue() {
        return value;
    }

    public void setValue(@NotNull StatsValue<?> value) {
        this.value = value;
    }

    public @Nullable Event getSourceEvent() {
        return sourceEvent;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
