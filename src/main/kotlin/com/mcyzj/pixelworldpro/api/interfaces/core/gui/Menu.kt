package com.mcyzj.pixelworldpro.api.interfaces.core.gui

import com.mcyzj.pixelworldpro.data.dataclass.gui.MenuData
import org.bukkit.OfflinePlayer
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType

interface Menu {
    fun open(opener: Player, player: OfflinePlayer, menu: YamlConfiguration, cache: HashMap<String, Any>)
    fun onClick(player: Player, number: Int, clickType: ClickType, menuData: MenuData)
}