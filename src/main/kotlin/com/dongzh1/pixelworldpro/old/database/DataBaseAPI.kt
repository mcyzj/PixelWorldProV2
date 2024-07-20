package com.dongzh1.pixelworldpro.old.database

import com.mcyzj.pixelworldpro.v2.core.api.PixelWorldProApi
import java.util.UUID

class DataBaseAPI {
    fun getWorldData(uuid: UUID): WorldData? {
        val newWorld = PixelWorldProApi().getWorld(uuid) ?: return null
        val newWorldData = newWorld.worldData
        return WorldData(
            "PixelWorldPro/cache/world/${newWorldData.type}/${newWorldData.id}/world",
            newWorld.getLevel().toString(),
            listOf(),
            listOf(),
            listOf(),
            listOf(),
            "unknown",
            "0000",
            "0000".toLong(),
            0,
            false,
            false,
            listOf(),
            HashMap(),
            HashMap()
        )
    }

    fun getWorldData(name: String): WorldData? {
        val newWorld = PixelWorldProApi().getWorld(name) ?: return null
        val newWorldData = newWorld.worldData
        return WorldData(
            "PixelWorldPro/cache/world/${newWorldData.type}/${newWorldData.id}/world",
            newWorld.getLevel().toString(),
            listOf(),
            listOf(),
            listOf(),
            listOf(),
            "unknown",
            "0000",
            "0000".toLong(),
            0,
            false,
            false,
            listOf(),
            HashMap(),
            HashMap()
        )
    }
}