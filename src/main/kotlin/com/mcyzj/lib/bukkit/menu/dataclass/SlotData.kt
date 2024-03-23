package com.mcyzj.lib.bukkit.menu.dataclass

import org.bukkit.configuration.ConfigurationSection


data class SlotData(
    val material: String,
    val config: ConfigurationSection,

    val lore: ArrayList<String>,
    val operate: ArrayList<String>,
    val data: ConfigurationSection,

    val name: String?,
    val type: String?,
    val value: String?
)