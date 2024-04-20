package com.mcyzj.pixelworldpro.v2.core.command.admin

import com.mcyzj.lib.bukkit.bridge.replacePlaceholder
import com.mcyzj.lib.bukkit.command.command
import com.mcyzj.lib.bukkit.utils.Color.colored
import com.mcyzj.lib.plugin.PlayerFound
import com.mcyzj.pixelworldpro.v2.core.api.PixelWorldProApi
import com.mcyzj.pixelworldpro.v2.core.util.Config
import com.mcyzj.pixelworldpro.v2.core.util.UpData
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class UpDataCommand {
    val lang = Config.getLang()

    private val v1 = command<CommandSender>("V1") {
        exec{
            if (!Bukkit.getPluginManager().isPluginEnabled("PixelWorldPro")) {
                sender.sendMessage("PixelWorldProV1没有启用")
            }
            UpData().fromV1()
        }
    }

    val upData = command<CommandSender>("upData") {
        permission = "pixelworldpro.admin"
        sub(v1)
    }
}