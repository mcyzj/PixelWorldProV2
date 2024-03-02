package com.mcyzj.pixelworldpro.v2.core.world

import com.mcyzj.lib.plugin.file.BuiltOutConfiguration
import com.mcyzj.pixelworldpro.v2.core.api.PixelWorldProApi
import com.mcyzj.pixelworldpro.v2.core.util.Config
import java.lang.Thread.sleep

object WorldCache {
    private val lang = Config.getLang()
    private val log = com.mcyzj.pixelworldpro.v2.core.PixelWorldPro.instance.log
    private val unUseList = ArrayList<Int>()

    fun getCacheConfig(file: String): BuiltOutConfiguration {
        return BuiltOutConfiguration("./PixelWorldPro/cache/$file")
    }

    fun cleanWorldCache() {
        val worldCacheConfig = getCacheConfig("world/unUse.yml")
        for (key in worldCacheConfig.getKeys(false)) {
            val time = worldCacheConfig.getLong(key)
            if (time < System.currentTimeMillis()) {
                val world = PixelWorldProApi().getWorld(key.toInt()) ?: continue
                try {
                    Thread {
                        world.compress()
                    }.start()
                    worldCacheConfig.set(key, null)
                    worldCacheConfig.saveToFile()
                    log.info(lang.getString("world.removeUnUse") + "${world.worldData.name}[${world.worldData.id}]")
                }catch (_:Exception){}
            }
        }
    }

    fun setUnUseWorld(world: PixelWorldProWorld) {
        Thread {
            val worldData = world.worldData
            val worldCacheConfig = getCacheConfig("world/unUse.yml")
            worldCacheConfig.set(worldData.id.toString(), System.currentTimeMillis() + (5 * 60 * 1000))
            worldCacheConfig.saveToFile()
            unUseList.add(worldData.id)
            log.info(lang.getString("world.addToUnUse") + "${worldData.name}[${worldData.id}]")
            sleep(60 * 1000)
            world.compress()
            worldCacheConfig.set(worldData.id.toString(), null)
            worldCacheConfig.saveToFile()
            unUseList.remove(worldData.id)
            log.info(lang.getString("world.removeUnUse") + "${worldData.name}[${worldData.id}]")
        }.start()
    }
    fun isInUnUse(world: PixelWorldProWorld): Boolean {
        return world.worldData.id in unUseList
    }
}