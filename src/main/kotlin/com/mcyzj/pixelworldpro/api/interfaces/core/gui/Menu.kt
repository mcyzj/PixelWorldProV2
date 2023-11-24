package com.mcyzj.pixelworldpro.api.interfaces.core.gui

import org.bukkit.OfflinePlayer
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player

interface Menu {
    fun open(opener: Player, player: OfflinePlayer, menu: YamlConfiguration, cache: HashMap<String, String>)
}