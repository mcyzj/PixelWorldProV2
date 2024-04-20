package com.mcyzj.pixelworldpro.v2.core.menu

import com.mcyzj.lib.bukkit.menu.MenuAPI
import com.mcyzj.lib.bukkit.menu.dataclass.SlotData
import com.mcyzj.pixelworldpro.v2.core.api.PixelWorldProApi
import com.mcyzj.pixelworldpro.v2.core.database.DataBase
import com.mcyzj.pixelworldpro.v2.core.world.PixelWorldProWorld
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.configuration.file.YamlConfiguration
import java.util.HashMap

class WorldMemberList: MenuAPI {

    private val memberMap = HashMap<Int, OfflinePlayer>()

    override fun getItem(player: OfflinePlayer, slotData: SlotData, number: Int, extend: YamlConfiguration): SlotData? {
        val extendData = extend.getConfigurationSection("SlotData") ?: return null
        val world = PixelWorldProApi().getWorld(extendData.getInt("World.ID")) ?: return null
        val dataFile = slotData.data

        var i = 0
        while (true) {
            if (i > 10) {
                return null
            }
            val member = memberMap[number]
            if (member != null) {
                dataFile.set("Player.Name", member.name)
                dataFile.set("Player.UUID", member.uniqueId)
                val permission = world.getPlayerPermission(member.uniqueId)
                if (permission != null) {
                    for (key in permission.keys) {
                        dataFile.set("Player.Permission.$key", permission[key])
                    }
                }
                return slotData.copy(data = dataFile)
            }

            i++
            init(world)
        }
    }

    private fun init(world: PixelWorldProWorld) {
        val memberList = world.getMemberList()
        for ((start, member) in memberList.withIndex()) {
            memberMap[start] = member
        }
    }
}