package com.mcyzj.pixelworldpro.v2.command

import com.mcyzj.pixelworldpro.v2.api.PixelWorldProApi
import com.mcyzj.pixelworldpro.v2.util.Config
import com.xbaimiao.easylib.module.command.command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandCore {
    private val create = command<CommandSender>("create") {
        permission = "pixelworldpro.create"
        exec {
            val player = sender as Player
            PixelWorldProApi().createWorld(player.uniqueId, null, null)
        }
    }

    private val tp = command<CommandSender>("tp") {
        permission = "pixelworldpro.tp"
        exec {
            val player = sender as Player
            val world = PixelWorldProApi().getWorld(player.uniqueId) ?: return@exec
            world.teleport(player)
        }
    }

    private val unload = command<CommandSender>("unload") {
        permission = "pixelworldpro.tp"
        exec {
            val player = sender as Player
            val world = PixelWorldProApi().getWorld(player.uniqueId) ?: return@exec
            world.unload()
        }
    }

    private val mainCommand = Config.config.getString("mainCommand") ?:"pwp"
    val commandRoot = command<CommandSender>(mainCommand) {
        permission = "pixelworldpro.use"
        sub(create)
        sub(tp)
        sub(unload)
    }
}