package com.mcyzj.pixelworldpro.bungee.database

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.mcyzj.pixelworldpro.PixelWorldPro
import com.mcyzj.pixelworldpro.bungee.Server
import com.mcyzj.pixelworldpro.config.Config
import com.mcyzj.pixelworldpro.server.Icon
import org.json.simple.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.lang.Thread.sleep
import java.net.Socket


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
            json["token"] = token!!
            json["realName"] = server.realName
            //发送信息
            val output = socket.getOutputStream()
            val out = PrintWriter(output)
            out.write(json.toString())
            out.flush()
            socket.shutdownOutput()
            //关闭发送
            out.close()
            output.close()
            //接受返回
            val input = socket.getInputStream()
            val back = BufferedReader(InputStreamReader(input))
            var info: String? = null
            while (back.readLine().also { info = it } != null) {
                val g = Gson()
                val type = g.fromJson(info, JsonObject::class.java).get("type").asString
                if (type == "tokenError"){
                    Icon.warning()
                    logger.warning("PixelWorldPro-Database连接失败：密钥错误")
                    socket.close()
                    back.close()
                    input.close()
                    return
                }
                if (type == "pass"){
                    logger.warning("PixelWorldPro-Database连接成功")
                    back.close()
                    input.close()
                    client = socket
                    return
                }
            }
            socket.close()
            back.close()
            input.close()
        } catch (e: Exception) {
            if (PixelWorldPro.instance.debug) {
                e.printStackTrace()
            }
        }
        Icon.warning()
        logger.warning("PixelWorldPro-Database连接失败，等待一分钟后尝试重连")
        sleep(60000)
        createClient()
    }
}