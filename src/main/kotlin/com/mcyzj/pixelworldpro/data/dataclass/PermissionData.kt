package com.mcyzj.pixelworldpro.data.dataclass

data class PermissionData(
    val name: String,
    val up: HashMap<String, PermissionGroupData>
)