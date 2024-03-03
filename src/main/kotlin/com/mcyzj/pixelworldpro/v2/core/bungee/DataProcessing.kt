package com.mcyzj.pixelworldpro.v2.core.bungee

import com.google.gson.JsonObject
import com.mcyzj.lib.plugin.file.BuiltOutConfiguration
import com.mcyzj.pixelworldpro.v2.core.PixelWorldPro
import com.mcyzj.pixelworldpro.v2.core.api.PixelWorldProApi
import com.mcyzj.pixelworldpro.v2.core.world.LocalWorld
import com.xbaimiao.easylib.module.utils.submit
import org.bukkit.Bukkit
import org.bukkit.Location
import java.util.*

object DataProcessing : DataProcessingAPI {
    val log = PixelWorldPro.instance.log
    override fun receive(data: JsonObject) {
        log.info(data.toString(), true)
        when (data["type"].asString) {
            "UpdateServer" -> {
                val id = data["id"].asInt
                val config = BuiltOutConfiguration("./PixelWorldPro/cache/bungee/server/$id.yml")
                config.set("return", true)
                config.saveToFile()
                BungeeWorld.saveServerData()
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
                LocalWorld.createWorldLocal(owner, template, seed)
                return
            }
            "WorldLoad" -> {
                val id = data["id"].asInt
                PixelWorldProApi().getWorld(id)!!.load()
                return
            }
            "WorldUnload" -> {
                val id = data["id"].asInt
                PixelWorldProApi().getWorld(id)!!.unload()
                return
            }
            "WorldTeleport" -> {
                val id = data["id"].asInt
                val uuid = UUID.fromString(data["player"].asString)
                val world = PixelWorldProApi().getWorld(id)!!
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