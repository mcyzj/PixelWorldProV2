package com.mcyzj.pixelworldpro.v2.core.api

import com.mcyzj.pixelworldpro.v2.core.util.Config
import com.mcyzj.pixelworldpro.v2.core.world.WorldImpl
import com.mcyzj.pixelworldpro.v2.core.world.LocalWorld
import com.mcyzj.pixelworldpro.v2.core.world.PixelWorldProWorld
import java.util.*

class PixelWorldProApi {
    val database = com.mcyzj.pixelworldpro.v2.core.PixelWorldPro.databaseApi
    val localBungeeExecution = Config.bungee.getBoolean("enable")
    fun getWorld(id: Int, bungeeExecution: Boolean = localBungeeExecution): PixelWorldProWorld? {
        val worldData = database.getWorldData(id) ?: return null
        return PixelWorldProWorld(worldData, bungeeExecution)
    }
    fun getWorld(owner: UUID, bungeeExecution: Boolean = localBungeeExecution): PixelWorldProWorld? {
        val worldData = database.getWorldData(owner) ?: return null
        return PixelWorldProWorld(worldData, bungeeExecution)
    }
    fun createWorldLocal(owner: UUID, template: String?, seed: Long?) {
        WorldImpl.createWorldLocal(owner, template, seed)
    }
}