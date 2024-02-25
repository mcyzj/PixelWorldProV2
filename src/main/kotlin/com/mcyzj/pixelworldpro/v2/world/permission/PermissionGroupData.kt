package com.mcyzj.pixelworldpro.v2.world.permission

data class PermissionGroupData(
    val least: Int,
    val max: Int,
    val level: HashMap<Int, PermissionUpData>
)
