package com.mcyzj.pixelworldpro.v2.core.menu

import com.mcyzj.lib.bukkit.menu.MenuAPI
import com.mcyzj.lib.bukkit.menu.dataclass.SlotData
import com.mcyzj.pixelworldpro.v2.core.database.DataBase
import com.mcyzj.pixelworldpro.v2.core.world.PixelWorldProWorld
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.configuration.file.YamlConfiguration

class WorldList: MenuAPI {

    private val worldMap = HashMap<Int, PixelWorldProWorld>()
    //目前获取到的最后一个世界id
    private var worldListLast = 0

    override fun getItem(player: OfflinePlayer, slotData: SlotData, number: Int, extend: YamlConfiguration): SlotData? {
        val dataFile = slotData.data
        var emptyTime = 0
        while (true) {
            if (emptyTime > 10) {
                return null
            }
            val world = worldMap[number]
            if (world != null) {
                dataFile.set("World.Name", world.worldData.name)
                dataFile.set("World.ID", world.worldData.id)
                val owner = Bukkit.getOfflinePlayer(world.worldData.owner)
                dataFile.set("World.Owner.Name", owner.name)
                dataFile.set("World.Owner.UUID", owner.uniqueId)
                dataFile.set("World.Type", world.worldData.type)
                return slotData.copy(data = dataFile)
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