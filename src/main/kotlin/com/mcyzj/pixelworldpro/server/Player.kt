package com.mcyzj.pixelworldpro.server

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.util.*

object Player {
    fun getOfflinePlayer(value: String):OfflinePlayer{
        return try {
            val uuid = UUID.fromString(value)
            Bukkit.getOfflinePlayer(uuid)
        }catch (_:Exception){
            Bukkit.getOfflinePlayer(value)
        }
    }
}