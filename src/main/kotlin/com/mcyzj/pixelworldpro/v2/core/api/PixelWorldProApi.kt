package com.mcyzj.pixelworldpro.v2.core.api

import com.mcyzj.pixelworldpro.v2.core.PixelWorldPro
import com.mcyzj.pixelworldpro.v2.core.database.DataBase
import com.mcyzj.pixelworldpro.v2.core.world.PixelWorldProWorld
import com.mcyzj.pixelworldpro.v2.core.world.WorldImpl
import java.util.*

class PixelWorldProApi {
    private val localBungeeExecution = PixelWorldPro.bungeeEnable
    fun getWorld(id: Int, bungeeExecution: Boolean = localBungeeExecution, type: String = "local"): PixelWorldProWorld? {
        val database = DataBase.getDataDriver(type)
        val worldData = database.getWorldData(id) ?: return null
        return PixelWorldProWorld(worldData, bungeeExecution)
    }
    fun getWorld(owner: UUID, bungeeExecution: Boolean = localBungeeExecution, type: String = "local"): PixelWorldProWorld? {
        val database = DataBase.getDataDriver(type)
        val worldData = database.getWorldData(owner) ?: return null
        return PixelWorldProWorld(worldData, bungeeExecution)
    }

    fun getWorld(worldName: String): PixelWorldProWorld? {
        val id: Int
        val type: String
        try {
            val nameList = worldName.split("/")
            id = nameList[nameList.size - 2].toInt()
            type = nameList[nameList.size - 3]

        } catch (_:Exception) {
            return null
        }
        return getWorld(id, localBungeeExecution, type)
    }
    fun createWorldLocal(owner: UUID, template: String?, seed: Long?) {
        WorldImpl.createWorldLocal(owner, template, seed)
    }
}