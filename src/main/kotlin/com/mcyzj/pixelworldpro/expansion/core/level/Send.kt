package com.mcyzj.pixelworldpro.expansion.core.level

import com.mcyzj.pixelworldpro.expansion.`object`.bungee.Client
import org.json.simple.JSONObject

object Send {
    fun send(id: Int, level: Int){
        val data = JSONObject()
        data["type"] = "LevelChange"
        data["id"] = id
        data["level"] = level
        Client.sengInformation(data)
    }
}