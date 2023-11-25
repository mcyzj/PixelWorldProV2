package com.mcyzj.pixelworldpro.command

import com.mcyzj.pixelworldpro.PixelWorldPro
import com.xbaimiao.easylib.module.command.command
import org.bukkit.command.CommandSender

class Register {
    private val config = PixelWorldPro.instance.config
    private val mainCommand = config.getString("mainCommand") ?:"pwp"
    private val lang = PixelWorldPro.instance.lang

    val command = command<CommandSender>(mainCommand) {
        sub(Admin.getCommand())
        sub(User.getCommand())
    }
}