package com.mcyzj.pixelworldpro.v2.core.bungee

import com.google.common.collect.Iterables
import com.google.common.io.ByteStreams
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.mcyzj.pixelworldpro.v2.core.PixelWorldPro
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import org.json.simple.JSONObject
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream


object Communicate: PluginMessageListener {
    val listener = HashMap<String, DataProcessingAPI>()
    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray?) {
        if (channel != "BungeeCord") {
            return
        }
        val receive = ByteStreams.newDataInput(message)
        val msgChannel = receive.readUTF()
        if (msgChannel != "PixelWorldProV2") {
            return
        }
        val type = receive.readUTF()
        val len = receive.readShort()
        val msgBytes = ByteArray(len.toInt())
        receive.readFully(msgBytes)

        val msgin = DataInputStream(ByteArrayInputStream(msgBytes))
        val msg = msgin.readUTF() // 以与写入数据相同的顺序读取数据
        val g = Gson()
        val back: JsonObject = g.fromJson(msg, JsonObject::class.java)
        receive(type, back)
    }

    fun send(player: Player?, type: String, msg: JSONObject) {
        val out = ByteStreams.newDataOutput()
        out.writeUTF("Forward") // 这样写BungeeCord就知道要转发它

        out.writeUTF("ALL")
        out.writeUTF("PixelWorldProV2")
        out.writeUTF(type)


        val msgbytes = ByteArrayOutputStream()
        val msgout = DataOutputStream(msgbytes)
        msgout.writeUTF(msg.toString()) // 你可以用msgout发送任何你想发送的数据

        out.writeShort(msgbytes.toByteArray().size)
        out.write(msgbytes.toByteArray())
        if (player == null) {
            Iterables.getFirst(Bukkit.getOnlinePlayers(), null)
                ?.sendPluginMessage(PixelWorldPro.instance, "BungeeCord", out.toByteArray())
        } else {
            player.sendPluginMessage(PixelWorldPro.instance, "BungeeCord", out.toByteArray())
        }
    }

    private fun receive(type: String, msg: JsonObject) {
        val listen = listener[type] ?: return
        listen.receive(msg)
    }
}