package com.mcyzj.pixelworldpro.v2.core.command

import com.mcyzj.lib.bukkit.command.command
import com.mcyzj.pixelworldpro.v2.core.util.Config
import org.bukkit.command.CommandSender

class CommandCore {


    private val mainCommand = Config.config.getString("mainCommand") ?:"pwp"
    val commandRoot = command<CommandSender>(mainCommand) {
        permission = "pixelworldpro.use"
        sub(WorldCommand().world)
        sub(AdminCommand().admin)
    }
}