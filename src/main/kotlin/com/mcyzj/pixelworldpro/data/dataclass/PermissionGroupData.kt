package com.mcyzj.pixelworldpro.data.dataclass

data class PermissionGroupData(
    val least: Int,
    val max: Int,
    val level: HashMap<Int, PermissionUpData>
)
