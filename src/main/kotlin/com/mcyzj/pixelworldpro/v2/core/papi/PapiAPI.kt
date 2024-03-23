package com.mcyzj.pixelworldpro.v2.core.papi

import com.mcyzj.pixelworldpro.v2.core.world.PixelWorldProWorld
import org.bukkit.OfflinePlayer

interface PapiAPI {
    fun process(paramsList: List<String>, world: PixelWorldProWorld, player: OfflinePlayer):Any?
}