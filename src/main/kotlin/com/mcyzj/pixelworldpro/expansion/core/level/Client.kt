package com.mcyzj.pixelworldpro.expansion.core.level

import com.google.gson.JsonObject
import com.mcyzj.pixelworldpro.PixelWorldPro
import com.mcyzj.pixelworldpro.api.interfaces.event.bungee.Client
import com.mcyzj.pixelworldpro.expansion.listener.trigger.level.Level

object Client : Client {
    override fun listen(data: JsonObject) {
        val worldData = PixelWorldPro.databaseApi.getWorldData(data["id"].asInt) ?: return
        Level.levelChange(worldData, data["level"].asInt)
    }
}