package com.mcyzj.pixelworldpro.v2.world.permission

data class PermissionUpData(
    val points: Double,
    val money: Double,
    val item: HashMap<String, Int>
)