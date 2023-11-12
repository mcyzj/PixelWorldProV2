package com.mcyzj.pixelworldpro.api.event.world

import com.mcyzj.pixelworldpro.data.dataclass.WorldData
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class WorldUnloadSuccess(val worldData: WorldData) : Event(){
    private val handlers = HandlerList()

    fun getHandlerList(): HandlerList {
        return handlers
    }
    override fun getHandlers(): HandlerList {
        return handlers
    }
}