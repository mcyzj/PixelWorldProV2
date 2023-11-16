package com.mcyzj.pixelworldpro.api.interfaces.core.permission

import com.mcyzj.pixelworldpro.permission.PermissionAPIImpl

interface PermissionAPI {
    fun getConfigWorldPermission():HashMap<String, HashMap<String, String>>

    object Factory {
        fun get() : PermissionAPIImpl {
            return PermissionAPIImpl
        }
    }
}