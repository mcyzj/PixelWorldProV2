package com.mcyzj.pixelworldpro.v2.core.command.admin

import com.mcyzj.lib.bukkit.command.command
import com.mcyzj.pixelworldpro.v2.core.util.Config
import org.bukkit.Bukkit
import org.bukkit.WorldCreator
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class LocalWorld {
    private val lang = Config.getLang()

    private val worldTp = command<CommandSender>("tp") {
        permission = "pixelworldpro.admin"
        exec {
            if (args.size == 1) {
                if (sender !is Player) {
                    return@exec
                }
                val world = Bukkit.getWorld(args[0])
                if(world == null){
                    sender.sendMessage("无法找到世界${args[0]}")
                    return@exec
                }
                Bukkit.getConsoleSender().sendMessage("传送中")
                (sender as Player).teleport(world.spawnLocation)
            }
            if (args.size == 2) {
                if (!sender.hasPermission("pixelworldpro.command.admin")) {
                    return@exec
                }
                val player = Bukkit.getPlayer(args[1])
                if (player == null) {
                    return@exec
                }
                val world = Bukkit.getWorld(args[0])
                if(world == null){
                    sender.sendMessage("无法找到世界${args[0]}")
                    return@exec
                }
                Bukkit.getConsoleSender().sendMessage("传送中")
                player.teleport(world.spawnLocation)
            }
        }
    }

    private val worldLoad = command<CommandSender>("load") {
        permission = "pixelworldpro.admin"
        exec{
            if (args.size == 1) {
                if (!sender.hasPermission("pixelworldpro.command.admin")) {
                    return@exec
                }
                var world = Bukkit.getWorld(args[0])
                if(world != null){
                    sender.sendMessage("世界已加载")
                    return@exec
                }
                world = Bukkit.createWorld(WorldCreator(args[0]))
                if(world != null){
                    sender.sendMessage("世界加载成功")
                    return@exec
                }else{
                    sender.sendMessage("世界加载失败")
                    return@exec
                }
            }else{
                sender.sendMessage("参数不合法")
            }
        }
    }

    private val worldUnLoad = command<CommandSender>("unload") {
        permission = "pixelworldpro.admin"
        exec{
            if (args.size == 1) {
                if (!sender.hasPermission("pixelworldpro.command.admin")) {
                    return@exec
                }
                val world = Bukkit.getWorld(args[0])
                if(world == null){
                    sender.sendMessage("世界未加载")
                    return@exec
                }
                if(Bukkit.unloadWorld(world, true)){
                    sender.sendMessage("世界卸载成功")
                    return@exec
                }else{
                    sender.sendMessage("世界卸载失败")
                    return@exec
                }
            }else{
                sender.sendMessage("参数不合法")
            }
        }
    }

    val localWorld = command<CommandSender>("localWorld") {
        permission = "pixelworldpro.admin"
        sub(worldTp)
        sub(worldLoad)
        sub(worldUnLoad)
    }
}