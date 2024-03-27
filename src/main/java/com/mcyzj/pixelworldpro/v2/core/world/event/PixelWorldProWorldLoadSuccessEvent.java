package com.mcyzj.pixelworldpro.v2.core.world.event;

import com.mcyzj.pixelworldpro.v2.core.world.PixelWorldProWorld;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class PixelWorldProWorldLoadSuccessEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
    private final PixelWorldProWorld world;
    private boolean cancelled;

    public PixelWorldProWorldLoadSuccessEvent(PixelWorldProWorld worlds) {
        world = worlds;
    }

    public PixelWorldProWorld getWorld() {
        return world;
    }


    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
