package com.mcyzj.pixelworldpro.v2.core.world

import com.mcyzj.lib.bukkit.submit
import com.mcyzj.lib.plugin.file.BuiltOutConfiguration
import com.mcyzj.pixelworldpro.v2.core.PixelWorldPro
import com.mcyzj.pixelworldpro.v2.core.database.DataBase
import com.mcyzj.pixelworldpro.v2.core.level.event.PixelWorldProLevelChangeEvent
import com.mcyzj.pixelworldpro.v2.core.permission.dataclass.ResultData
import com.mcyzj.pixelworldpro.v2.core.util.Config
import com.mcyzj.pixelworldpro.v2.core.world.compress.None
import com.mcyzj.pixelworldpro.v2.core.world.compress.Zip
import com.mcyzj.pixelworldpro.v2.core.world.dataclass.WorldData
import com.mcyzj.pixelworldpro.v2.core.world.event.PixelWorldProWorldLoadEvent
import com.mcyzj.pixelworldpro.v2.core.world.event.PixelWorldProWorldLoadSuccessEvent
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.World
import org.bukkit.entity.Player
import java.io.File
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class PixelWorldProWorld(val worldData: WorldData, bungeeExecution: Boolean = Config.bungee.getBoolean("enable")){
    private val log = PixelWorldPro.instance.log
    private val lang = Config.getLang()
    private val worldConfig = Config.world
    private val worldDriver = WorldCache.getWorldDriver(worldData.type, bungeeExecution)

    /**
     * 获取世界压缩锁
     */
    fun isCompress(): Boolean {
        val compressConfig = getDataConfig("compress")
        return compressConfig.getBoolean("block")
    }
    fun setCompress(value: Boolean) {
        val compressConfig = getDataConfig("compress")
        compressConfig.set("block", value)
        compressConfig.saveToFile()
    }

    /**
     * 获取世界压缩方式
     */
    fun getCompressMethod(): String {
        val blockConfig = getDataConfig("compress")
        return blockConfig.getString("method") ?: "None"
    }

    fun setCompressMethod(value: String) {
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
        val tpsTickets = try {
            (tpsMax - Bukkit.getTPS().first()) * tpsWeight
        } catch (_: Exception) {
            20.0
        }
        val playerList = WorldImpl.onlinePlayer[worldData.id] ?: ArrayList()
        val playerTickets = playerList.size * player
        return initial + tpsTickets + world + playerTickets
    }

    fun load(server: String? = null): CompletableFuture<ResultData> {
        val worldCacheConfig = WorldCache.getCacheConfig("world/${worldData.type}/unUse.yml")
        val lock = worldCacheConfig.getLong(worldData.id.toString())
        if (lock > System.currentTimeMillis()) {
            val future = CompletableFuture<ResultData>()
            future.complete(
                ResultData(
                    false,
                    lang.getString("world.inUnUse") ?: "世界正在冷却[就算是奇犽，也离不开充电，咕咕~]"
                )
            )
            return future
        }
        val event = PixelWorldProWorldLoadEvent(this)
        submit {
            Bukkit.getServer().pluginManager.callEvent(event)
        }
        if (event.isCancelled) {
            val future = CompletableFuture<ResultData>()
            future.complete(
                ResultData(
                    false,
                    lang.getString("world.isCancelled") ?: "世界被取消加载[糟了，世界被点燃了！]"
                )
            )
            return future
        }
        val future = worldDriver.load(this, server)
        future.thenApply {
            if (it.result) {
                val successEvent = PixelWorldProWorldLoadSuccessEvent(this)
                Bukkit.getServer().pluginManager.callEvent(successEvent)
            }
        }
        return future
    }

    fun unload(): CompletableFuture<ResultData> {
        return worldDriver.unload(this)
    }

    fun isLoad(): CompletableFuture<Boolean> {
        return worldDriver.isLoad(this)
    }

    fun teleport(player: Player): CompletableFuture<ResultData> {
        return worldDriver.teleport(player, this)
    }

    fun getWorlds(): HashMap<String, World> {
        val worldMap = HashMap<String, World>()
        val world = Bukkit.getWorld("PixelWorldPro/cache/world/${worldData.type}/${worldData.id}/world")
        if (world != null) {
            worldMap["world"] = world
        }
        return worldMap
    }

    fun setLevel(level: Int){
        val levelData = getDataConfig("level")
        val event = PixelWorldProLevelChangeEvent(this, levelData.getInt("level"), level)
        Bukkit.getServer().pluginManager.callEvent(event)
        if (!event.isCancelled) {
            levelData.set("level", level)
            levelData.saveToFile()
        }
    }

    fun getLevel(): Int {
        val levelData = getDataConfig("level")
        return levelData.getInt("level")
    }

    fun getMemberList(): ArrayList<OfflinePlayer> {
        val memberMap = worldData.player
        memberMap[worldData.owner] = "owner"
        val permissionPlayerMap = HashMap<Int, ArrayList<OfflinePlayer>>()
        val cachePermissionPlayerMap = HashMap<String, Int>()
        for (uuid in memberMap.keys) {
            val permission = worldData.permission[memberMap[uuid]] ?: continue
            if (permission["teleport"] == "false") {
                continue
            }
            when (memberMap[uuid]) {
                "owner" -> {
                    val list = permissionPlayerMap[0] ?: ArrayList()
                    list.add(Bukkit.getOfflinePlayer(uuid))
                    permissionPlayerMap[0] = list
                }

                "member" -> {
                    val list = permissionPlayerMap[1] ?: ArrayList()
                    list.add(Bukkit.getOfflinePlayer(uuid))
                    permissionPlayerMap[1] = list
                }

                else -> {
                    val id = cachePermissionPlayerMap[memberMap[uuid]] ?: (permissionPlayerMap.keys.last() + 1)
                    cachePermissionPlayerMap[memberMap[uuid]!!] = id
                    val list = permissionPlayerMap[id] ?: ArrayList()
                    list.add(Bukkit.getOfflinePlayer(uuid))
                    permissionPlayerMap[id] = list
                }
            }
        }

        val playerList = ArrayList<OfflinePlayer>()
        for (key in permissionPlayerMap.keys) {
            for (player in permissionPlayerMap[key]!!) {
                playerList.add(player)
            }
        }

        return playerList
    }

    //获取玩家权限组
    fun getPlayerGroup(player: UUID): String {
        return worldData.player[player] ?: "none"
    }
    //获取玩家权限组具体权限
    fun getPlayerPermission(player: UUID): HashMap<String, String>? {
        if (player == worldData.owner) {
            return worldData.permission["owner"]
        }
        return worldData.permission[getPlayerGroup(player)]
    }

    fun getBlackMemberList(): ArrayList<OfflinePlayer> {
        val memberMap = worldData.player
        val playerList = ArrayList<OfflinePlayer>()
        for (uuid in memberMap.keys) {
            val permission = worldData.permission[memberMap[uuid]] ?: continue
            if (permission["teleport"] != "false") {
                continue
            }
            playerList.add(Bukkit.getOfflinePlayer(uuid))
        }
        return playerList
    }

    fun delete(): CompletableFuture<ResultData>  {
        val future = CompletableFuture<ResultData>()
        Thread {
            try {
                if (isLoad().get()) {
                    unload().get()
                }
                val database = DataBase.getDataDriver(worldData.type)
                database.deleteWorldData(worldData)
                Thread.sleep(60 * 1000)
                File("PixelWorldPro/cache/world/${worldData.type}/${worldData.id}").delete()
                future.complete(
                    ResultData(
                        true,
                        ""
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                future.complete(
                    ResultData(
                    false,
                    e.toString()
                )
                )
            }
        }.start()
        return future
    }
}