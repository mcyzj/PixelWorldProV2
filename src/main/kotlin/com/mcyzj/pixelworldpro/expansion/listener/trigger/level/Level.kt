package com.mcyzj.pixelworldpro.expansion.listener.trigger.level

import com.mcyzj.pixelworldpro.data.dataclass.WorldData
import com.mcyzj.pixelworldpro.expansion.listener.ListenerRegister

object Level {
    fun levelChange(worldData: WorldData, level: Int){
        val map = ListenerRegister.getLevelListener()
        for (value in map.values){
            value.levelChange(worldData, level)
        }
    }
}