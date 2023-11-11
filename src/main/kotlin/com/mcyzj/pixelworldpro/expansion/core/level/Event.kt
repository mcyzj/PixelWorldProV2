package com.mcyzj.pixelworldpro.expansion.core.level

import com.mcyzj.pixelworldpro.data.dataclass.WorldData
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class WorldLevelChange(val worldData: WorldData, val level: Int) : Event(){
    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        val handlerList = HandlerList()
    }
}