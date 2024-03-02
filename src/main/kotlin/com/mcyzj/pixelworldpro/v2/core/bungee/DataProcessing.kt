package com.mcyzj.pixelworldpro.v2.core.bungee

import com.google.gson.JsonObject
import com.mcyzj.lib.plugin.file.BuiltOutConfiguration
import com.mcyzj.pixelworldpro.v2.core.PixelWorldPro
import com.mcyzj.pixelworldpro.v2.core.api.PixelWorldProApi
import com.mcyzj.pixelworldpro.v2.core.world.LocalWorld
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
            }
            "WorldLoad" -> {
                val id = data["id"].asInt
                PixelWorldProApi().getWorld(id)!!.load()
            }
        }
    }
}