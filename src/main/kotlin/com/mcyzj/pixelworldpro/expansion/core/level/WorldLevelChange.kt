package com.mcyzj.pixelworldpro.expansion.core.level

import com.mcyzj.pixelworldpro.data.dataclass.WorldData
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

class WorldLevelChange(val worldData: WorldData, val level: Int) : Event(){
    private val handlers = HandlerList()

    fun getHandlerList(): HandlerList {
        return handlers
    }
    override fun getHandlers(): HandlerList {
        return handlers
    }
}
