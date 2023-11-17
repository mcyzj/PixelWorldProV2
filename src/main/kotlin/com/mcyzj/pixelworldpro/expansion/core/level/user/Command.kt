package com.mcyzj.pixelworldpro.expansion.core.level.user

import com.mcyzj.pixelworldpro.PixelWorldPro
import com.xbaimiao.easylib.module.command.command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Command {
    private val lang = PixelWorldPro.instance.lang

    private val up = command<CommandSender>("up") {
        permission = "pwp.user.level"
        exec{
            if (!sender.hasPermission("pwp.user.level")){
                sender.sendMessage(lang.getString("command.warning.noPermission")?:"你没有权限执行这个命令")
                return@exec
            }
            if (sender !is Player){
                return@exec
            }
            User.levelUp(sender as Player)
        }
    }
    val level = command<CommandSender>("level") {
        permission = "pwp.user.level"
        sub(up)
    }
}