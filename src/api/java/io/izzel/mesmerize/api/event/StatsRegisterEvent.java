package io.izzel.mesmerize.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class StatsRegisterEvent extends Event {

    private static final HandlerList LIST = new HandlerList();

    private final StatsRegistry registry;
    private final StatsManager manager;

    public StatsRegisterEvent(StatsRegistry registry, StatsManager manager) {
        this.registry = registry;
        this.manager = manager;
    }

    public StatsRegistry getRegistry() {
        return registry;
    }

    public StatsManager getManager() {
        return manager;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return LIST;
    }

    public static HandlerList getHandlerList() {
        return LIST;
    }
}
