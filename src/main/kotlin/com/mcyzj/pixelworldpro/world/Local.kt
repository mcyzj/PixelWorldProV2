package com.mcyzj.pixelworldpro.world

import com.mcyzj.pixelworldpro.PixelWorldPro
import com.mcyzj.pixelworldpro.api.interfaces.core.world.WorldAPI
import com.mcyzj.pixelworldpro.bungee.Server
import com.mcyzj.pixelworldpro.bungee.System
import com.mcyzj.pixelworldpro.bungee.database.SocketClient.tpWorld
import com.mcyzj.pixelworldpro.file.Config
import com.mcyzj.pixelworldpro.server.World
import com.mcyzj.pixelworldpro.server.World.localWorld
import com.xbaimiao.easylib.bridge.economy.PlayerPoints
import com.xbaimiao.easylib.bridge.economy.Vault
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.io.File
import java.lang.Thread.sleep
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.collections.ArrayList

object Local {
    private val logger = PixelWorldPro.instance.logger
    private var config = Config.config
    private val lang = PixelWorldPro.instance.lang
    private val database = PixelWorldPro.databaseApi
    private var file = Config.file
    private var worldConfig = Config.world
    private var bungee = Config.bungee
    fun adminCreateWorld(owner: UUID, template: String?): CompletableFuture<Boolean>{
        if (bungee.getBoolean("Enable")){
            return com.mcyzj.pixelworldpro.bungee.World.adminCreateWorld(owner, template)
        } else {
            val future = CompletableFuture<Boolean>()
            val temp = if (template == null) {
                val templatePath = file.getString("Template.Path")
                if (templatePath == null) {
                    logger.warning("§aPixelWorldPro ${lang.getString("worldConfig.warning.template.pathNotSet")}")
                    future.complete(false)
                    return future
                } else {
                    val templateFile = File(templatePath)
                    val templateList = templateFile.list()!!
                    templateList[Random().nextInt(templateList.size)]
                }
            } else {
                template
            }
            val worldApi = WorldAPI.Factory.get()
            return worldApi.createWorld(owner, temp)
        }
    }
    fun adminLoadWorld(owner: UUID): CompletableFuture<Boolean>{
        return if (bungee.getBoolean("Enable")){
            com.mcyzj.pixelworldpro.bungee.World.loadWorld(owner)
        } else {
            val worldApi = WorldAPI.Factory.get()
            worldApi.loadWorld(owner)
        }
    }
    fun adminLoadWorld(id: Int): CompletableFuture<Boolean>{
        return if (bungee.getBoolean("Enable")){
            com.mcyzj.pixelworldpro.bungee.World.loadWorld(id)
        } else {
            val worldApi = WorldAPI.Factory.get()
            worldApi.loadWorld(id)
        }
    }
    fun adminUnloadWorld(owner: UUID): CompletableFuture<Boolean>{
        return if (bungee.getBoolean("Enable")){
            com.mcyzj.pixelworldpro.bungee.World.unloadWorld(owner)
        } else {
            val worldApi = WorldAPI.Factory.get()
            worldApi.unloadWorld(owner)
        }
    }
    fun adminUnloadWorld(id: Int): CompletableFuture<Boolean>{
        return if (bungee.getBoolean("Enable")){
            com.mcyzj.pixelworldpro.bungee.World.unloadWorld(id)
        } else {
            val worldApi = WorldAPI.Factory.get()
            worldApi.unloadWorld(id)
        }
    }

    fun unloadAllWorld(){
        val keys = ArrayList<Int>()
        for (key in localWorld.keys){
            keys.add(key)
        }
        println(keys)
        for (key in keys){
            logger.info("§aPixelWorldPro 使用线程：${Thread.currentThread().name} 进行世界卸载操作")
            //拉取世界数据
            val worldData = database.getWorldData(key)
            if (worldData == null){
                logger.warning("§aPixelWorldPro $key ${lang.getString("worldConfig.warning.unload.noWorldData")}")
                return
            }
            System.removeWorldLock(worldData)
            //获取世界
            val world = localWorld[key]!!
            if (Bukkit.unloadWorld(world, true)){
                WorldAPI.Factory.get().zipWorld(worldData.world, worldData.world)
                localWorld.remove(key)
                World.removeLock(key)
                File(file.getString("World.Server"), worldData.world).deleteRecursively()
            }
        }
    }

