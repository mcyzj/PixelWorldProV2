package com.mcyzj.pixelworldpro.v2.core.command.admin

import com.mcyzj.lib.bukkit.bridge.replacePlaceholder
import com.mcyzj.lib.bukkit.command.command
import com.mcyzj.lib.bukkit.utils.Color.colored
import com.mcyzj.lib.plugin.PlayerFound
import com.mcyzj.pixelworldpro.v2.core.api.PixelWorldProApi
import com.mcyzj.pixelworldpro.v2.core.util.Config
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class LevelCommand {
    val lang = Config.getLang()

    private val up = command<CommandSender>("set") {
        exec{
            when (args.size) {
                1 -> {
                    if (sender !is Player) {
                        sender.sendMessage((lang.getString("plugin.needPlayer") ?: "需要玩家操作").colored())
                        return@exec
                    }
                    val player = sender as Player
                    val world = PixelWorldProApi().getWorld(player.uniqueId)
                    if (world == null) {
                        sender.sendMessage((lang.getString("command.admin.level.noWorld")?: "对象没有世界").colored().replacePlaceholder(player))
                        return@exec
                    }
                    val level = try {
                        args[0].toInt()
                    } catch (_:Exception) {
                        sender.sendMessage((lang.getString("command.admin.level.needInt") ?: "输入内容不是一个有效数字").colored().replacePlaceholder(player))
                        return@exec
                    }
                    world.setLevel(level)
                    sender.sendMessage((lang.getString("command.admin.level.success") ?: "成功设置世界等级").colored().replacePlaceholder(player))
                }

                2 -> {
                    val player = PlayerFound.getOfflinePlayer(args[0])
                    val world = PixelWorldProApi().getWorld(player.uniqueId)
                    if (world == null) {
                        sender.sendMessage((lang.getString("command.admin.level.noWorld")?: "对象没有世界").colored().replacePlaceholder(player))
                        return@exec
                    }
                    val level = try {
                        args[1].toInt()
                    } catch (_:Exception) {
                        sender.sendMessage((lang.getString("command.admin.level.needInt") ?: "输入内容不是一个有效数字").colored().replacePlaceholder(player))
                        return@exec
                    }
                    world.setLevel(level)
                    sender.sendMessage((lang.getString("command.admin.level.success") ?: "成功设置世界等级").colored().replacePlaceholder(player))
                }
            }

        }
    }

    val level = command<CommandSender>("level") {
        permission = "pixelworldpro.admin"
        sub(up)
    }
}