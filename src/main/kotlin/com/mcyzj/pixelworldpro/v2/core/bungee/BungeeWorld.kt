package com.mcyzj.pixelworldpro.v2.core.bungee

import com.google.gson.JsonObject
import com.mcyzj.lib.plugin.file.BuiltInConfiguration
import com.mcyzj.lib.plugin.file.BuiltOutConfiguration
import com.mcyzj.pixelworldpro.v2.core.PixelWorldPro
import com.mcyzj.pixelworldpro.v2.core.bungee.redis.Communicate
import com.mcyzj.pixelworldpro.v2.core.bungee.redis.DataProcessing
import com.mcyzj.pixelworldpro.v2.core.permission.dataclass.ResultData
import com.mcyzj.pixelworldpro.v2.core.util.Config
import com.mcyzj.pixelworldpro.v2.core.world.PixelWorldProWorld
import com.mcyzj.pixelworldpro.v2.core.world.PixelWorldProWorldAPI
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.json.simple.JSONObject
import java.io.File
import java.util.*
import java.util.concurrent.CompletableFuture

class BungeeWorld : PixelWorldProWorldAPI {
    private val lang = Config.getLang()
    private val publicBungeeConfig = getPublicBungeeConfig()
    private val log = PixelWorldPro.instance.log

    private fun getPublicBungeeConfig(): BuiltOutConfiguration {
        val config = BuiltOutConfiguration("./PixelWorldPro/bungee.yml")
        if (config.getInt("version") < 1) {
            val example = BuiltInConfiguration("example/bungee.yml")
            example.save(config.file)
        }
        return BuiltOutConfiguration("./PixelWorldPro/bungee.yml")
    }

    fun getResponseId(): Int {
        var time = 0
        var checkId = Random().nextInt(900000000) + 100000000
        while (true) {
            if (!File("./PixelWorldPro/cache/bungee/response/$checkId.yml").exists()) {
                break
            }
            time ++
            checkId = Random().nextInt(900000000) + 100000000
            if (time > 60) {
                return 0
            }
        }
        return checkId
    }

    fun getServerResponse(checkId: Int, maxTime:Int = 10): CompletableFuture<ResponseData> {
        val future = CompletableFuture<ResponseData>()
        Thread {
            if (checkId == 0) {
                future.complete(ResponseData(false, JsonObject()))
                return@Thread
            }
            BungeeWorldImpl.inResponse.add(checkId)
            var time = 0
            while (true) {
                val response = BungeeWorldImpl.response[checkId]
                if (response != null) {
                    File("./PixelWorldPro/cache/bungee/response/$checkId.yml").delete()
                    future.complete(response)
                    return@Thread
                }
                Thread.sleep(1000)
                time ++
                if (time > maxTime) {
                    File("./PixelWorldPro/cache/bungee/response/$checkId.yml").delete()
                    future.complete(ResponseData(false, JsonObject()))
                    BungeeWorldImpl.inResponse.remove(checkId)
                    return@Thread
                }
            }
        }.start()
        return future
    }

    private fun isLock(world: PixelWorldProWorld): Boolean {
        val bungeeData = world.getDataConfig("bungee")
        return bungeeData.getBoolean("lock")
    }

    private fun setLock(world: PixelWorldProWorld, value: Boolean) {
        val bungeeData = world.getDataConfig("bungee")
        bungeeData.set("lock", value)
        bungeeData.saveToFile()
    }

    fun getServer(owner: UUID) : BungeeData? {
        val player = Bukkit.getPlayer(owner)
        var group = "default"
        if (player != null) {
            val groupConfig = publicBungeeConfig.getConfigurationSection("group")!!
            for (key in groupConfig.getKeys(false)) {
                if (key == group) {
                    continue
                }
                val permission = groupConfig.getString("$key.permission") ?: "pixelworldpro.vip"
                if (player.hasPermission(permission)) {
                    group = key
                    break
                }
            }
        }
        var time = 0
        while (true) {
            val serverList = publicBungeeConfig.getStringList("group.$group.server")
            var serverData: BungeeData? = null
            log.info(serverList.toString(), true)
            for (server in serverList) {
                val bungeeData = BungeeWorldImpl.getServerData(server) ?: continue
                log.info(bungeeData.toString(), true)
                if (!bungeeData.load) {
                    continue
                }
                if (serverData != null) {
                    if (serverData.tickets < bungeeData.tickets) {
                        continue
                    }
                }
                serverData = bungeeData
            }
            log.info(serverData?.toString(), true)
            if (serverData != null) {
                if (checkServer(serverData.server).get()) {
                    return serverData
                }
            }
            time ++
            if (time > 10) {
                return null
            }
        }
    }

