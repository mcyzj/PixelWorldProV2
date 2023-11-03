package com.mcyzj.pixelworldpro.world

import com.mcyzj.pixelworldpro.PixelWorldPro
import com.mcyzj.pixelworldpro.api.interfaces.WorldAPI
import com.mcyzj.pixelworldpro.config.Config
import com.mcyzj.pixelworldpro.server.World.localWorld
import com.xbaimiao.easylib.bridge.economy.PlayerPoints
import com.xbaimiao.easylib.bridge.economy.Vault
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.io.File
import java.util.*
import java.util.concurrent.CompletableFuture

object Local {
    private val logger = PixelWorldPro.instance.logger
    private var config = Config.config
    private val lang = PixelWorldPro.instance.lang
    private val database = PixelWorldPro.databaseApi
    private var file = Config.file
    private var world = Config.world
    fun adminCreateWorld(owner: UUID, template: String?): CompletableFuture<Boolean>{
        val future = CompletableFuture<Boolean>()
        val temp = if (template == null){
            val templatePath = file.getString("Template.Path")
            if (templatePath == null){
                logger.warning("§aPixelWorldPro ${lang.getString("world.warning.template.pathNotSet")}")
                future.complete(false)
                return future
            } else {
                val templateFile = File(templatePath)
                val templateList = templateFile.list()!!
                templateList[Random().nextInt(templateList.size)]
            }
        }else{
            template
        }
        val worldApi = WorldAPI.Factory.get()
        return worldApi.createWorld(owner, temp)
    }
    fun adminLoadWorld(owner: UUID): CompletableFuture<Boolean>{
        val worldApi = WorldAPI.Factory.get()
        return worldApi.loadWorld(owner)
    }
    fun adminLoadWorld(id: Int): CompletableFuture<Boolean>{
        val worldApi = WorldAPI.Factory.get()
        return worldApi.loadWorld(id)
    }
    fun adminUnloadWorld(owner: UUID): CompletableFuture<Boolean>{
        val worldApi = WorldAPI.Factory.get()
        return worldApi.unloadWorld(owner)
    }
    fun adminUnloadWorld(id: Int): CompletableFuture<Boolean>{
        val worldApi = WorldAPI.Factory.get()
        return worldApi.unloadWorld(id)
    }

    fun createWorld(owner: UUID, template: String?){
        val player = Bukkit.getPlayer(owner)!!
        if (!checkCreateMoney(owner)){
            player.sendMessage(lang.getString("world.warning.create.notEnough")?: "无法创建世界：所需的资源不足")
        }
        val temp = if (template == null){
            val templatePath = file.getString("Template.Path")
            if (templatePath == null){
                logger.warning("§aPixelWorldPro ${lang.getString("world.warning.template.pathNotSet")}")
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
                player.sendMessage(lang.getString("world.warning.create.notEnough")?: "无法创建世界：所需的资源不足")
            }
        }
    }

    private fun checkCreateMoney(user: UUID):Boolean{
        val player = Bukkit.getPlayer(user) ?: return false
        val useList = world.getConfigurationSection("Create.Use")!!.getKeys(false)
        useList.remove("Default")
        if (useList.isNotEmpty()){
            for (use in useList){
                val permission = world.getString("Create.Use.$use.Permission")!!
                if (!player.hasPermission(permission)){
                    return false
                }
                if (world.getDouble("Create.Use.$use.Money") > 0.0) {
                    if (!Vault().has(player, world.getDouble("Create.Use.$use.Money"))) {
                        return false
                    }
                }
                if (world.getDouble("Create.Use.$use.Point") > 0.0) {
                    if (!PlayerPoints().has(player, world.getDouble("Create.Use.$use.Point"))) {
                        return false
                    }
                }
                return true
            }
        }
        val permission = world.getString("Create.Use.Default.Permission")!!
        if (!player.hasPermission(permission)){
            return false
        }
        if (world.getDouble("Create.Use.Default.Money") > 0.0) {
            if (!Vault().has(player, world.getDouble("Create.Use.Default.Money"))) {
                return false
            }
        }
        if (world.getDouble("Create.Use.Default.Point") > 0.0) {
            if (!PlayerPoints().has(player, world.getDouble("Create.Use.Default.Point"))) {
                return false
            }
        }
        return true
    }

    private fun takeCreateMoney(user: UUID):Boolean{
        val player = Bukkit.getPlayer(user) ?: return false
        val useList = world.getConfigurationSection("Create.Use")!!.getKeys(false)
        useList.remove("Default")
        if (useList.isNotEmpty()){
            for (use in useList){
                val permission = world.getString("Create.Use.$use.Permission")!!
                if (!player.hasPermission(permission)){
                    return false
                }
                if (world.getDouble("Create.Use.$use.Money") > 0.0) {
                    if (!Vault().has(player, world.getDouble("Create.Use.$use.Money"))) {
                        return false
                    }
                    Vault().take(player, world.getDouble("Create.Use.$use.Money"))
                }
                if (world.getDouble("Create.Use.$use.Point") > 0.0) {
                    if (!PlayerPoints().has(player, world.getDouble("Create.Use.$use.Point"))) {
                        return false
                    }
                    PlayerPoints().take(player, world.getDouble("Create.Use.$use.Point"))
                }
                return true
            }
        }
        val permission = world.getString("Create.Use.Default.Permission")!!
        if (!player.hasPermission(permission)){
            return false
        }
        if (world.getDouble("Create.Use.Default.Money") > 0.0) {
            if (!Vault().has(player, world.getDouble("Create.Use.Default.Money"))) {
                return false
            }
            Vault().take(player, world.getDouble("Create.Use.Default.Money"))
        }
        if (world.getDouble("Create.Use.Default.Point") > 0.0) {
            if (!PlayerPoints().has(player, world.getDouble("Create.Use.Default.Point"))) {
                return false
            }
            PlayerPoints().take(player, world.getDouble("Create.Use.Default.Point"))
        }
        return true
    }

    fun adminTpWorldId(player: Player, id: Int){
        val world = localWorld[id] ?:return
        val location = world.spawnLocation
        player.teleport(location)
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