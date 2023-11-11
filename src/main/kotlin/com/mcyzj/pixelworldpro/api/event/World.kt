package com.mcyzj.pixelworldpro.api.event

import com.mcyzj.pixelworldpro.data.dataclass.WorldData
import org.bukkit.event.Event
import org.bukkit.event.HandlerList


class WorldCreateSuccess(val worldData: WorldData) : Event(){
    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        val handlerList = HandlerList()
    }
}

class WorldLoadSuccess(val worldData: WorldData) : Event(){
    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        val handlerList = HandlerList()
    }
}
class WorldUnloadSuccess(val worldData: WorldData) : Event(){
    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        val handlerList = HandlerList()
    }
}