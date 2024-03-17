package com.mcyzj.pixelworldpro.v2.core.bungee.redis

import com.google.gson.JsonObject
import com.mcyzj.lib.plugin.file.BuiltOutConfiguration
import com.mcyzj.pixelworldpro.v2.core.PixelWorldPro
import com.mcyzj.pixelworldpro.v2.core.api.PixelWorldProApi
import com.mcyzj.pixelworldpro.v2.core.bungee.BungeeWorld
import com.mcyzj.pixelworldpro.v2.core.bungee.BungeeWorldImpl
import com.mcyzj.pixelworldpro.v2.core.bungee.ResponseData
import com.mcyzj.pixelworldpro.v2.core.permission.dataclass.ResultData
import com.mcyzj.pixelworldpro.v2.core.world.WorldImpl
import com.xbaimiao.easylib.module.utils.submit
import org.bukkit.Bukkit
import java.util.*
import kotlin.collections.HashMap

object DataProcessing : DataProcessingAPI {
    val log = PixelWorldPro.instance.log
    override fun receive(data: JsonObject) {
        log.info(data.toString(), true)
        when (data["type"].asString) {
            "UpdateServer" -> {
                val id = data["id"].asInt
                val config = BuiltOutConfiguration("./PixelWorldPro/cache/bungee/response/$id.yml")
                config.set("return", true)
                config.saveToFile()
                BungeeWorldImpl.saveServerData()
                return
            }
            "ServerCheck" -> {
                Communicate.setResponse(ResponseData(true, JsonObject()), data)
            }
            "Response" -> {
                val responseID = data["id"].asInt
                val result = data["result"].asBoolean
                val responseData = data["data"].asJsonObject
                if (responseID in BungeeWorldImpl.inResponse) {
                    BungeeWorldImpl.inResponse.remove(responseID)
                    BungeeWorldImpl.response[responseID] = ResponseData(
                        result,
                        responseData
                    )
                }
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
                WorldImpl.createWorldLocal(owner, template, seed, false).thenApply {
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
                            submit {
                                world.teleport(player)
                            }
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