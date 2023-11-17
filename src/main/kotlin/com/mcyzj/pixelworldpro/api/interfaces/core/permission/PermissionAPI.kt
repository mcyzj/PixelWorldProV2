package com.mcyzj.pixelworldpro.api.interfaces.core.permission

import com.mcyzj.pixelworldpro.permission.PermissionImpl

interface PermissionAPI {
    fun getConfigWorldPermission():HashMap<String, HashMap<String, String>>

    object Factory {
        fun get() : PermissionImpl {
            return PermissionImpl
        }
    }
}