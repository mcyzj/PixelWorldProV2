package com.mcyzj.pixelworldpro.v2.core.world.event;

import com.mcyzj.pixelworldpro.v2.core.world.PixelWorldProWorld;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Cancellable;

public final class PixelWorldProWorldLoadEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private PixelWorldProWorld world;
    private boolean cancelled;

    public PixelWorldProWorldLoadEvent(PixelWorldProWorld world) {
    }

    public PixelWorldProWorld getWorld() {
        return world;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
