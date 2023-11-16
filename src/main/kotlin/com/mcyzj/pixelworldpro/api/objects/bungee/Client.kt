package com.mcyzj.pixelworldpro.api.objects.bungee

import org.json.simple.JSONObject
import com.mcyzj.pixelworldpro.expansion.`object`.bungee.Client as ObjectClient

object Client {
    fun sengInformation(data: JSONObject){
        ObjectClient.sengInformation(data)
    }
}