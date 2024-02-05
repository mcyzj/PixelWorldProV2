package com.mcyzj.pixelworldpro.data.dataclass.gui

data class ConfigItemData(
    val material: String,
    val name: String?,
    val lore: List<String>,
    val type: String?,
    val command: List<String>,
    val value: String?,
    val cache: HashMap<String, String>
)
