package com.mcyzj.pixelworldpro.v2.core.bungee

import com.mcyzj.pixelworldpro.v2.core.bungee.redis.Communicate
import org.json.simple.JSONObject
import java.util.*
import java.util.concurrent.CompletableFuture

object BungeeWorldImpl {

    fun createWorld(owner: UUID, template: String?, seed: Long?):CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        Thread {
            val server = BungeeServer.getServerData(owner)
            if (server == null) {
                future.complete(false)
                return@Thread
            }
            val response = BungeeServer.getResponseId()
            val data = JSONObject()
            data["type"] = "WorldCreate"
            data["owner"] = owner
            data["template"] = template
            data["seed"] = seed
            data["response"] = response
            Communicate.send(server.server, "local", data)
            future.complete(BungeeServer.getServerResponse(response, 120).get().result)
            return@Thread
        }.start()
        return future
    }
}