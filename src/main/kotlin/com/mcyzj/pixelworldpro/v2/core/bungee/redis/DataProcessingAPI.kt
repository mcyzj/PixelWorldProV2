package com.mcyzj.pixelworldpro.v2.core.bungee.redis

import com.google.gson.JsonObject

interface DataProcessingAPI {
    fun receive (data: JsonObject)
}