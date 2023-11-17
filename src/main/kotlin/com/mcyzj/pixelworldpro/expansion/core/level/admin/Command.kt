package com.mcyzj.pixelworldpro.expansion.core.level.admin

import com.mcyzj.pixelworldpro.PixelWorldPro
import com.mcyzj.pixelworldpro.world.Local
import com.xbaimiao.easylib.module.command.command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Command {
    private val lang = PixelWorldPro.instance.lang
    private val database = PixelWorldPro.databaseApi

    private val setPlayer = command<CommandSender>("player") {
        permission = "pwp.admin.level"
        exec{
            if (!sender.hasPermission("pwp.admin.level")){
                sender.sendMessage(lang.getString("command.warning.noPermission")?:"你没有权限执行这个命令")
                return@exec
            }
            when(args.size){
                2 -> {
                    try {
                        val number = args[1].toInt()
                        val player = com.mcyzj.pixelworldpro.server.Player.getOfflinePlayer(args[0])
                        val worldData = database.getWorldData(player.uniqueId)?:return@exec
                        Admin.setLevel(worldData, number)
                    }catch (e:Exception){
                        e.printStackTrace()
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
    private val setId = command<CommandSender>("id") {
        permission = "pwp.admin.level"
        exec{
            if (!sender.hasPermission("pwp.admin.level")){
                sender.sendMessage(lang.getString("command.warning.noPermission")?:"你没有权限执行这个命令")
                return@exec
            }
            when(args.size){
                2 -> {
                    try {
                        val number = args[1].toInt()
                        val id = args[0].toInt()
                        val worldData = database.getWorldData(id)?:return@exec
                        Admin.setLevel(worldData, number)
                    }catch (e:Exception){
                        e.printStackTrace()
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
    private val set = command<CommandSender>("set") {
        permission = "pwp.admin.level"
        sub(setId)
        sub(setPlayer)
    }
    val level = command<CommandSender>("level") {
        permission = "pwp.admin.level"
        sub(set)
    }
}