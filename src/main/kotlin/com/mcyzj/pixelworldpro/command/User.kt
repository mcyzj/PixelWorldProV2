package com.mcyzj.pixelworldpro.command

import com.mcyzj.pixelworldpro.PixelWorldPro
import com.mcyzj.pixelworldpro.api.interfaces.core.world.WorldAPI
import com.mcyzj.pixelworldpro.expansion.core.gui.Open
import com.mcyzj.pixelworldpro.file.Config
import com.mcyzj.pixelworldpro.world.Local
import com.xbaimiao.easylib.module.command.ArgNode
import com.xbaimiao.easylib.module.command.CommandSpec
import com.xbaimiao.easylib.module.command.command
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object User {
    private val logger = PixelWorldPro.instance.logger
    private val lang = PixelWorldPro.instance.lang
    private val database = PixelWorldPro.databaseApi
    private val commandMap = HashMap<String, CommandSpec<CommandSender>>()

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
    private val tpId = command<CommandSender>("id") {
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

    private val guiArgNode = ArgNode("",
        exec = {
            Config.gui.getConfigurationSection("Command")!!.getKeys(false).toList()
        }, parse = {
            it
        }
    )

    private val gui = command<CommandSender>("gui") {
        permission = "pwp.user.gui"
        arg(guiArgNode) { gui ->
            exec {
                if (!sender.hasPermission("pwp.user.gui")) {
                    sender.sendMessage(lang.getString("command.warning.noPermission") ?: "你没有权限执行这个命令")
                    return@exec
                }
                when (args.size) {
                    0 -> {

                    }

                    1 -> {
                        Open.open(sender as Player, valueOf(gui))
                    }

                    else -> {
                        sender.sendMessage(lang.getString("command.warning.formatError") ?: "命令格式错误")
                        sender.sendMessage(lang.getString("command.prompt.admin.create1") ?: "???")
                        sender.sendMessage(lang.getString("command.prompt.admin.create2") ?: "???")
                        sender.sendMessage(lang.getString("command.prompt.admin.create3") ?: "???")
                        sender.sendMessage(lang.getString("command.prompt.admin.create4") ?: "???")
                    }
                }
            }
        }
    }

    private val groupSet = command<CommandSender>("set") {
        permission = "pwp.user.group.set"
        exec{
            if (!sender.hasPermission("pwp.user.group.set")){
                sender.sendMessage(lang.getString("command.warning.noPermission")?:"你没有权限执行这个命令")
                return@exec
            }
            when(args.size){
                2 -> {
                    val player = com.mcyzj.pixelworldpro.server.Player.getOfflinePlayer(args[0])
                    val worldData = database.getWorldData((sender as Player).uniqueId) ?: return@exec
                    if (worldData.owner == player.uniqueId){
                        return@exec
                    }
                    sender.sendMessage(com.mcyzj.pixelworldpro.permission.Local.setGroup(worldData, player, args[1]).reason)
                }
            }
        }
    }

    private val groupUp = command<CommandSender>("up") {
        permission = "pwp.user.group.up"
        exec{
            if (!sender.hasPermission("pwp.user.group.up")){
                sender.sendMessage(lang.getString("command.warning.noPermission")?:"你没有权限执行这个命令")
                return@exec
            }
            when(args.size){
                1 -> {
                    val worldData = database.getWorldData((sender as Player).uniqueId) ?: return@exec
                    sender.sendMessage(com.mcyzj.pixelworldpro.permission.Local.upPermission(worldData, args[0]).reason)
                }
            }
        }
    }

    private val group = command<CommandSender>("group") {
        permission = "pwp.user.group"
        sub(groupSet)
        sub(groupUp)
    }

    private val user = command<CommandSender>("user") {
        permission = "pwp.user"
        sub(create)
        sub(load)
        sub(unload)
        sub(tp)
        sub(gui)
        sub(group)
    }

    fun setCommand(expansion: String, command: CommandSpec<CommandSender>){
        commandMap[expansion] = command
    }

    fun getCommand(): CommandSpec<CommandSender> {
        logger.info("注册 ${commandMap.keys.size} 个User扩展命令")
        for (key in commandMap.keys) {
            logger.info("注册命令User扩展命令 $key")
            user.sub(commandMap[key]!!)
        }
        return user
    }
}