package com.mcyzj.pixelworldpro.expansion.`object`.bungee

import com.mcyzj.pixelworldpro.bungee.Server
import com.mcyzj.pixelworldpro.bungee.database.SocketClient
import org.json.simple.JSONObject

object Client {
    fun sengInformation(data: JSONObject) {
        if (data["to"] == null) {
            data["to"] = "all"
        }
        val local = Server.getLocalServer()
        data["realName"] = local.realName
        val client = SocketClient.getClient()
        //发送数据
        client.getOutputStream().write(data.toString().toByteArray(charset("UTF-8")))
        client.getOutputStream().flush()
    }
}