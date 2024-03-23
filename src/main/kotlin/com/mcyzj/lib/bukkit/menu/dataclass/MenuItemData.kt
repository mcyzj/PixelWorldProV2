package com.mcyzj.lib.bukkit.menu.dataclass

import org.bukkit.inventory.ItemStack

data class MenuItemData(
    val itemStack: ItemStack,
    val slotData: SlotData
)
