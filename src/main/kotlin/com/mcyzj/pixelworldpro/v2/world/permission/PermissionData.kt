package com.mcyzj.pixelworldpro.v2.world.permission

import com.mcyzj.pixelworldpro.v2.world.permission.PermissionGroupData

data class PermissionData(
    val name: String,
    val group: HashMap<String, PermissionGroupData>
)