package com.mcyzj.pixelworldpro.command

import com.mcyzj.pixelworldpro.PixelWorldPro
import com.mcyzj.pixelworldpro.api.interfaces.core.world.WorldAPI
import com.mcyzj.pixelworldpro.expansion.ExpansionManager.loadAllExpansion
import com.mcyzj.pixelworldpro.file.Config
import com.mcyzj.pixelworldpro.permission.PermissionImpl
import com.mcyzj.pixelworldpro.world.Local
import com.xbaimiao.easylib.module.command.CommandSpec
import com.xbaimiao.easylib.module.command.command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object Admin {
    private val logger = PixelWorldPro.instance.logger
    private val lang = PixelWorldPro.instance.lang
    private val database = PixelWorldPro.databaseApi
    private val commandMap = HashMap<String, CommandSpec<CommandSender>>()

    private val create = command<CommandSender>("create") {
        permission = "pwp.admin.create"
        exec{
            if (!sender.hasPermission("pwp.admin.create")){
                sender.sendMessage(lang.getString("command.warning.noPermission")?:"你没有权限执行这个命令")
                return@exec
            }
            when(args.size){
                0 -> {
                    if (database.getWorldData((sender as Player).uniqueId) != null){
                        sender.sendMessage(lang.getString("world.warning.create.created")?:"无法创建世界：对象已经有一个世界了")
                        return@exec
                    }
                    Local.adminLoadDimension((sender as Player).uniqueId, null)
                }
                1 -> {
                    if (database.getWorldData((sender as Player).uniqueId) != null){
                        sender.sendMessage(lang.getString("world.warning.create.created")?:"无法创建世界：对象已经有一个世界了")
                        return@exec
                    }
                    Local.adminLoadDimension((sender as Player).uniqueId, args[0])
                }
                2 -> {
                    val player = com.mcyzj.pixelworldpro.server.Player.getOfflinePlayer(args[0])
                    if (database.getWorldData(player.uniqueId) != null){
                        sender.sendMessage(lang.getString("world.warning.create.created")?:"无法创建世界：对象经有一个世界了")
                        return@exec
                    }
                    if (args[1] == "auto") {
                        Local.adminLoadDimension(player.uniqueId, null)
                    }else{
                        Local.adminLoadDimension(player.uniqueId, args[1])
                    }
                }
                else -> {
                    sender.sendMessage(lang.getString("command.warning.formatError")?:"命令格式错误")
                    sender.sendMessage(lang.getString("command.prompt.admin.create1")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create2")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create3")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create4")?:"???")
                }
            }
        }
    }

    private val loadPlayer = command<CommandSender>("player") {
        permission = "pwp.admin.load"
        exec{
            if (!sender.hasPermission("pwp.admin.load")){
                sender.sendMessage(lang.getString("command.warning.noPermission")?:"你没有权限执行这个命令")
                return@exec
            }
            when(args.size){
                0 -> {
                    if (database.getWorldData((sender as Player).uniqueId) == null){
                        sender.sendMessage(lang.getString("world.warning.unload.unloaded")?:"无法卸载世界：对象没有世界")
                        return@exec
                    }
                    Local.adminLoadWorld((sender as Player).uniqueId)
                }
                1 -> {
                    val player = com.mcyzj.pixelworldpro.server.Player.getOfflinePlayer(args[0])
                    if (database.getWorldData(player.uniqueId) == null){
                        sender.sendMessage(lang.getString("world.warning.unload.unloaded")?:"无法卸载世界：对象没有世界")
                        return@exec
                    }
                    Local.adminLoadWorld(player.uniqueId)
                }
                else -> {
                    sender.sendMessage(lang.getString("command.warning.formatError")?:"命令格式错误")
                    sender.sendMessage(lang.getString("command.prompt.admin.create1")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create2")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create3")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create4")?:"???")
                }
            }
        }
    }
    private val loadId = command<CommandSender>("id") {
        permission = "pwp.admin.load"
        exec{
            if (!sender.hasPermission("pwp.admin.load")){
                sender.sendMessage(lang.getString("command.warning.noPermission")?:"你没有权限执行这个命令")
                return@exec
            }
            when(args.size){
                0 -> {
                    if (database.getWorldData((sender as Player).uniqueId) == null){
                        sender.sendMessage(lang.getString("world.warning.unload.unloaded")?:"无法卸载世界：对象没有世界")
                        return@exec
                    }
                        Local.adminLoadWorld((sender as Player).uniqueId)
                }
                1 -> {
                    val id = try {
                        args[0].toInt()
                    }catch (_:Exception){
                        sender.sendMessage(lang.getString("world.warning.unload.notID")?:"无法卸载世界：输入值不是一个有效的数字id")
                        return@exec
                    }
                    if (database.getWorldData(id) == null){
                        sender.sendMessage(lang.getString("world.warning.unload.unloaded")?:"无法卸载世界：对象没有世界")
                        return@exec
                    }
                    Local.adminLoadWorld(id)
                }
                else -> {
                    sender.sendMessage(lang.getString("command.warning.formatError")?:"命令格式错误")
                    sender.sendMessage(lang.getString("command.prompt.admin.create1")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create2")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create3")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create4")?:"???")
                }
            }
        }
    }
    private val load = command<CommandSender>("load") {
        permission = "pwp.admin.load"
        sub(loadId)
        sub(loadPlayer)
    }

    private val unloadPlayer = command<CommandSender>("player") {
        permission = "pwp.admin.unload"
        exec{
            if (!sender.hasPermission("pwp.admin.unload")){
                sender.sendMessage(lang.getString("command.warning.noPermission")?:"你没有权限执行这个命令")
                return@exec
            }
            when(args.size){
                0 -> {
                    if (database.getWorldData((sender as Player).uniqueId) == null){
                        sender.sendMessage(lang.getString("world.warning.unload.unloaded")?:"无法卸载世界：对象没有世界")
                        return@exec
                    }
                    WorldAPI.Factory.get().unloadWorld((sender as Player).uniqueId)
                }
                1 -> {
                    val player = com.mcyzj.pixelworldpro.server.Player.getOfflinePlayer(args[0])
                    if (database.getWorldData(player.uniqueId) == null){
                        sender.sendMessage(lang.getString("world.warning.unload.unloaded")?:"无法卸载世界：对象没有世界")
                        return@exec
                    }
                    Local.adminUnloadWorld(player.uniqueId)
                }
                else -> {
                    sender.sendMessage(lang.getString("command.warning.formatError")?:"命令格式错误")
                    sender.sendMessage(lang.getString("command.prompt.admin.create1")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create2")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create3")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create4")?:"???")
                }
            }
        }
    }
    private val unloadId = command<CommandSender>("id") {
        permission = "pwp.admin.unload"
        exec{
            if (!sender.hasPermission("pwp.admin.unload")){
                sender.sendMessage(lang.getString("command.warning.noPermission")?:"你没有权限执行这个命令")
                return@exec
            }
            when(args.size){
                0 -> {
                    if (database.getWorldData((sender as Player).uniqueId) == null){
                        sender.sendMessage(lang.getString("world.warning.unload.unloaded")?:"无法卸载世界：对象没有世界")
                        return@exec
                    }
                    WorldAPI.Factory.get().unloadWorld((sender as Player).uniqueId)
                }
                1 -> {
                    val id = try {
                        args[0].toInt()
                    }catch (_:Exception){
                        sender.sendMessage(lang.getString("world.warning.unload.notID")?:"无法卸载世界：输入值不是一个有效的数字id")
                        return@exec
                    }
                    if (database.getWorldData(id) == null){
                        sender.sendMessage(lang.getString("world.warning.unload.unloaded")?:"无法卸载世界：对象没有世界")
                        return@exec
                    }
                    Local.adminUnloadWorld(id)
                }
                else -> {
                    sender.sendMessage(lang.getString("command.warning.formatError")?:"命令格式错误")
                    sender.sendMessage(lang.getString("command.prompt.admin.create1")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create2")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create3")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create4")?:"???")
                }
            }
        }
    }
    private val unload = command<CommandSender>("unload") {
        permission = "pwp.admin.unload"
        sub(unloadId)
        sub(unloadPlayer)
    }

    private val tpPlayer = command<CommandSender>("player") {
        permission = "pwp.admin.tp"
        exec{
            if (!sender.hasPermission("pwp.admin.tp")){
                sender.sendMessage(lang.getString("command.warning.noPermission")?:"你没有权限执行这个命令")
                return@exec
            }
            when(args.size){
                0 -> {
                    if (database.getWorldData((sender as Player).uniqueId) == null){
                        sender.sendMessage(lang.getString("world.warning.tp.noWorld")?:"无法传送至世界：对象没有世界")
                        return@exec
                    }
                    Local.adminTpWorldId(sender as Player, database.getWorldData((sender as Player).uniqueId)!!.id)
                }
                1 -> {
                    val player = com.mcyzj.pixelworldpro.server.Player.getOfflinePlayer(args[0])
                    if (database.getWorldData(player.uniqueId) == null){
                        sender.sendMessage(lang.getString("world.warning.tp.noWorld")?:"无法传送至世界：对象没有世界")
                        return@exec
                    }
                    Local.adminTpWorldId(sender as Player, database.getWorldData(player.uniqueId)!!.id)
                }
                2 -> {
                    val player = com.mcyzj.pixelworldpro.server.Player.getOfflinePlayer(args[1])
                    if (database.getWorldData(player.uniqueId) == null){
                        sender.sendMessage(lang.getString("world.warning.tp.noWorld")?:"无法传送至世界：对象没有世界")
                        return@exec
                    }
                    val player2 = com.mcyzj.pixelworldpro.server.Player.getOfflinePlayer(args[0])
                    Local.adminTpWorldId(player2 as Player, database.getWorldData(player.uniqueId)!!.id)
                }
                else -> {
                    sender.sendMessage(lang.getString("command.warning.formatError")?:"命令格式错误")
                    sender.sendMessage(lang.getString("command.prompt.admin.create1")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create2")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create3")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create4")?:"???")
                }
            }
        }
    }
    private val tpId = command<CommandSender>("id") {
        permission = "pwp.admin.tp"
        exec{
            if (!sender.hasPermission("pwp.admin.tp")){
                sender.sendMessage(lang.getString("command.warning.noPermission")?:"你没有权限执行这个命令")
                return@exec
            }
            when(args.size){
                0 -> {
                    if (database.getWorldData((sender as Player).uniqueId) == null){
                        sender.sendMessage(lang.getString("world.warning.tp.noWorld")?:"无法传送至世界：对象没有世界")
                        return@exec
                    }
                    Local.adminTpWorldId(sender as Player, database.getWorldData((sender as Player).uniqueId)!!.id)
                }
                1 -> {
                    val id = try {
                        args[0].toInt()
                    }catch (_:Exception){
                        sender.sendMessage(lang.getString("world.warning.tp.notID")?:"无法传送至世界：输入值不是一个有效的数字id")
                        return@exec
                    }
                    if (database.getWorldData(id) == null){
                        sender.sendMessage(lang.getString("world.warning.tp.noWorld")?:"无法传送至世界：对象没有世界")
                        return@exec
                    }
                    Local.adminTpWorldId(sender as Player, id)
                }
                2 -> {
                    val player = com.mcyzj.pixelworldpro.server.Player.getOfflinePlayer(args[1])
                    val id = try {
                        args[0].toInt()
                    }catch (_:Exception){
                        sender.sendMessage(lang.getString("world.warning.tp.notID")?:"无法传送至世界：输入值不是一个有效的数字id")
                        return@exec
                    }
                    if (database.getWorldData(id) == null){
                        sender.sendMessage(lang.getString("world.warning.tp.noWorld")?:"无法传送至世界：对象没有世界")
                        return@exec
                    }
                    if (player.isOnline){
                        Local.adminTpWorldId(player as Player, id)
                    }
                }
                else -> {
                    sender.sendMessage(lang.getString("command.warning.formatError")?:"命令格式错误")
                    sender.sendMessage(lang.getString("command.prompt.admin.create1")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create2")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create3")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create4")?:"???")
                }
            }
        }
    }
    private val tp = command<CommandSender>("tp") {
        permission = "pwp.admin.tp"
        sub(tpId)
        sub(tpPlayer)
    }

    private val expansionLoad = command<CommandSender>("load") {
        permission = "pwp.admin.expansion"
        exec{
            if (!sender.hasPermission("pwp.admin.expansion")){
                sender.sendMessage(lang.getString("command.warning.noPermission")?:"你没有权限执行这个命令")
                return@exec
            }
            when(args.size){
                1 -> {
                    if (args[0] == "all"){
                        loadAllExpansion()
                    }
                }
                else -> {
                    sender.sendMessage(lang.getString("command.warning.formatError")?:"命令格式错误")
                    sender.sendMessage(lang.getString("command.prompt.admin.create1")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create2")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create3")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create4")?:"???")
                }
            }
        }
    }

    private val expansion = command<CommandSender>("expansion") {
        permission = "pwp.admin.expansion"
        sub(expansionLoad)
    }

    private val groupUpdateAll = command<CommandSender>("all") {
        permission = "pwp.admin.group"
        exec{
            if (!sender.hasPermission("pwp.admin.group")){
                sender.sendMessage(lang.getString("command.warning.noPermission")?:"你没有权限执行这个命令")
                return@exec
            }
            val worldMap = database.getWorldIdMap()
            for (worldData in worldMap.values){
                worldData.permission = PermissionImpl.getConfigWorldPermission()
                database.setWorldData(worldData)
            }
        }
    }

    private val groupUpdate = command<CommandSender>("update") {
        permission = "pwp.admin.group"
        sub(groupUpdateAll)
    }

    private val group = command<CommandSender>("group") {
        permission = "pwp.admin.group"
        sub(groupUpdate)
    }

    private val nameSet = command<CommandSender>("set") {
        permission = "pwp.admin.name"
        exec{
            if (!sender.hasPermission("pwp.admin.name")){
                sender.sendMessage(lang.getString("command.warning.noPermission")?:"你没有权限执行这个命令")
                return@exec
            }
            if (sender !is Player){
                return@exec
            }
            val player = sender as Player
            Local.adminNameWorld(player.uniqueId, args[0], player)
        }
    }

    private val nameId = command<CommandSender>("player") {
        permission = "pwp.admin.name"
        exec{
            if (!sender.hasPermission("pwp.admin.name")){
                sender.sendMessage(lang.getString("command.warning.noPermission")?:"你没有权限执行这个命令")
                return@exec
            }
            when (args.size){
                2 -> {
                    val id = try {
                        args[0].toInt()
                    } catch (_:Exception) {
                        sender.sendMessage(lang.getString("world.warning.name.notId")?:"无法命名世界：输入值不是一个有效的数字id")
                        return@exec
                    }
                    Local.adminNameWorld(id, args[1], sender)
                }
            }
        }
    }

    private val namePlayer = command<CommandSender>("player") {
        permission = "pwp.admin.name"
        exec{
            if (!sender.hasPermission("pwp.admin.name")){
                sender.sendMessage(lang.getString("command.warning.noPermission")?:"你没有权限执行这个命令")
                return@exec
            }
            when (args.size){
                2 -> {
                    val player = com.mcyzj.pixelworldpro.server.Player.getOfflinePlayer(args[0])
                    Local.adminNameWorld(player.uniqueId, args[1], sender)
                }
            }
        }
    }

    private val name = command<CommandSender>("name") {
        permission = "pwp.admin.name"
        sub(nameId)
        sub(namePlayer)
        sub(nameSet)
    }

    private val reload = command<CommandSender>("reload") {
        permission = "pwp.admin.reload"
        exec {
            Config.reload()
        }
    }

    private val createDimensionPlayer = command<CommandSender>("player") {
        permission = "pwp.admin.dimension.create"
        exec{
            if (!sender.hasPermission("pwp.admin.dimension.create")){
                sender.sendMessage(lang.getString("command.warning.noPermission")?:"你没有权限执行这个命令")
                return@exec
            }
            when(args.size){
                1 -> {
                    Local.adminCreateDimension((sender as Player).uniqueId, args[0]).thenApply {
                        sender.sendMessage(it.reason)
                    }
                }
                2 -> {
                    val player = com.mcyzj.pixelworldpro.server.Player.getOfflinePlayer(args[0])
                    Local.adminCreateDimension(player.uniqueId, args[1]).thenApply {
                        sender.sendMessage(it.reason)
                    }
                }
                else -> {
                    sender.sendMessage(lang.getString("command.warning.formatError")?:"命令格式错误")
                    sender.sendMessage(lang.getString("command.prompt.admin.create1")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create2")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create3")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create4")?:"???")
                }
            }
        }
    }
    private val createDimensionId = command<CommandSender>("id") {
        permission = "pwp.admin.dimension.create"
        exec{
            if (!sender.hasPermission("pwp.admin.dimension.create")){
                sender.sendMessage(lang.getString("command.warning.noPermission")?:"你没有权限执行这个命令")
                return@exec
            }
            when(args.size){
                1 -> {
                    Local.adminCreateDimension((sender as Player).uniqueId, args[0]).thenApply {
                        sender.sendMessage(it.reason)
                    }
                }
                2 -> {
                    val id = try {
                        args[0].toInt()
                    }catch (_:Exception){
                        sender.sendMessage(lang.getString("world.warning.dimension.create.notID")?:"无法加载维度：输入值不是一个有效的数字id")
                        return@exec
                    }
                    Local.adminCreateDimension(id, args[1]).thenApply {
                        sender.sendMessage(it.reason)
                    }
                }
                else -> {
                    sender.sendMessage(lang.getString("command.warning.formatError")?:"命令格式错误")
                    sender.sendMessage(lang.getString("command.prompt.admin.create1")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create2")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create3")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create4")?:"???")
                }
            }
        }
    }
    private val createDimension = command<CommandSender>("create") {
        permission = "pwp.admin.dimension.create"
        sub(createDimensionId)
        sub(createDimensionPlayer)
    }


    private val loadDimensionPlayer = command<CommandSender>("player") {
        permission = "pwp.admin.dimension.load"
        exec{
            if (!sender.hasPermission("pwp.admin.dimension.load")){
                sender.sendMessage(lang.getString("command.warning.noPermission")?:"你没有权限执行这个命令")
                return@exec
            }
            when(args.size){
                1 -> {
                    Local.adminLoadDimension((sender as Player).uniqueId, args[0]).thenApply {
                        sender.sendMessage(it.reason)
                    }
                }
                2 -> {
                    val player = com.mcyzj.pixelworldpro.server.Player.getOfflinePlayer(args[0])
                    Local.adminLoadDimension(player.uniqueId, args[1]).thenApply {
                        sender.sendMessage(it.reason)
                    }
                }
                else -> {
                    sender.sendMessage(lang.getString("command.warning.formatError")?:"命令格式错误")
                    sender.sendMessage(lang.getString("command.prompt.admin.create1")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create2")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create3")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create4")?:"???")
                }
            }
        }
    }
    private val loadDimensionId = command<CommandSender>("id") {
        permission = "pwp.admin.dimension.load"
        exec{
            if (!sender.hasPermission("pwp.admin.dimension.load")){
                sender.sendMessage(lang.getString("command.warning.noPermission")?:"你没有权限执行这个命令")
                return@exec
            }
            when(args.size){
                1 -> {
                    Local.adminLoadDimension((sender as Player).uniqueId, args[0]).thenApply {
                        sender.sendMessage(it.reason)
                    }
                }
                2 -> {
                    val id = try {
                        args[0].toInt()
                    }catch (_:Exception){
                        sender.sendMessage(lang.getString("world.warning.dimension.load.notID")?:"无法加载维度：输入值不是一个有效的数字id")
                        return@exec
                    }
                    Local.adminLoadDimension(id, args[1]).thenApply {
                        sender.sendMessage(it.reason)
                    }
                }
                else -> {
                    sender.sendMessage(lang.getString("command.warning.formatError")?:"命令格式错误")
                    sender.sendMessage(lang.getString("command.prompt.admin.create1")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create2")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create3")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create4")?:"???")
                }
            }
        }
    }
    private val loadDimension = command<CommandSender>("load") {
        permission = "pwp.admin.dimension.load"
        sub(loadDimensionId)
        sub(loadDimensionPlayer)
    }

    private val unloadDimensionPlayer = command<CommandSender>("player") {
        permission = "pwp.admin.dimension.unload"
        exec{
            if (!sender.hasPermission("pwp.admin.dimension.unload")){
                sender.sendMessage(lang.getString("command.warning.noPermission")?:"你没有权限执行这个命令")
                return@exec
            }
            when(args.size){
                1 -> {
                    Local.adminUnloadDimension((sender as Player).uniqueId, args[0]).thenApply {
                        sender.sendMessage(it.reason)
                    }
                }
                2 -> {
                    val player = com.mcyzj.pixelworldpro.server.Player.getOfflinePlayer(args[0])
                    Local.adminUnloadDimension(player.uniqueId, args[1]).thenApply {
                        sender.sendMessage(it.reason)
                    }
                }
                else -> {
                    sender.sendMessage(lang.getString("command.warning.formatError")?:"命令格式错误")
                    sender.sendMessage(lang.getString("command.prompt.admin.create1")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create2")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create3")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create4")?:"???")
                }
            }
        }
    }
    private val unloadDimensionId = command<CommandSender>("id") {
        permission = "pwp.admin.dimension.unload"
        exec{
            if (!sender.hasPermission("pwp.admin.dimension.unload")){
                sender.sendMessage(lang.getString("command.warning.noPermission")?:"你没有权限执行这个命令")
                return@exec
            }
            when(args.size){
                1 -> {
                    Local.adminUnloadDimension((sender as Player).uniqueId, args[0]).thenApply {
                        sender.sendMessage(it.reason)
                    }
                }
                2 -> {
                    val id = try {
                        args[0].toInt()
                    }catch (_:Exception){
                        sender.sendMessage(lang.getString("world.warning.dimension.unload.notID")?:"无法卸载维度：输入值不是一个有效的数字id")
                        return@exec
                    }
                    Local.adminUnloadDimension(id, args[1]).thenApply {
                        sender.sendMessage(it.reason)
                    }
                }
                else -> {
                    sender.sendMessage(lang.getString("command.warning.formatError")?:"命令格式错误")
                    sender.sendMessage(lang.getString("command.prompt.admin.create1")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create2")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create3")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create4")?:"???")
                }
            }
        }
    }
    private val unloadDimension = command<CommandSender>("unload") {
        permission = "pwp.admin.dimension.unload"
        sub(unloadDimensionId)
        sub(unloadDimensionPlayer)
    }

    private val tpDimensionPlayer = command<CommandSender>("player") {
        permission = "pwp.admin.dimension.tp"
        exec{
            if (!sender.hasPermission("pwp.admin.dimension.tp")){
                sender.sendMessage(lang.getString("command.warning.noPermission")?:"你没有权限执行这个命令")
                return@exec
            }
            when(args.size){
                1 -> {
                    Local.adminTpDimension((sender as Player), (sender as Player).uniqueId, args[0]).thenApply {
                        sender.sendMessage(it.reason)
                    }
                }
                2 -> {
                    val player = com.mcyzj.pixelworldpro.server.Player.getOfflinePlayer(args[0])
                    Local.adminTpDimension((sender as Player), player.uniqueId, args[1]).thenApply {
                        sender.sendMessage(it.reason)
                    }
                }
                3 -> {
                    val player = com.mcyzj.pixelworldpro.server.Player.getOfflinePlayer(args[0])
                    val toPlayer = com.mcyzj.pixelworldpro.server.Player.getOfflinePlayer(args[2])
                    if (toPlayer.isOnline) {
                        Local.adminTpDimension(toPlayer.player!!, player.uniqueId, args[1]).thenApply {
                            sender.sendMessage(it.reason)
                        }
                    }
                }
                else -> {
                    sender.sendMessage(lang.getString("command.warning.formatError")?:"命令格式错误")
                    sender.sendMessage(lang.getString("command.prompt.admin.create1")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create2")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create3")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create4")?:"???")
                }
            }
        }
    }
    private val tpDimensionId = command<CommandSender>("id") {
        permission = "pwp.admin.dimension.tp"
        exec{
            if (!sender.hasPermission("pwp.admin.dimension.tp")){
                sender.sendMessage(lang.getString("command.warning.noPermission")?:"你没有权限执行这个命令")
                return@exec
            }
            when(args.size){
                1 -> {
                    Local.adminTpDimension((sender as Player), (sender as Player).uniqueId, args[0]).thenApply {
                        sender.sendMessage(it.reason)
                    }
                }
                2 -> {
                    val id = try {
                        args[0].toInt()
                    }catch (_:Exception){
                        sender.sendMessage(lang.getString("world.warning.dimension.tp.notID")?:"无法传送维度：输入值不是一个有效的数字id")
                        return@exec
                    }
                    Local.adminTpDimension((sender as Player), id, args[1]).thenApply {
                        sender.sendMessage(it.reason)
                    }
                }
                3 -> {
                    val id = try {
                        args[0].toInt()
                    }catch (_:Exception){
                        sender.sendMessage(lang.getString("world.warning.dimension.tp.notID")?:"无法传送维度：输入值不是一个有效的数字id")
                        return@exec
                    }
                    val toPlayer = com.mcyzj.pixelworldpro.server.Player.getOfflinePlayer(args[2])
                    if (toPlayer.isOnline) {
                        Local.adminTpDimension(toPlayer.player!!, id, args[1]).thenApply {
                            sender.sendMessage(it.reason)
                        }
                    }
                }
                else -> {
                    sender.sendMessage(lang.getString("command.warning.formatError")?:"命令格式错误")
                    sender.sendMessage(lang.getString("command.prompt.admin.create1")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create2")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create3")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create4")?:"???")
                }
            }
        }
    }
    private val tpDimension = command<CommandSender>("tp") {
        permission = "pwp.admin.dimension.tp"
        sub(tpDimensionId)
        sub(tpDimensionPlayer)
    }

    private val dimension = command<CommandSender>("dimension") {
        permission = "pwp.admin.dimension"
        sub(createDimension)
        sub(loadDimension)
        sub(unloadDimension)
        sub(tpDimension)
    }

    private val restoreBackupPlayer = command<CommandSender>("player") {
        permission = "pwp.admin.restoreBackup"
        exec{
            if (!sender.hasPermission("pwp.admin.restoreBackup")){
                sender.sendMessage(lang.getString("command.warning.noPermission")?:"你没有权限执行这个命令")
                return@exec
            }
            when(args.size){
                2 -> {
                    val player = com.mcyzj.pixelworldpro.server.Player.getOfflinePlayer(args[0])
                    Local.adminTpDimension((sender as Player), player.uniqueId, args[1]).thenApply {
                        sender.sendMessage(it.reason)
                    }
                }
                else -> {
                    sender.sendMessage(lang.getString("command.warning.formatError")?:"命令格式错误")
                    sender.sendMessage(lang.getString("command.prompt.admin.create1")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create2")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create3")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create4")?:"???")
                }
            }
        }
    }
    private val restoreBackupId = command<CommandSender>("id") {
        permission = "pwp.admin.restoreBackup"
        exec{
            if (!sender.hasPermission("pwp.admin.restoreBackup")){
                sender.sendMessage(lang.getString("command.warning.noPermission")?:"你没有权限执行这个命令")
                return@exec
            }
            when(args.size){
                1 -> {
                    Local.adminTpDimension((sender as Player), (sender as Player).uniqueId, args[0]).thenApply {
                        sender.sendMessage(it.reason)
                    }
                }
                2 -> {
                    val id = try {
                        args[0].toInt()
                    }catch (_:Exception){
                        sender.sendMessage(lang.getString("world.warning.dimension.tp.notID")?:"无法传送维度：输入值不是一个有效的数字id")
                        return@exec
                    }
                    Local.adminTpDimension((sender as Player), id, args[1]).thenApply {
                        sender.sendMessage(it.reason)
                    }
                }
                3 -> {
                    val id = try {
                        args[0].toInt()
                    }catch (_:Exception){
                        sender.sendMessage(lang.getString("world.warning.dimension.tp.notID")?:"无法传送维度：输入值不是一个有效的数字id")
                        return@exec
                    }
                    val toPlayer = com.mcyzj.pixelworldpro.server.Player.getOfflinePlayer(args[2])
                    if (toPlayer.isOnline) {
                        Local.adminTpDimension(toPlayer.player!!, id, args[1]).thenApply {
                            sender.sendMessage(it.reason)
                        }
                    }
                }
                else -> {
                    sender.sendMessage(lang.getString("command.warning.formatError")?:"命令格式错误")
                    sender.sendMessage(lang.getString("command.prompt.admin.create1")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create2")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create3")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.admin.create4")?:"???")
                }
            }
        }
    }
    private val restoreBackup = command<CommandSender>("restoreBackup") {
        permission = "pwp.admin.restoreBackup"
        sub(restoreBackupId)
        sub(restoreBackupPlayer)
    }

    private var admin = command<CommandSender>("admin") {
        permission = "pwp.admin"
        sub(create)
        sub(load)
        sub(unload)
        sub(tp)
        sub(expansion)
        sub(group)
        sub(name)
        sub(dimension)
        sub(reload)
    }

    fun setCommand(expansion: String, command: CommandSpec<CommandSender>){
        commandMap[expansion] = command
    }

    fun getCommand(): CommandSpec<CommandSender> {
        logger.info("注册 ${commandMap.keys.size} 个Admin扩展命令")
        for (key in commandMap.keys) {
            logger.info("注册命令Admin扩展命令 $key")
            admin.sub(commandMap[key]!!)
        }
        return admin
    }
}