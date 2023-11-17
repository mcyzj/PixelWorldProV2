package com.mcyzj.pixelworldpro.expansion.core.level.dataclass

data class LevelData(
    var level: Int,
    val maxLevel: Boolean,
    val points: Double,
    val money: Double,
    val item: HashMap<String, Int>
)