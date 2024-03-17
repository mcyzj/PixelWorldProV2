package com.mcyzj.pixelworldpro.v2.core.command

import com.mcyzj.lib.bukkit.command.command
import com.mcyzj.lib.plugin.PlayerFound
import com.mcyzj.pixelworldpro.v2.core.api.PixelWorldProApi
import com.mcyzj.pixelworldpro.v2.core.util.Config
import com.mcyzj.pixelworldpro.v2.core.world.WorldImpl
import org.bukkit.command.CommandSender

class AdminCommand {
    private val lang = Config.getLang()

    private val create = command<CommandSender>("create") {
        exec {
            when (args.size) {
                1 -> {
                    val player = PlayerFound.getOfflinePlayer(args[0])
                    WorldImpl.createWorldLocal(player.uniqueId, null, null)
                }

                2 -> {
                    val player = PlayerFound.getOfflinePlayer(args[0])
                    WorldImpl.createWorldLocal(player.uniqueId, args[1], null)
                }

                3 -> {
                    val player = PlayerFound.getOfflinePlayer(args[0])
                    val template = if (args[1] == "auto") {
                        null
                    } else {
                        args[1]
                    }
                    val seed = try {
                        args[2].toLong()
                    } catch (_: Exception) {
                        sender.sendMessage(lang.getString("check.notEnough.seed") ?: "种子不合法")
                        return@exec
                    }
                    WorldImpl.createWorldLocal(player.uniqueId, template, seed)
                }
            }
        }
    }

    private val unload = command<CommandSender>("unload") {
        exec {
            when (args.size) {
                1 -> {
                    val player = PlayerFound.getOfflinePlayer(args[0])
                    val world = PixelWorldProApi().getWorld(player.uniqueId) ?: return@exec
                    world.unload()
                }
            }
        }
    }

    val admin = command<CommandSender>("admin") {
        permission = "pixelworldpro.admin"
        sub(create)
        sub(unload)
    }
}