package com.mcyzj.pixelworldpro.v2.permission.dataclass

data class PermissionData(
    val name: String,
    val group: HashMap<String, PermissionGroupData>
)