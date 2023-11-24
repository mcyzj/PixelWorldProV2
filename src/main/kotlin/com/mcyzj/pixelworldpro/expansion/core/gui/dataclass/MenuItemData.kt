package com.mcyzj.pixelworldpro.expansion.core.gui.dataclass

data class MenuItemData(
    val material: String,
    val name: String?,
    val lore: List<String>,
    val type: String?,
    val command: List<String>,
    val value: String?,
    val cache: HashMap<String, String>
)
