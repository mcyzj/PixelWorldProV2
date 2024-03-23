package com.mcyzj.pixelworldpro.v2.core.world.compress

import com.mcyzj.pixelworldpro.v2.core.world.dataclass.WorldData
import java.io.File
import java.io.IOException

object None {
    fun toZip(worldData: WorldData){
        try {
            val worldFile = File(File("./PixelWorldPro/world/${worldData.type}", worldData.id.toString()), "world")
            if (worldFile.exists()){
                worldFile.deleteRecursively()
            }
            worldFile.mkdirs()
            val cacheFile = File("./PixelWorldPro/cache/world/${worldData.type}", worldData.id.toString())
            cacheFile.copyRecursively(worldFile)
            cacheFile.deleteRecursively()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun unZip(worldData: WorldData){
        try {
            val cacheFile = File("./PixelWorldPro/cache/world/${worldData.type}", worldData.id.toString())
            val worldFile = File(File("./PixelWorldPro/world/${worldData.type}", worldData.id.toString()), "world")
            if (cacheFile.exists()){
                cacheFile.deleteRecursively()
            }
            worldFile.copyRecursively(cacheFile)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}