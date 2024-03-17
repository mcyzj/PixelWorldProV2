@file:Suppress("SameParameterValue")
package com.mcyzj.pixelworldpro.v2.core.bungee.redis

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.mcyzj.pixelworldpro.v2.core.PixelWorldPro
import com.mcyzj.pixelworldpro.v2.core.bungee.BungeeWorld
import com.mcyzj.pixelworldpro.v2.core.bungee.BungeeWorldImpl
import com.mcyzj.pixelworldpro.v2.core.bungee.ResponseData
import com.xbaimiao.easylib.module.utils.submit
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
            log.info(channel + message.toString(), true)
            if (channel != PixelWorldPro.redisConfig.channel) {
                return@Thread
            }
            val g = Gson()
            val back: JsonObject = g.fromJson(message, JsonObject::class.java)
            if (back["server"].asString != "all") {
                if (back["server"].asString != BungeeWorldImpl.getBungeeData().server) {
                    return@Thread
                }
            }
            receive(back["plugin"].asString, back)
        }.start()
    }

    fun send(player: Player?, server: String? = "all", type: String, msg: JSONObject) {
        msg["sendServer"] = BungeeWorldImpl.getBungeeData().server
        msg["server"] = server
        msg["plugin"] = type
        push(msg.toString())
    }

    private fun push(message: String) {
        jedisPool.resource.use { jedis -> jedis.publish(PixelWorldPro.redisConfig.channel, message) }
    }

    private fun receive(type: String, msg: JsonObject) {
        try {
            log.info(type + msg.toString(), true)
            val listen = listener[type] ?: return
            submit {
                listen.receive(msg)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun connect(player: Player, server: String) {
        val byteArray = ByteArrayOutputStream()
        val out = DataOutputStream(byteArray)
        try {
            out.writeUTF("Connect")
            out.writeUTF(server)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        player.sendPluginMessage(PixelWorldPro.instance, "BungeeCord", byteArray.toByteArray())
    }

    fun setResponse(response: ResponseData, localData: JsonObject) {
        val data = JSONObject()
        data["id"] = localData["response"].asInt
        data["result"] = response.result
        data["data"] = response.data
        val server = localData["sendServer"].asString
        send(null, server, "Response", data)
    }
}