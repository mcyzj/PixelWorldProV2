package com.mcyzj.pixelworldpro.command

import com.mcyzj.pixelworldpro.PixelWorldPro
import com.mcyzj.pixelworldpro.api.interfaces.WorldAPI
import com.mcyzj.pixelworldpro.gui.Open
import com.mcyzj.pixelworldpro.world.Local
import com.xbaimiao.easylib.module.command.command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class User {
    private val lang = PixelWorldPro.instance.lang
    private val database = PixelWorldPro.databaseApi

    private val create = command<CommandSender>("create") {
        permission = "pwp.user.create"
        exec{
            if (!sender.hasPermission("pwp.user.create")){
                sender.sendMessage(lang.getString("command.warning.noPermission")?:"你没有权限执行这个命令")
                return@exec
            }
            when(args.size){
                0 -> {
                    if (database.getWorldData((sender as Player).uniqueId) != null){
                        sender.sendMessage(lang.getString("world.warning.create.created")?:"无法创建世界：对象已经有一个世界了")
                        return@exec
                    }
                    Local.createWorld((sender as Player).uniqueId, null)
                }
                1 -> {
                    if (database.getWorldData((sender as Player).uniqueId) != null){
                        sender.sendMessage(lang.getString("world.warning.create.created")?:"无法创建世界：对象已经有一个世界了")
                        return@exec
                    }
                    Local.createWorld((sender as Player).uniqueId, args[0])
                }
                else -> {
                    sender.sendMessage(lang.getString("command.warning.formatError")?:"命令格式错误")
                    sender.sendMessage(lang.getString("command.prompt.user.create1")?:"???")
                    sender.sendMessage(lang.getString("command.prompt.user.create2")?:"???")
                }
            }
        }
    }

    private val load = command<CommandSender>("load") {
        permission = "pwp.user.load"
        exec{
            if (!sender.hasPermission("pwp.user.load")){
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
        permission = "pwp.use.unload"
        exec{
            if (!sender.hasPermission("pwp.use.unload")){
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

    private val tpPlayer = command<CommandSender>("player") {
        permission = "pwp.user.tp"
        exec{
            if (!sender.hasPermission("pwp.user.tp")){
                sender.sendMessage(lang.getString("command.warning.noPermission")?:"你没有权限执行这个命令")
                return@exec
            }
            when(args.size){
                0 -> {
                    if (database.getWorldData((sender as Player).uniqueId) == null){
                        sender.sendMessage(lang.getString("world.warning.tp.noWorld")?:"无法传送至世界：对象没有世界")
                        return@exec
                    }
                    Local.tpWorldId(sender as Player, database.getWorldData((sender as Player).uniqueId)!!.id)
                }
                1 -> {
                    val player = com.mcyzj.pixelworldpro.server.Player.getOfflinePlayer(args[0])
                    if (database.getWorldData(player.uniqueId) == null){
                        sender.sendMessage(lang.getString("world.warning.tp.noWorld")?:"无法传送至世界：对象没有世界")
                        return@exec
                    }
                    Local.tpWorldId(sender as Player, database.getWorldData(player.uniqueId)!!.id)
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
    private val tpId = command<CommandSender>("Id") {
        permission = "pwp.user.tp"
        exec{
            if (!sender.hasPermission("pwp.user.tp")){
                sender.sendMessage(lang.getString("command.warning.noPermission")?:"你没有权限执行这个命令")
                return@exec
            }
            when(args.size){
                0 -> {
                    if (database.getWorldData((sender as Player).uniqueId) == null){
                        sender.sendMessage(lang.getString("world.warning.tp.noWorld")?:"无法传送至世界：对象没有世界")
                        return@exec
                    }
                    Local.tpWorldId(sender as Player, database.getWorldData((sender as Player).uniqueId)!!.id)
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
                    Local.tpWorldId(sender as Player, id)
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
        permission = "pwp.user.tp"
        sub(tpId)
        sub(tpPlayer)
    }

    private val gui = command<CommandSender>("gui") {
        permission = "pwp.user.gui"
        exec{
            if (!sender.hasPermission("pwp.user.gui")){
                sender.sendMessage(lang.getString("command.warning.noPermission")?:"你没有权限执行这个命令")
                return@exec
            }
            when(args.size){
                0 -> {

                }
                1 -> {
                    Open.open(sender as Player, args[0])
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

    val user = command<CommandSender>("user") {
        permission = "pwp.user"
        sub(create)
        sub(load)
        sub(unload)
        sub(tp)
        sub(gui)
    }
}