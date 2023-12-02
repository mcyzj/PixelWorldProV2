package com.mcyzj.pixelworldpro.api.objects

import com.mcyzj.pixelworldpro.data.dataclass.WorldData
import com.mcyzj.pixelworldpro.expansion.core.level.Change

object Level {
    fun setLevel(worldData: WorldData, level: Int){
        Change.setLevel(worldData, level)
    }
    fun getLevel(worldData: WorldData): Int {
        return Change.getLevel(worldData)
    }
    fun addLevel(worldData: WorldData){
        Change.addLevel(worldData)
    }
    fun addLevel(worldData: WorldData, number: Int){
        Change.addLevel(worldData, number)
    }
    fun removeLevel(worldData: WorldData){
        Change.removeLevel(worldData)
    }
    fun removeLevel(worldData: WorldData, number: Int){
        Change.removeLevel(worldData, number)
    }
}