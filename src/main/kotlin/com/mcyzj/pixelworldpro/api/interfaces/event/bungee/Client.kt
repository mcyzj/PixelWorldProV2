package com.mcyzj.pixelworldpro.api.interfaces.event.bungee

import com.google.gson.JsonObject

interface Client {
    fun listen(data: JsonObject)
}