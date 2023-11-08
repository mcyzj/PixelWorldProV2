package com.mcyzj.pixelworldpro.bungee.database

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.mcyzj.pixelworldpro.PixelWorldPro
import com.mcyzj.pixelworldpro.api.interfaces.WorldAPI
import com.mcyzj.pixelworldpro.bungee.Server
import com.mcyzj.pixelworldpro.config.Config
import com.mcyzj.pixelworldpro.server.Icon
import org.bukkit.OfflinePlayer
import org.json.simple.JSONObject
import java.io.InputStream
import java.lang.Thread.sleep
import java.net.Socket
import java.util.*


object SocketClient {
    private lateinit var client : Socket
    private var bungee = Config.bungee
    private val logger = PixelWorldPro.instance.logger
    fun createClient(){
        try {
            //拉起socket连接
            val socket = Socket(bungee.getString("Database.Host"), bungee.getInt("Database.Port"))
            //password密钥握手
            //拉取密钥
            val token = bungee.getString("Database.Password")
            //拉取本地信息
            val server = Server.getLocalServer()
            //构建json
            val json = JSONObject()
            json["type"] = "LoginIn"
            json["token"] = token!!
            json["realName"] = server.realName
            //发送信息
            socket.getOutputStream().write(json.toString().toByteArray(charset("UTF-8")))
            //接受返回
            val inputStream = socket.getInputStream()
            val bytes = ByteArray(1024)
            var len: Int
            var info: String
            while (inputStream.read(bytes).also { len = it } != -1) {
                //注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
                info = String(bytes, 0, len, charset("UTF-8"))
                val g = Gson()
                val data = g.fromJson(info, JsonObject::class.java)
                val type = data.get("type").asString
                if (type == "tokenError") {
                    Icon.warning()
                    logger.warning("PixelWorldPro-Database连接失败：密钥错误")
                    socket.close()
                    return
                }
                if (type == "pass") {
                    logger.info("PixelWorldPro-Database连接成功")
                    client = socket
                    keepLive()
                    listen(inputStream)
                    return
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Icon.warning()
        logger.warning("PixelWorldPro-Database连接失败，等待一分钟后尝试重连")
        sleep(60000)
        createClient()
    }
    private fun keepLive(){
        Thread{
            var clientBreak = false
            while (!clientBreak) {
                try {
                    //拉取本地信息
                    val server = Server.getLocalServer()
                    val json = JSONObject()
                    json["type"] = "KeepLive"
                    json["realName"] = server.realName
                    client.getOutputStream().write(json.toString().toByteArray(charset("UTF-8")))
                    client.getOutputStream().flush()
                    sleep(1000)
                } catch (e: Exception) {
                    e.printStackTrace()
                    clientBreak = true
                }
            }
            Icon.warning()
            logger.warning("PixelWorldPro-Database断开连接，等待一分钟后尝试重连")
            sleep(60000)
            createClient()
        }.start()
    }

    //以下为监听模块
    private fun listen(inputStream: InputStream){
        Thread {
            try {
                val bytes = ByteArray(1024)
                var len: Int
                var info: String
                while (inputStream.read(bytes).also { len = it } != -1) {
                    //注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
                    info = String(bytes, 0, len, charset("UTF-8"))
                    logger.info(info)
                    val g = Gson()
                    val data = g.fromJson(info, JsonObject::class.java)
                    when (data.get("type").asString) {
                        "CreateWorld" -> {
                            val worldApi = WorldAPI.Factory.get()
                            worldApi.createWorld(
                                UUID.fromString(data["player"].asJsonObject["uuid"].asString),
                                data["template"].asString
                            )
                        }

                        "LoadWorld" -> {
                            val worldApi = WorldAPI.Factory.get()
                            worldApi.loadWorld(data["id"].asInt)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    //以下为通讯模块
    fun createWorld(player: OfflinePlayer, template: String, server: String){
        //拉取本地信息
        val local = Server.getLocalServer()
        //构建json
        //构建玩家json
        val playerJson = JSONObject()
        playerJson["name"] = player.name
        playerJson["uuid"] = player.uniqueId.toString()
        //构建create数据
        val json = JSONObject()
        json["type"] = "CreateWorld"
        json["realName"] = local.realName
        json["player"] = playerJson
        json["template"] = template
        json["to"] = server
        //发送数据
        client.getOutputStream().write(json.toString().toByteArray(charset("UTF-8")))
        client.getOutputStream().flush()
    }

    fun loadWorld(world: Int, server: String){
        //拉取本地信息
        val local = Server.getLocalServer()
        //构建json
        val json = JSONObject()
        json["type"] = "LoadWorld"
        json["realName"] = local.realName
        json["id"] = world
        json["to"] = server
        //发送数据
        client.getOutputStream().write(json.toString().toByteArray(charset("UTF-8")))
        client.getOutputStream().flush()
    }
}