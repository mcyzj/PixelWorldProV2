package com.mcyzj.pixelworldpro.v2.core.level.event;

import com.mcyzj.pixelworldpro.v2.core.world.PixelWorldProWorld;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PixelWorldProLevelChangeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final PixelWorldProWorld world;

    private boolean cancelled;
    private final Integer localLevel;

    private final Integer nextLevel;

    public PixelWorldProLevelChangeEvent(PixelWorldProWorld worlds, Integer localLevels, Integer nextLevels) {
        world = worlds;
        localLevel = localLevels;
        nextLevel = nextLevels;
    }

    public PixelWorldProWorld getWorld() {
        return world;
    }

    public int getLocalLevel() {
        return localLevel;
    }

    public int getNextLevel() {
        return nextLevel;
    }


    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}