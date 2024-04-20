package com.mcyzj.pixelworldpro.v2.core.bungee.redis

import com.google.gson.JsonObject
import com.mcyzj.lib.bukkit.submit
import com.mcyzj.pixelworldpro.v2.core.PixelWorldPro
import com.mcyzj.pixelworldpro.v2.core.api.PixelWorldProApi
import com.mcyzj.pixelworldpro.v2.core.bungee.BungeeServer
import com.mcyzj.pixelworldpro.v2.core.bungee.ResponseData
import com.mcyzj.pixelworldpro.v2.core.world.WorldImpl
import org.bukkit.Bukkit
import java.util.*

object DataProcessing : DataProcessingAPI {
    val log = PixelWorldPro.instance.log
    override fun receive(data: JsonObject) {
        when (data["type"].asString) {
            "ServerCheck" -> {
                Communicate.setResponse(ResponseData(true, JsonObject()), data)
                return
            }
            "Response" -> {
                BungeeServer.setServerResponse(data)
                return
            }
            "WorldCreate" -> {
                val owner = UUID.fromString(data["owner"].asString)
                val template = try {
                    data["template"].asString
                } catch (_:Exception) {
                    null
                }
                val seed = try {
                    data["seed"].asLong
                } catch (_:Exception) {
                    null
                }
                WorldImpl.createWorldLocal(owner, template, seed, false, data["worldType"].asString).thenApply {
                    Communicate.setResponse(ResponseData(it, JsonObject()), data)
                }
                return
            }
            "WorldCheck" -> {
                val id = data["id"].asInt
                PixelWorldProApi().getWorld(id, false)!!.isLoad().thenApply {
                    Communicate.setResponse(ResponseData(it, JsonObject()), data)
                }
                return
            }
            "WorldLoad" -> {
                val id = data["id"].asInt
                PixelWorldProApi().getWorld(id, false)!!.load().thenApply {
                    Communicate.setResponse(ResponseData(it.result, JsonObject()), data)
                }
                return
            }
            "WorldUnload" -> {
                val id = data["id"].asInt
                PixelWorldProApi().getWorld(id, false)!!.unload().thenApply {
                    Communicate.setResponse(ResponseData(it.result, JsonObject()), data)
                }
                return
            }
            "WorldTeleport" -> {
                val id = data["id"].asInt
                val uuid = UUID.fromString(data["player"].asString)
                val world = PixelWorldProApi().getWorld(id, false)!!
                Thread {
                    var times = 0
                    while (times < 1000) {
                        val player = Bukkit.getPlayer(uuid)
                        if (player != null) {
                            world.teleport(player)
                            return@Thread
                        }
                        Thread.sleep(500)
                        times += 1
                    }
                }.start()
                return
            }
        }
    }
}