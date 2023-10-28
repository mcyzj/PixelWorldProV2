package com.mcyzj.pixelworldpro.command

import com.mcyzj.pixelworldpro.PixelWorldPro
import com.mcyzj.pixelworldpro.api.interfaces.WorldAPI
import com.mcyzj.pixelworldpro.world.Local
import com.xbaimiao.easylib.module.command.command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Admin {
    private val lang = PixelWorldPro.instance.lang
    private val database = PixelWorldPro.databaseApi

    private val create = command<CommandSender>("create") {
        permission = "pwp.admin.create"
        exec{
            if (!sender.hasPermission("pwp.command.admin.create")){
                sender.sendMessage(lang.getString("command.warning.noPermission")?:"你没有权限执行这个命令")
                return@exec
            }
            when(args.size){
                0 -> {
                    if (database.getWorldData((sender as Player).uniqueId) != null){
                        sender.sendMessage(lang.getString("world.warning.create.created")?:"无法创建世界：对象已经有一个世界了")
                        return@exec
                    }
                    Local.adminCreateWorld((sender as Player).uniqueId, null)
                }
                1 -> {
                    if (database.getWorldData((sender as Player).uniqueId) != null){
                        sender.sendMessage(lang.getString("world.warning.create.created")?:"无法创建世界：对象已经有一个世界了")
                        return@exec
                    }
                    Local.adminCreateWorld((sender as Player).uniqueId, args[0])
                }
                2 -> {
                    val player = com.mcyzj.pixelworldpro.server.Player.getOfflinePlayer(args[0])
                    if (database.getWorldData(player.uniqueId) != null){
                        sender.sendMessage(lang.getString("world.warning.create.created")?:"无法创建世界：对象经有一个世界了")
                        return@exec
                    }
                    if (args[1] == "auto") {
                        Local.adminCreateWorld(player.uniqueId, null)
                    }else{
                        Local.adminCreateWorld(player.uniqueId, args[1])
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
        permission = "pwp.admin.unload"
        exec{
            if (!sender.hasPermission("pwp.command.admin.load")){
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
    private val loadId = command<CommandSender>("Id") {
        permission = "pwp.admin.unload"
        exec{
            if (!sender.hasPermission("pwp.command.admin.load")){
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
        sub(loadId)
        sub(loadPlayer)
    }

    private val unloadPlayer = command<CommandSender>("player") {
        permission = "pwp.admin.unload"
        exec{
            if (!sender.hasPermission("pwp.command.admin.unload")){
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
    private val unloadId = command<CommandSender>("Id") {
        permission = "pwp.admin.unload"
        exec{
            if (!sender.hasPermission("pwp.command.admin.unload")){
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
        sub(unloadId)
        sub(unloadPlayer)
    }

    val admin = command<CommandSender>("admin") {
        permission = "pwp.admin"
        sub(create)
        sub(load)
        sub(unload)
    }
}