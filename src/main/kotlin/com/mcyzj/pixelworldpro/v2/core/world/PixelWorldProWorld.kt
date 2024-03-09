package com.mcyzj.pixelworldpro.v2.core.world

import com.mcyzj.lib.plugin.file.BuiltOutConfiguration
import com.mcyzj.pixelworldpro.v2.core.PixelWorldPro
import com.mcyzj.pixelworldpro.v2.core.permission.dataclass.ResultData
import com.mcyzj.pixelworldpro.v2.core.util.Config
import com.mcyzj.pixelworldpro.v2.core.world.compress.None
import com.mcyzj.pixelworldpro.v2.core.world.compress.Zip
import com.mcyzj.pixelworldpro.v2.core.world.dataclass.WorldData
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Player
import java.io.File
import java.util.concurrent.CompletableFuture

class PixelWorldProWorld(val worldData: WorldData, val bungeeExecution: Boolean = Config.bungee.getBoolean("enable")){
    private val log = PixelWorldPro.instance.log
    private val lang = Config.getLang()
    private val worldConfig = Config.world

    /**
     * 获取世界压缩锁
     */
    private fun isCompress(): Boolean {
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
     * 获取数据文件
     */
    fun getDataFile(file: String): File {
        return File("./PixelWorldPro/world/${worldData.type}/${worldData.id}/data/${file}")
    }
    /**
     * 获取Yaml格式的数据文件
     */
    fun getDataConfig(file: String): BuiltOutConfiguration {
        return BuiltOutConfiguration("./PixelWorldPro/world/${worldData.type}/${worldData.id}/data/${file}.yml")
    }

    fun tickets(): Double {
        if (!isLoad().get()) {
            return 0.0
        }
        val initial = worldConfig.getDouble("tickets.initial")
        val tpsMax = worldConfig.getDouble("tickets.tps.max")
        val tpsWeight = worldConfig.getDouble("tickets.tps.weight")
        val world = worldConfig.getDouble("tickets.world")
        val player = worldConfig.getDouble("tickets.player")
        val tpsTickets = (tpsMax - Bukkit.getTPS().first()) * tpsWeight
        val playerList = WorldImpl.onlinePlayer[worldData.id] ?: ArrayList<Player>()
        val playerTickets = playerList.size * player
        return initial + tpsTickets + world + playerTickets
    }

    fun load(): CompletableFuture<ResultData> {
        TODO("Not yet implemented")
    }

    fun unload(): CompletableFuture<ResultData> {
        TODO("Not yet implemented")
    }

    fun isLoad(): CompletableFuture<Boolean> {
        TODO("Not yet implemented")
    }

    fun teleport(player: Player) {
        TODO("Not yet implemented")
    }

    fun getWorlds(): HashMap<String, World>? {
        return null
    }
}