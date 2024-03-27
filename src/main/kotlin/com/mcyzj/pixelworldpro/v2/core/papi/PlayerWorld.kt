package com.mcyzj.pixelworldpro.v2.core.papi

import com.mcyzj.pixelworldpro.v2.core.world.PixelWorldProWorld
import com.mcyzj.pixelworldpro.v2.core.world.WorldImpl
import org.bukkit.OfflinePlayer

class PlayerWorld: PapiAPI {
    override fun process(paramsList: List<String>, world: PixelWorldProWorld, player: OfflinePlayer):Any? {
        val worldData = world.worldData
        return when(paramsList[1]){
            "name" -> {
                worldData.name
            }
            "type" -> {
                worldData.type
            }
            "id" -> {
                worldData.id
            }
            "group" -> {
                if (player.uniqueId == worldData.owner){
                    val groupData = worldData.permission["owner"] ?: return null
                    return groupData["name"]
                }
                val group = worldData.player[player.uniqueId] ?: "visitor"
                val groupData = worldData.permission[group] ?: return null
                groupData["name"]
            }
            "permission" -> {
                if (paramsList.size < 4){
                    return null
                }
                val group = paramsList[2]
                val permissionData = worldData.permission[group] ?: return null
                val key = paramsList[3]
                permissionData[key]
            }
            "online" -> {
                return WorldImpl.onlinePlayer[worldData.id]
            }

            else -> {
                null
            }
        }
    }
}