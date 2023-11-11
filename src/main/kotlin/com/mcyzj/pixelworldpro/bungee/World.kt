package com.mcyzj.pixelworldpro.bungee

import com.mcyzj.pixelworldpro.PixelWorldPro
import com.mcyzj.pixelworldpro.bungee.database.SocketClient
import com.mcyzj.pixelworldpro.file.Config
import org.bukkit.Bukkit
import java.io.File
import java.util.*
import java.util.concurrent.CompletableFuture

object World {
    private val logger = PixelWorldPro.instance.logger
    private var file = Config.file
    private var lang = PixelWorldPro.instance.lang
    private var database = PixelWorldPro.databaseApi
    fun adminCreateWorld(owner: UUID, template: String?): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        val temp = if (template == null){
            val templatePath = file.getString("Template.Path")
            if (templatePath == null){
                logger.warning("§aPixelWorldPro ${lang.getString("worldConfig.warning.template.pathNotSet")}")
                future.complete(false)
            } else {
                val templateFile = File(templatePath)
                val templateList = templateFile.list()!!
                templateList[Random().nextInt(templateList.size)]
            }
        }else{
            template
        }
        val createServer = Server.getCreateServer()
        val loadServer = Server.getLoadServer()
        if (loadServer == null){
            logger.warning(lang.getString("bungee.warning.noLoadServer"))
            future.complete(false)
            return future
        }
        if (createServer != null){
            SocketClient.createWorld(Bukkit.getOfflinePlayer(owner), temp.toString(), createServer.realName)
        } else {
            SocketClient.createWorld(Bukkit.getOfflinePlayer(owner), temp.toString(), loadServer.realName)
        }
        return future
    }
    fun loadWorld(id: Int): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        //拉取世界数据
        val worldData = database.getWorldData(id)
        if (worldData == null) {
            logger.warning("§aPixelWorldPro $id ${lang.getString("world.warning.load.noWorldData")}")
            future.complete(false)
            return future
        }
        //拉取服务器
        val loadServer = Server.getLoadServer()
        if (loadServer == null){
            logger.warning(lang.getString("bungee.warning.noLoadServer"))
            future.complete(false)
            return future
        }
        //发送信息
        SocketClient.loadWorld(worldData.id, loadServer.realName)
        return future
    }

    fun loadWorld(owner: UUID): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        //拉取世界数据
        val worldData = database.getWorldData(owner)
        if (worldData == null) {
            logger.warning("§aPixelWorldPro $owner ${lang.getString("world.warning.load.noWorldData")}")
            future.complete(false)
            return future
        }
        //拉取服务器
        val loadServer = Server.getLoadServer()
        if (loadServer == null){
            logger.warning(lang.getString("bungee.warning.noLoadServer"))
            future.complete(false)
            return future
        }
        //发送信息
        SocketClient.loadWorld(worldData.id, loadServer.realName)
        return future
    }

    fun unloadWorld(id: Int): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        //拉取世界数据
        val worldData = database.getWorldData(id)
        if (worldData == null) {
            logger.warning("§aPixelWorldPro $id ${lang.getString("world.warning.unload.noWorldData")}")
            future.complete(false)
            return future
        }
        //拉取服务器
        val loadServer = System.getWorldLock(worldData)
        if (loadServer == null){
            future.complete(true)
            return future
        }
        //发送信息
        SocketClient.unloadWorld(worldData.id, loadServer)
        return future
    }

    fun unloadWorld(owner: UUID): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        logger.info("§aPixelWorldPro 使用线程：${Thread.currentThread().name} 进行世界加载操作")
        //拉取世界数据
        val worldData = database.getWorldData(owner)
        if (worldData == null) {
            logger.warning("§aPixelWorldPro $owner ${lang.getString("world.warning.unload.noWorldData")}")
            future.complete(false)
            return future
        }
        //拉取服务器
        val loadServer = System.getWorldLock(worldData)
        if (loadServer == null){
            future.complete(true)
            return future
        }
        //发送信息
        SocketClient.unloadWorld(worldData.id, loadServer)
        return future
    }
}