package com.mcyzj.pixelworldpro.api.interfaces.core.permission

import com.mcyzj.pixelworldpro.data.dataclass.WorldData
import com.mcyzj.pixelworldpro.permission.PermissionImpl
import org.bukkit.OfflinePlayer

interface PermissionAPI {
    fun getConfigWorldPermission():HashMap<String, HashMap<String, String>>
    fun changePermission(player: OfflinePlayer, world: WorldData, permission: String)

    object Factory {
        fun get() : PermissionImpl {
            return PermissionImpl
        }
    }
}