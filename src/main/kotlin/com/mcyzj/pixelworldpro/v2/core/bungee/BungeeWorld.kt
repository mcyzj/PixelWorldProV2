package com.mcyzj.pixelworldpro.v2.core.bungee

import com.mcyzj.pixelworldpro.v2.core.bungee.BungeeServer.getResponseId
import com.mcyzj.pixelworldpro.v2.core.bungee.BungeeServer.getServerData
import com.mcyzj.pixelworldpro.v2.core.bungee.BungeeServer.getServerResponse
import com.mcyzj.pixelworldpro.v2.core.bungee.redis.Communicate
import com.mcyzj.pixelworldpro.v2.core.permission.dataclass.ResultData
import com.mcyzj.pixelworldpro.v2.core.util.Config
import com.mcyzj.pixelworldpro.v2.core.world.PixelWorldProWorld
import com.mcyzj.pixelworldpro.v2.core.world.PixelWorldProWorldAPI
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.json.simple.JSONObject
import java.util.concurrent.CompletableFuture

class BungeeWorld : PixelWorldProWorldAPI {
    private val lang = Config.getLang()

    private fun isLock(world: PixelWorldProWorld): Boolean {
        val bungeeData = world.getDataConfig("bungee")
        return bungeeData.getBoolean("lock")
    }

    private fun setLock(world: PixelWorldProWorld, value: Boolean) {
        val bungeeData = world.getDataConfig("bungee")
        bungeeData.set("lock", value)
        bungeeData.saveToFile()
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
            if (server == BungeeServer.getLocalServer().server) {
                val worlds = Bukkit.getWorld("PixelWorldPro/cache/world/${world.worldData.type}/${world.worldData.id}/world")
                future.complete(worlds != null)
                return@Thread
            }
            val checkId = getResponseId()
            val data = JSONObject()
            data["type"] = "WorldCheck"
            data["id"] = world.worldData.id
            data["response"] = checkId
            Communicate.send(server, "local", data)
            val result = getServerResponse(checkId).get()
            if (!result.result) {
                bungeeData.set("load.server", null)
                bungeeData.saveToFile()
            }
            future.complete(result.result)
        }.start()
        return future
    }

    override fun teleport(player: Player, world: PixelWorldProWorld): CompletableFuture<ResultData> {
        val future = CompletableFuture<ResultData>()
        Thread {
            if (!world.isLoad().get()) {
                val value = world.load().get()
                if (!value.result) {
                    future.complete(value)
                    return@Thread
                }
            }
            val bungeeData = world.getDataConfig("bungee")
            val server = bungeeData.getString("load.server") ?: return@Thread
            val data = JSONObject()
            data["type"] = "WorldTeleport"
            data["id"] = world.worldData.id
            data["player"] = player.uniqueId
            Communicate.send(server, "local", data)
            future.complete(ResultData(true))
            Communicate.connect(player, server)
        }.start()
        return future
    }

    override fun load(world: PixelWorldProWorld, serverName: String?): CompletableFuture<ResultData> {
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
                val server = if (serverName != null) {
                    getServerData(serverName)
                } else {
                    getServerData(world.worldData.owner)
                }
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
                Communicate.send(server.server, "local", data)
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
            val data = JSONObject()
            data["type"] = "WorldUnload"
            data["id"] = world.worldData.id
            val response = getResponseId()
            data["response"] = response
            val bungeeData = world.getDataConfig("bungee")
            val server = bungeeData.getString("load.server")!!
            Communicate.send(server, "local", data)
            val result = getServerResponse(response, 20).get()
            if (result.result) {

            }
            future.complete(ResultData(
                getServerResponse(response, 20).get().result,
                ""
            ))
        }.start()
        return future
    }
}