    fun createWorld(owner: UUID, template: String?){
        if (database.getWorldData(owner) != null){
            Bukkit.getPlayer(owner)?.sendMessage(lang.getString("worldConfig.warning.create.created")?:"无法创建世界：对象已经有一个世界了")
            return
        }
        val player = Bukkit.getPlayer(owner)!!
        if (!checkCreateMoney(owner)){
            player.sendMessage(lang.getString("worldConfig.warning.create.notEnough")?: "无法创建世界：所需的资源不足")
        }
        val temp = if (template == null){
            val templatePath = file.getString("Template.Path")
            if (templatePath == null){
                logger.warning("§aPixelWorldPro ${lang.getString("worldConfig.warning.template.pathNotSet")}")
                return
            } else {
                val templateFile = File(templatePath)
                val templateList = templateFile.list()!!
                templateList[Random().nextInt(templateList.size)]
            }
        }else{
            template
        }
        val worldApi = WorldAPI.Factory.get()
        val future = worldApi.createWorld(owner, temp)
        future.thenApply {
            if (!takeCreateMoney(owner)){
                player.sendMessage(lang.getString("worldConfig.warning.create.notEnough")?: "无法创建世界：所需的资源不足")
            }
        }
    }

    private fun checkCreateMoney(user: UUID):Boolean{
        val player = Bukkit.getPlayer(user) ?: return false
        val useList = worldConfig.getConfigurationSection("Create.Use")!!.getKeys(false)
        useList.remove("Default")
        if (useList.isNotEmpty()){
            for (use in useList){
                val permission = worldConfig.getString("Create.Use.$use.Permission")!!
                if (!player.hasPermission(permission)){
                    return false
                }
                if (worldConfig.getDouble("Create.Use.$use.Money") > 0.0) {
                    if (!Vault().has(player, worldConfig.getDouble("Create.Use.$use.Money"))) {
                        return false
                    }
                }
                if (worldConfig.getDouble("Create.Use.$use.Point") > 0.0) {
                    if (!PlayerPoints().has(player, worldConfig.getDouble("Create.Use.$use.Point"))) {
                        return false
                    }
                }
                return true
            }
        }
        val permission = worldConfig.getString("Create.Use.Default.Permission")!!
        if (!player.hasPermission(permission)){
            return false
        }
        if (worldConfig.getDouble("Create.Use.Default.Money") > 0.0) {
            if (!Vault().has(player, worldConfig.getDouble("Create.Use.Default.Money"))) {
                return false
            }
        }
        if (worldConfig.getDouble("Create.Use.Default.Point") > 0.0) {
            if (!PlayerPoints().has(player, worldConfig.getDouble("Create.Use.Default.Point"))) {
                return false
            }
        }
        return true
    }

    private fun takeCreateMoney(user: UUID):Boolean{
        val player = Bukkit.getPlayer(user) ?: return false
        val useList = worldConfig.getConfigurationSection("Create.Use")!!.getKeys(false)
        useList.remove("Default")
        if (useList.isNotEmpty()){
            for (use in useList){
                val permission = worldConfig.getString("Create.Use.$use.Permission")!!
                if (!player.hasPermission(permission)){
                    return false
                }
                if (worldConfig.getDouble("Create.Use.$use.Money") > 0.0) {
                    if (!Vault().has(player, worldConfig.getDouble("Create.Use.$use.Money"))) {
                        return false
                    }
                    Vault().take(player, worldConfig.getDouble("Create.Use.$use.Money"))
                }
                if (worldConfig.getDouble("Create.Use.$use.Point") > 0.0) {
                    if (!PlayerPoints().has(player, worldConfig.getDouble("Create.Use.$use.Point"))) {
                        return false
                    }
                    PlayerPoints().take(player, worldConfig.getDouble("Create.Use.$use.Point"))
                }
                return true
            }
        }
        val permission = worldConfig.getString("Create.Use.Default.Permission")!!
        if (!player.hasPermission(permission)){
            return false
        }
        if (worldConfig.getDouble("Create.Use.Default.Money") > 0.0) {
            if (!Vault().has(player, worldConfig.getDouble("Create.Use.Default.Money"))) {
                return false
            }
            Vault().take(player, worldConfig.getDouble("Create.Use.Default.Money"))
        }
        if (worldConfig.getDouble("Create.Use.Default.Point") > 0.0) {
            if (!PlayerPoints().has(player, worldConfig.getDouble("Create.Use.Default.Point"))) {
                return false
            }
            PlayerPoints().take(player, worldConfig.getDouble("Create.Use.Default.Point"))
        }
        return true
    }

