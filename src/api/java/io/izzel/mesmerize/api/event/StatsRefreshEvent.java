package io.izzel.mesmerize.api.event;

import io.izzel.mesmerize.api.visitor.util.StatsSet;
import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.jetbrains.annotations.NotNull;

public class StatsRefreshEvent extends EntityEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final StatsSet statsSet;

    public StatsRefreshEvent(@NotNull Entity what, StatsSet statsSet) {
        super(what);
        this.statsSet = statsSet;
    }

    public StatsSet getStatsSet() {
        return statsSet;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
