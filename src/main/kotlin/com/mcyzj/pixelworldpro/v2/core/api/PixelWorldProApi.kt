package com.mcyzj.pixelworldpro.v2.core.api

import com.mcyzj.pixelworldpro.v2.core.world.LocalWorld
import com.mcyzj.pixelworldpro.v2.core.world.PixelWorldProWorld
import java.util.*

class PixelWorldProApi {
    val database = com.mcyzj.pixelworldpro.v2.core.PixelWorldPro.databaseApi
    fun getWorld(id: Int): PixelWorldProWorld? {
        val worldData = database.getWorldData(id) ?: return null
        return PixelWorldProWorld(worldData)
    }
    fun getWorld(owner: UUID): PixelWorldProWorld? {
        val worldData = database.getWorldData(owner) ?: return null
        return PixelWorldProWorld(worldData)
    }
    fun createWorldLocal(owner: UUID, template: String?, seed: Long?) {
        LocalWorld.createWorldLocal(owner, template, seed)
    }
}