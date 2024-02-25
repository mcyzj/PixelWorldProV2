package com.mcyzj.pixelworldpro.v2.permission.dataclass

data class PermissionGroupData(
    val least: Int,
    val max: Int,
    val level: HashMap<Int, PermissionUpData>
)