    fun adminTpWorldId(player: Player, id: Int) {
        if (bungee.getBoolean("Enable")) {
            val worldData = database.getWorldData(id) ?: return
            val server = System.getWorldLock(worldData)
            if (server == null) {
                player.sendMessage(lang.getString("worldConfig.warning.tp.notLoad") ?: "无法传送至世界：世界未加载")
                return
            }
            try {
                Server.bungeeTp(player, server)
            }catch (_: Exception){
                logger.info(lang.getString("bungee.warning.messageError") ?: "发送bungee信息失败")
            }
            tpWorld(id, server, player)
        } else {
            val world = localWorld[id]
            if (world == null) {
                player.sendMessage(lang.getString("worldConfig.warning.tp.notLoad") ?: "无法传送至世界：世界未加载")
                return
            }
            val location = world.spawnLocation
            player.teleport(location)
        }
    }

    fun tpWorldId(player: Player, id: Int){
        if (bungee.getBoolean("Enable")) {
            val worldData = database.getWorldData(id) ?: return
            var server = System.getWorldLock(worldData)
            if (server == null) {
                adminLoadWorld(id).thenApply {
                    server = System.getWorldLock(worldData)
                    if (server != null) {
                        try {
                            Server.bungeeTp(player, server!!)
                        } catch (_: Exception) {
                            logger.info(lang.getString("bungee.warning.messageError") ?: "发送bungee信息失败")
                        }
                        tpWorld(id, server!!, player)
                    } else {
                        player.sendMessage(
                            lang.getString("worldConfig.warning.tp.notLoad") ?: "无法传送至世界：世界未加载"
                        )
                        return@thenApply
                    }
                }
            } else {
                try {
                    Server.bungeeTp(player, server!!)
                }catch (_: Exception){
                    logger.info(lang.getString("bungee.warning.messageError") ?: "发送bungee信息失败")
                }
                tpWorld(id, server!!, player)
            }
        } else {
            var world = localWorld[id]
            if (world == null) {
                adminLoadWorld(id).thenApply {
                    world = localWorld[id] ?: return@thenApply
                    val location = world!!.spawnLocation
                    player.teleport(location)
                }
            }
            world = localWorld[id] ?: return
            val location = world!!.spawnLocation
            player.teleport(location)
        }
    }

    private fun backupAllWorld(){
        if (localWorld.isNotEmpty()) {
            for (id in localWorld.keys) {
                WorldAPI.Factory.get().backupWorld(id, true)
            }
        }
    }

    fun regularBackup(){
        if (file.getInt("Backup.time") <= 0){
            return
        }
        Thread{
            while (true) {
                sleep((file.getInt("Backup.time") * 1000).toLong())
                backupAllWorld()
            }
        }.start()
    }

    fun getUnzipWorld(){
        val lock = World.getLock()?:return
        val world = WorldAPI.Factory.get()
        for (id in lock){
            world.backupWorld(id, true)
            //拉取世界数据
            val worldData = database.getWorldData(id)
            if (worldData == null){
                logger.warning("§aPixelWorldPro $id ${lang.getString("world.warning.unload.noWorldData")}")
                return
            }
            File(file.getString("World.Server"), worldData.world).deleteRecursively()
        }
    }

    fun getWorldNameUUID(worldName: String): UUID? {
        val realNamelist = worldName.split("/").size
        if (realNamelist < 2) {
            return null
        }
        val realName = worldName.split("/")[realNamelist - 2]
        val uuidString: String? = Regex(pattern = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-z]{12}")
            .find(realName)?.value
        return try{
            UUID.fromString(uuidString)
        }catch (_:Exception){
            null
        }
    }
}