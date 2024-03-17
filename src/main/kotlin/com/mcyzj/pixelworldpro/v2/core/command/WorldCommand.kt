package com.mcyzj.pixelworldpro.v2.core.command

import com.mcyzj.lib.bukkit.command.command
import com.mcyzj.pixelworldpro.v2.core.api.PixelWorldProApi
import com.mcyzj.pixelworldpro.v2.core.util.Config
import com.mcyzj.pixelworldpro.v2.core.world.WorldImpl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class WorldCommand {
    private val lang = Config.getLang()

    private val create = command<CommandSender>("create") {
        permission = "pixelworldpro.create"
        exec {
            if (sender !is Player) {
                sender.sendMessage(lang.getString("plugin.needPlayer") ?: "需要玩家操作")
                return@exec
            }
            val player = sender as Player
            when (args.size) {
                0 -> {
                    sender.sendMessage(WorldImpl.createWorld(player, null, null).reason)
                }
                1 -> {
                    sender.sendMessage(WorldImpl.createWorld(player, args[0], null).reason)
                }
                2 -> {
                    val template = if (args[0] =="auto") {
                        null
                    } else {
                        args[0]
                    }
                    val seed = try {
                        args[1].toLong()
                    } catch (_:Exception) {
                        sender.sendMessage(lang.getString("check.notEnough.seed") ?: "种子不合法")
                        return@exec
                    }
                    sender.sendMessage(WorldImpl.createWorld(player, template, seed).reason)
                }
            }
        }
    }

    private val tp = command<CommandSender>("tp") {
        permission = "pixelworldpro.tp"
        exec {
            if (sender !is Player) {
                sender.sendMessage(lang.getString("plugin.needPlayer") ?: "需要玩家操作")
                return@exec
            }
            val player = sender as Player
            when (args.size) {
                0 -> {
                    val world = PixelWorldProApi().getWorld(player.uniqueId) ?: return@exec
                    world.teleport(player)
                }
                1 -> {
                    sender.sendMessage(WorldImpl.teleport(args[0], player).reason)
                }
            }
        }
    }

    private val unload = command<CommandSender>("unload") {
        permission = "pixelworldpro.unload"
        exec {
            if (sender !is Player) {
                sender.sendMessage(lang.getString("plugin.needPlayer") ?: "需要玩家操作")
                return@exec
            }
            val player = sender as Player
            val world = PixelWorldProApi().getWorld(player.uniqueId) ?: return@exec
            world.unload()
        }
    }

    val world = command<CommandSender>("world") {
        permission = "pixelworldpro.use"
        sub(create)
        sub(tp)
        sub(unload)
    }
}