    override fun isLoad(world: PixelWorldProWorld): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        Thread {
            val bungeeData = world.getDataConfig("bungee")
            val server = bungeeData.getString("load.server")
            if (server == null) {
                future.complete(false)
                return@Thread
            }
            if (server == BungeeWorldImpl.getBungeeData().server) {
                val worlds = Bukkit.getWorld("PixelWorldPro/cache/world/${world.worldData.type}/${world.worldData.id}/world")
                future.complete(worlds != null)
                return@Thread
            }
            val checkId = getResponseId()
            val data = JSONObject()
            data["type"] = "WorldCheck"
            data["id"] = world.worldData.id
            data["checkId"] = checkId
            Communicate.send(null, server, "local", data)
            future.complete(getServerResponse(checkId).get().result)
        }.start()
        return future
    }

    override fun teleport(player: Player, world: PixelWorldProWorld) {
        Thread {
            if (!world.isLoad().get()) {
                val value = world.load().get()
                if (!value.result) {
                    return@Thread
                }
            }
            val bungeeData = world.getDataConfig("bungee")
            val server = bungeeData.getString("load.server") ?: return@Thread
            val data = JSONObject()
            data["type"] = "WorldTeleport"
            data["id"] = world.worldData.id
            data["player"] = player.uniqueId
            Communicate.send(null, server, "local", data)
            Communicate.connect(player, server)
        }.start()
    }

    private fun checkServer(server: String): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        Thread {
            val id = getResponseId()
            val data = JSONObject()
            data["type"] = "ServerCheck"
            data["response"] = id
            Communicate.send(null, server, "local", data)
            future.complete(getServerResponse(id).get().result)
        }.start()
        return future
    }

    override fun load(world: PixelWorldProWorld): CompletableFuture<ResultData> {
        val future = CompletableFuture<ResultData>()
        Thread {
            if (isLock(world)) {
                val result = ResultData(
                    false,
                    lang.getString("check.world.bungeeLock") ?: "世界被已Bungee保护锁定"
                )
                future.complete(result)
                return@Thread
            }
            setLock(world, true)
            try {
                if (isLoad(world).get()) {
                    val result = ResultData(
                        false,
                        lang.getString("check.world.alreadyLoad") ?: "世界被已加载，请不要重复加载"
                    )
                    future.complete(result)
                    setLock(world, false)
                    return@Thread
                }
                val server = getServer(world.worldData.owner)
                if (server == null) {
                    val result = ResultData(
                        false,
                        lang.getString("check.world.noServer") ?: "没有可供世界加载的服务器"
                    )
                    future.complete(result)
                    setLock(world, false)
                    return@Thread
                }
                val bungeeData = world.getDataConfig("bungee")
                bungeeData.set("load.server", server.server)
                bungeeData.saveToFile()
                val data = JSONObject()
                data["type"] = "WorldLoad"
                data["id"] = world.worldData.id
                val response = getResponseId()
                data["response"] = response
                Communicate.send(null, server.server, "local", data)
                future.complete(ResultData(
                    getServerResponse(response, 120).get().result,
                    ""
                ))
                setLock(world, false)
                return@Thread
            } catch (e: Exception) {
                e.printStackTrace()
            }
            setLock(world, false)
            future.complete(ResultData(
                false,
                "内部错误[internal error]"
            ))
        }.start()
        return future
    }

    override fun unload(world: PixelWorldProWorld): CompletableFuture<ResultData> {
        val future = CompletableFuture<ResultData>()
        Thread {
            if (!isLoad(world).get()) {
                val result = ResultData(
                    false,
                    lang.getString("check.world.notLoad") ?: "世界未加载"
                )
                future.complete(result)
            }
        }.start()
        return future
    }
}