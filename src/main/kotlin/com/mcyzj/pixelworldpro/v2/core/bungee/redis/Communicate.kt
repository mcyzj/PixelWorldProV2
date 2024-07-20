@file:Suppress("SameParameterValue")
package com.mcyzj.pixelworldpro.v2.core.bungee.redis

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.mcyzj.lib.bukkit.submit
import com.mcyzj.pixelworldpro.v2.Main
import com.mcyzj.pixelworldpro.v2.core.PixelWorldPro
import com.mcyzj.pixelworldpro.v2.core.bungee.BungeeServer
import com.mcyzj.pixelworldpro.v2.core.bungee.ResponseData
import org.bukkit.entity.Player
import org.json.simple.JSONObject
import redis.clients.jedis.JedisPubSub
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException


object Communicate : JedisPubSub() {
    private val jedisPool = PixelWorldPro.jedisPool
    val listener = HashMap<String, DataProcessingAPI>()
    val log = PixelWorldPro.instance.log
    override fun onMessage(channel: String?, message: String?) {
        Thread {
            if (channel != PixelWorldPro.redisConfig.channel) {
                return@Thread
            }
            log.info("监听到Redis消息：$message", true)
            val g = Gson()
            val back: JsonObject = g.fromJson(message, JsonObject::class.java)
            if (back["server"].asString != "all") {
                if (back["server"].asString != BungeeServer.getLocalServer().server) {
                    return@Thread
                }
            }
            receive(back["plugin"].asString, back)
        }.start()
    }

    fun send(server: String? = "all", type: String, msg: JSONObject) {
        val localServer = BungeeServer.getLocalServer()
        msg["sendServer"] = localServer.server
        msg["server"] = server
        msg["plugin"] = type
        if (server == localServer.server) {
            Thread {
                val g = Gson()
                val back: JsonObject = g.fromJson(msg.toJSONString(), JsonObject::class.java)
                receive(back["plugin"].asString, back)
            }.start()
            return
        }
        push(msg.toJSONString())
    }

    private fun push(message: String) {
        jedisPool.resource.use { jedis -> jedis.publish(PixelWorldPro.redisConfig.channel, message) }
    }

    private fun receive(type: String, msg: JsonObject) {
        try {
            val listen = listener[type] ?: return
            submit {
                listen.receive(msg)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun connect(player: Player, server: String) {
        if (server == BungeeServer.getLocalServer().server) {
            return
        }
        val byteArray = ByteArrayOutputStream()
        val out = DataOutputStream(byteArray)
        try {
            out.writeUTF("Connect")
            out.writeUTF(server)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        player.sendPluginMessage(Main.instance, "BungeeCord", byteArray.toByteArray())
    }

    fun setResponse(response: ResponseData, localData: JsonObject) {
        val data = JSONObject()
        data["type"] = "Response"
        data["id"] = localData["response"].asInt
        data["result"] = response.result
        data["data"] = response.data
        val server = localData["sendServer"].asString
        if (server == BungeeServer.getLocalServer().server) {
            BungeeServer.setServerResponse(Gson().fromJson(data.toJSONString(), JsonObject::class.java))
            return
        }
        log.info("发送${localData["response"].asInt}的回应信息")
        send(server, "local", data)
    }
}