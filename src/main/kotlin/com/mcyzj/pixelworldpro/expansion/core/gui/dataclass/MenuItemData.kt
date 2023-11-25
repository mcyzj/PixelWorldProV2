package com.mcyzj.pixelworldpro.expansion.core.gui.dataclass

import org.bukkit.inventory.ItemStack

data class MenuItemData(
    val itemStack: ItemStack,
    val type: String?,
    val command: List<String>,
    val value: String?,
    val cache: HashMap<String, String>
)
