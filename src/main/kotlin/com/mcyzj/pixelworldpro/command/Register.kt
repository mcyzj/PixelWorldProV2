package com.mcyzj.pixelworldpro.command

import com.mcyzj.pixelworldpro.PixelWorldPro
import com.mcyzj.pixelworldpro.expansion.core.gui.Open
import com.mcyzj.pixelworldpro.expansion.core.gui.WorldList
import com.xbaimiao.easylib.module.command.command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Register {
    private val config = PixelWorldPro.instance.config
    private val mainCommand = config.getString("mainCommand") ?:"pwp"
    private val lang = PixelWorldPro.instance.lang

    val command = command<CommandSender>(mainCommand) {
        sub(Admin.getCommand())
        sub(User.getCommand())
        sub(command<CommandSender>("gui") {
            permission = "pwp.user.gui"
            exec {
                WorldList(sender as Player).open()
            }
        })
    }
}