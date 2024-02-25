package com.mcyzj.pixelworldpro.v2.permission

import com.mcyzj.pixelworldpro.v2.PixelWorldPro
import com.mcyzj.pixelworldpro.v2.util.Config
import com.mcyzj.pixelworldpro.v2.world.dataclass.WorldData
import org.bukkit.OfflinePlayer

object PermissionImpl{
    private val permission = Config.permission
    fun getConfigWorldPermission(): HashMap<String, HashMap<String, String>> {
        val permissionList = permission.getConfigurationSection("world")!!.getKeys(false)
        val permissionMap = java.util.HashMap<String, java.util.HashMap<String, String>>()
        for (p in permissionList){
            val map = java.util.HashMap<String, String>()
            for (key in permission.getConfigurationSection("world.$p")!!.getKeys(false)){
                map[key] = permission.getString("world.$p.$key")!!
            }
            permissionMap[p] = map
        }
        return permissionMap
    }

    fun changePermission(player: OfflinePlayer, world: WorldData, permission: String) {
        val playerMap = world.player
        val permissionData = world.permission
        if (permission !in permissionData){
            return
        }
        playerMap[player.uniqueId] = permission
        world.player = playerMap
        PixelWorldPro.databaseApi.setWorldData(world)
    }
}