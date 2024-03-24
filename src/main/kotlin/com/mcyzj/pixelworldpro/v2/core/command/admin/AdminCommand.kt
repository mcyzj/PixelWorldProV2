package com.mcyzj.pixelworldpro.v2.core.command.admin

import com.mcyzj.lib.bukkit.command.command
import org.bukkit.command.CommandSender

class AdminCommand {

    val admin = command<CommandSender>("admin") {
        permission = "pixelworldpro.admin"
        sub(WorldCommand().world)
        sub(LevelCommand().level)
    }
}