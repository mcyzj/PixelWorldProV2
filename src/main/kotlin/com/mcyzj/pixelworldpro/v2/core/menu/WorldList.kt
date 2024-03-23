package com.mcyzj.pixelworldpro.v2.core.menu

import com.mcyzj.pixelworldpro.v2.core.database.DataBase
import com.mcyzj.pixelworldpro.v2.core.world.PixelWorldProWorld
import org.bukkit.OfflinePlayer

class WorldList {
    val worldMap = HashMap<Int, PixelWorldProWorld>()
    //目前获取到的最后一个世界id
    var worldListLast = 0
    fun getList(player: OfflinePlayer, type: String, number: Int) {
        when(type) {
            "world" -> {
                var emptyTime = 0
                while (true) {
                    if (emptyTime > 10) {
                        break
                    }
                    val world = worldMap[number]
                    if (world != null) {
                        break
                    }
                    val worldData = DataBase.getDataDriver("local").getWorldData(worldListLast)
                    worldListLast ++
                    if (worldData != null) {
                        if (worldData.id == -1) {
                            continue
                        }
                        if (worldData.player[player.uniqueId] != null){
                            val permissionData = worldData.permission[worldData.player[player.uniqueId]]!!
                            when (permissionData["teleport"]){
                                "false" -> {
                                    continue
                                }
                            }
                            return
                        } else {
                            val permissionData = worldData.permission["visitor"]!!
                            when (permissionData["teleport"]) {
                                "false" -> {
                                    continue
                                }
                            }
                        }
                        if (worldMap.isEmpty()) {
                            worldMap[0] = PixelWorldProWorld(worldData)
                        } else {
                            worldMap[worldMap.size] = PixelWorldProWorld(worldData)
                        }
                    } else {
                        emptyTime ++
                        continue
                    }
                }
            }
        }
    }
}