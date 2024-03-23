package com.mcyzj.lib.bukkit.menu

import com.mcyzj.lib.bukkit.menu.dataclass.SlotData
import org.bukkit.OfflinePlayer

interface MenuAPI {
    fun getList(player: OfflinePlayer, slotData: SlotData, number: Int): SlotData?
}