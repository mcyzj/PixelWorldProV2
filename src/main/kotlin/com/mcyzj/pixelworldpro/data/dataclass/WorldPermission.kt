package com.mcyzj.pixelworldpro.data.dataclass

data class WorldPermission(
    val worldData: WorldData,
    var permission: String,
    var permissionMap: HashMap<String, Int>
)
