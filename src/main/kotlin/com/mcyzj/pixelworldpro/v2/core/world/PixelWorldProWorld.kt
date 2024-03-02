package com.mcyzj.pixelworldpro.v2.core.world

import com.mcyzj.lib.plugin.file.BuiltOutConfiguration
import com.mcyzj.pixelworldpro.v2.core.PixelWorldPro
import com.mcyzj.pixelworldpro.v2.core.util.Config
import com.mcyzj.pixelworldpro.v2.core.world.compress.None
import com.mcyzj.pixelworldpro.v2.core.world.compress.Zip
import com.mcyzj.pixelworldpro.v2.core.world.dataclass.LocationData
import com.mcyzj.pixelworldpro.v2.core.world.dataclass.WorldData
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.entity.Player
import java.io.File


/**
 * PixelWorldPro世界
 * @return: PixelWorldProWorld
 */
@Suppress("unused")
class PixelWorldProWorld(val worldData: WorldData) {
    private val log = com.mcyzj.pixelworldpro.v2.core.PixelWorldPro.instance.log
    private val lang = Config.getLang()
    private val worldConfig = Config.world
    private val bungeeConfig = Config.bungee

    /**
     * 获取世界压缩锁
     */
    fun isCompress(): Boolean {
        val compressConfig = getDataConfig("compress")
        return compressConfig.getBoolean("block")
    }
    private fun setCompress(value: Boolean) {
        val compressConfig = getDataConfig("compress")
        compressConfig.set("block", value)
        compressConfig.saveToFile()
    }

    /**
     * 获取世界压缩方式
     */
    private fun getCompressMethod(): String {
        val blockConfig = getDataConfig("compress")
        return blockConfig.getString("method") ?: "None"
    }

    private fun setCompressMethod(value: String) {
        val blockConfig = getDataConfig("compress")
        blockConfig.set("method", value)
        blockConfig.saveToFile()
    }

    /**
     * 压缩世界
     */
    fun compress() {
        //开启压缩
        if (!isCompress()) {
            when (worldConfig.getString("compress.method")) {
                "None" -> {
                    None.toZip(worldData)
                    setCompressMethod("None")
                }

                "Zip" -> {
                    Zip.toZip(worldData)
                    setCompressMethod("Zip")
                }

                else -> {
                    None.toZip(worldData)
                    setCompressMethod("None")
                }
            }
        }
        //解开世界压缩锁
        setCompress(true)
        log.info(lang.getString("world.compress") + "${worldData.name}[${worldData.id}]")
    }

    /**
     * 解压缩世界
     */
    fun decompression() {
        if (isCompress()) {
            when (getCompressMethod()){
                "None" -> {
                    None.unZip(worldData)
                }

                "Zip" -> {
                    Zip.unZip(worldData)
                }

                else -> {
                    None.unZip(worldData)
                }
            }
            //锁定世界压缩锁
            setCompress(false)
            log.info(lang.getString("world.decompression") + "${worldData.name}[${worldData.id}]")
        }
    }

    /**
     * 是否已加载
     */
    fun isLoad(): Boolean {
        return if (bungeeConfig.getBoolean("enable")) {
            false
        } else {
            val world = Bukkit.getWorld("PixelWorldPro/cache/world/${worldData.id}/world")
            world != null
        }
    }

    /**
     * 加载世界
     */
    fun load(): World? {
        if (WorldCache.isInUnUse(this)){
            return null
        }
        if (!isLoad()) {
            decompression()
            log.info(lang.getString("world.load") + "${worldData.name}[${worldData.id}]")
            val worldCreator = WorldCreator("PixelWorldPro/cache/world/${worldData.id}/world")
            val worldDataConfig = getDataConfig("world")
            when (worldDataConfig.getString("worldCreator")) {
                "auto" -> {}
                else -> {
                    worldCreator.generator(worldDataConfig.getString("worldCreator"))
                }
            }
            val seed = worldDataConfig.getString("seed")
            if (seed != null) {
                worldCreator.seed(seed.toLong())
            }
            val world = Bukkit.createWorld(worldCreator) ?: return null
            world.keepSpawnInMemory = false
            return world
        }
        return null
    }

    /**
     * 卸载世界
     */
    fun unload() {
        if (isLoad()) {
            log.info(lang.getString("world.unload") + "${worldData.name}[${worldData.id}]")
            val world = Bukkit.getWorld("PixelWorldPro/cache/world/${worldData.id}/world")!!
            val mainWorld = Bukkit.getWorld("world")!!
            for (player in world.players) {
                player.teleport(mainWorld.spawnLocation)
            }
            Bukkit.unloadWorld(world, true)
            WorldCache.setUnUseWorld(this)
        }
    }
    /**
     * 获取数据文件
     */
    fun getDataFile(file: String): File {
        return File("./PixelWorldPro/World/${worldData.id}/data/${file}")
    }
    /**
     * 获取Yaml格式的数据文件
     */
    fun getDataConfig(file: String): BuiltOutConfiguration {
        return BuiltOutConfiguration("./PixelWorldPro/World/${worldData.id}/data/${file}.yml")
    }
    /**
     * 传送
     */
    fun teleport(player: Player) {
        if (!isLoad()) {
            load()
        }
        val world = Bukkit.getWorld("PixelWorldPro/cache/world/${worldData.id}/world")
        if (world != null) {
            val worldDataConfig = getDataConfig("world")
            val location = if (worldDataConfig.getConfigurationSection("location") != null) {
                LocationData(
                    worldDataConfig.getDouble("location.x"),
                    worldDataConfig.getDouble("location.y"),
                    worldDataConfig.getDouble("location.z")
                )
            } else {
                null
            }
            if (location != null) {
                player.teleport(
                    Location(world,location.x, location.y, location.z)
                )
            } else {
                player.teleport(world.spawnLocation)
            }
        }
    }
}