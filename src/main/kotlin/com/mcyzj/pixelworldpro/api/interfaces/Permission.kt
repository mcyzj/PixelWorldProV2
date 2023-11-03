package com.mcyzj.pixelworldpro.api.interfaces

import com.mcyzj.pixelworldpro.permission.PermissionImpl
import com.mcyzj.pixelworldpro.world.WorldImpl

interface Permission {
    fun getConfigWorldPermission():HashMap<String, HashMap<String, String>>

    object Factory {
        fun get() : PermissionImpl {
            return PermissionImpl
        }
    }
}