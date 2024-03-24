package com.mcyzj.pixelworldpro.v2.core.command

import com.mcyzj.lib.bukkit.command.command
import com.mcyzj.pixelworldpro.v2.core.level.LevelImpl
import com.mcyzj.pixelworldpro.v2.core.util.Config
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class LevelCommand {
    val lang = Config.getLang()

    private val up = command<CommandSender>("up") {
        permission = "pixelworldpro.level.up"
        exec{
            if (sender !is Player) {
                sender.sendMessage(lang.getString("plugin.needPlayer") ?: "需要玩家操作")
                return@exec
            }
            LevelImpl.levelUp(sender as Player)
        }
    }

    val level = command<CommandSender>("level") {
        permission = "pixelworldpro.use"
        sub(up)
    }
}