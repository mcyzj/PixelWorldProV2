package com.mcyzj.pixelworldpro.v2.core.papi

import com.mcyzj.lib.bukkit.bridge.PlaceholderExpansion
import com.mcyzj.lib.plugin.PlayerFound
import com.mcyzj.pixelworldpro.v2.core.api.PixelWorldProApi
import com.mcyzj.pixelworldpro.v2.core.util.Config
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player


object Papi: PlaceholderExpansion() {
    private var config = Config.config
    override val identifier: String
        get() = config.getString("mainPapi")!!
    override val version: String
        get() = "2.0.0"

    override fun onRequest(p: OfflinePlayer, params: String): String? {
        val paramsList = params.split("_") as ArrayList<String>
        val player = if ((paramsList.size > 3).and(paramsList[0] == "player")) {
            val cache = PlayerFound.getOfflinePlayer(paramsList[1])
            paramsList.removeFirst()
            paramsList.removeFirst()
            cache
        } else {
            p
        }
        when (paramsList[0]) {
            "local" -> {
                val world = PixelWorldProApi().getWorld(player.uniqueId) ?: return null
                PlayerWorld().process(paramsList, world, player)
            }

            "now" -> {
                if (!player.isOnline) {
                    return null
                }
                val world = PixelWorldProApi().getWorld((player as Player).world.name) ?: return null
                PlayerWorld().process(paramsList, world, player)
            }

            "id" -> {
                val id = try {
                    paramsList[1].toInt()
                } catch (_: Exception) {
                    return null
                }
                val world = PixelWorldProApi().getWorld(id) ?: return null
                paramsList.remove(paramsList[1])
                PlayerWorld().process(paramsList, world, player)
            }

            "name" -> {
                val owner = PlayerFound.getOfflinePlayer(paramsList[1])
                val world = PixelWorldProApi().getWorld(owner.uniqueId) ?: return null
                paramsList.remove(paramsList[1])
                PlayerWorld().process(paramsList, world, player)
            }
        }
        return null
    }
}