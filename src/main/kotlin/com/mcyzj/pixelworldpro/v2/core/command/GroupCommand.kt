package com.mcyzj.pixelworldpro.v2.core.command

import com.mcyzj.lib.bukkit.command.command
import com.mcyzj.lib.plugin.PlayerFound
import com.mcyzj.pixelworldpro.v2.core.api.PixelWorldProApi
import com.mcyzj.pixelworldpro.v2.core.permission.LocalPermission
import com.mcyzj.pixelworldpro.v2.core.util.Config
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class GroupCommand {
    private val lang = Config.getLang()


    private val groupSet = command<CommandSender>("set") {
        permission = "pixelworldpro.group.set"
        exec{
            if (sender !is Player) {
                sender.sendMessage(lang.getString("plugin.needPlayer") ?: "需要玩家操作")
                return@exec
            }
            if (!sender.hasPermission("pwp.user.group.set")){
                sender.sendMessage(lang.getString("command.warning.noPermission")?:"你没有权限执行这个命令")
                return@exec
            }
            when(args.size){
                2 -> {
                    val player = PlayerFound.getOfflinePlayer(args[0])
                    val world = PixelWorldProApi().getWorld((sender as Player).uniqueId) ?: return@exec
                    val worldData = world.worldData
                    if (worldData.owner == player.uniqueId){
                        return@exec
                    }
                    sender.sendMessage(LocalPermission.setGroup(world, player, args[1]).reason)
                }
            }
        }
    }

    private val groupUp = command<CommandSender>("up") {
        permission = "pixelworldpro.group.up"
        exec{
            if (sender !is Player) {
                sender.sendMessage(lang.getString("plugin.needPlayer") ?: "需要玩家操作")
                return@exec
            }
            if (!sender.hasPermission("pwp.user.group.up")){
                sender.sendMessage(lang.getString("command.warning.noPermission")?:"你没有权限执行这个命令")
                return@exec
            }
            when(args.size){
                1 -> {
                    val world = PixelWorldProApi().getWorld((sender as Player).uniqueId) ?: return@exec
                    sender.sendMessage(LocalPermission.upPermission(world, args[0]).reason)
                }
            }
        }
    }

    val group = command<CommandSender>("group") {
        permission = "pixelworldpro.use"
        sub(groupSet)
        sub(groupUp)
    }
}