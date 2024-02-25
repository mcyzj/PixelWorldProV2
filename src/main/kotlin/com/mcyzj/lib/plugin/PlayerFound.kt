package com.mcyzj.lib.plugin

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.util.*

object PlayerFound {
    fun getOfflinePlayer(value: String):OfflinePlayer{
        return try {
            val uuid = UUID.fromString(value)
            Bukkit.getOfflinePlayer(uuid)
        }catch (_:Exception){
            Bukkit.getOfflinePlayer(value)
        }
    }
}