package com.mcyzj.pixelworldpro.v2.core.command

import com.mcyzj.lib.bukkit.bridge.replacePlaceholder
import com.mcyzj.lib.bukkit.command.command
import com.mcyzj.lib.bukkit.menu.Menu
import com.mcyzj.lib.bukkit.menu.MenuImpl
import com.mcyzj.lib.bukkit.utils.Color.colored
import com.mcyzj.pixelworldpro.v2.Main
import com.mcyzj.pixelworldpro.v2.core.command.admin.AdminCommand
import com.mcyzj.pixelworldpro.v2.core.util.Config
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandCore {
    val lang = Config.getLang()

    private val menu = command<CommandSender>("menu") {
        permission = "pixelworldpro.menu"
        exec{
            if (sender !is Player) {
                sender.sendMessage(lang.getString("plugin.needPlayer") ?: "需要玩家操作")
                return@exec
            }
            val player = sender as Player
            val menu = when (args.size) {
                0 -> "list"
                else -> {
                    args[0]
                }
            }
            val menuData = MenuImpl.getMenu(menu, Main.instance)
            if (menuData == null) {
                sender.sendMessage((lang.getString("command.menu.notFound") ?: "没有找到菜单").colored().replacePlaceholder(player))
                return@exec
            }
            Menu(player, player, menuData).open()
        }
    }


    private val mainCommand = Config.config.getString("mainCommand") ?:"pwp"
    val commandRoot = command<CommandSender>(mainCommand) {
        permission = "pixelworldpro.use"
        sub(WorldCommand().world)
        sub(AdminCommand().admin)
        sub(GroupCommand().group)
        sub(LevelCommand().level)
        sub(menu)
    }
}