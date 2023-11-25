package com.mcyzj.pixelworldpro.expansion.core.gui.dataclass

import com.mcyzj.pixelworldpro.api.interfaces.core.gui.Menu
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.Inventory

data class MenuData(
    val inventory : Inventory,
    val config: YamlConfiguration,
    val menuItemMap: HashMap<Int, MenuItemData>,
    val cache: HashMap<String, Any>,
    val menu: Menu
)
