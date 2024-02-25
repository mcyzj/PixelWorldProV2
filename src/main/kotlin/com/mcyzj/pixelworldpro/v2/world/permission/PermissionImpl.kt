package com.mcyzj.pixelworldpro.permission

import com.mcyzj.pixelworldpro.PixelWorldPro
import com.mcyzj.pixelworldpro.api.interfaces.core.permission.PermissionAPI
import com.mcyzj.pixelworldpro.data.dataclass.WorldData
import com.mcyzj.pixelworldpro.file.Config
import org.bukkit.OfflinePlayer

object PermissionImpl : PermissionAPI {
    private val permission = Config.permission
    override fun getConfigWorldPermission(): HashMap<String, HashMap<String, String>> {
        val permissionList = permission.getConfigurationSection("World")!!.getKeys(false)
        val permissionMap = java.util.HashMap<String, java.util.HashMap<String, String>>()
        for (p in permissionList){
            val map = java.util.HashMap<String, String>()
            for (key in permission.getConfigurationSection("World.$p")!!.getKeys(false)){
                map[key] = permission.getString("World.$p.$key")!!
            }
            permissionMap[p] = map
        }
        return permissionMap
    }

    override fun changePermission(player: OfflinePlayer, world: WorldData, permission: String) {
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