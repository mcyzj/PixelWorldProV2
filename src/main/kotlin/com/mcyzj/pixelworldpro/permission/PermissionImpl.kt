package com.mcyzj.pixelworldpro.permission

import com.mcyzj.pixelworldpro.api.interfaces.Permission
import com.mcyzj.pixelworldpro.config.Config
import com.mcyzj.pixelworldpro.world.WorldImpl

object PermissionImpl : Permission {
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
}