package com.mcyzj.pixelworldpro.v2.permission.dataclass

data class PermissionUpData(
    val points: Double,
    val money: Double,
    val item: HashMap<String, Int>
)