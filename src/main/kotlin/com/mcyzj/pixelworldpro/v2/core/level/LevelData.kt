package com.mcyzj.pixelworldpro.v2.core.level

data class LevelData(
    var level: Int,
    val maxLevel: Boolean,
    val points: Double,
    val money: Double,
    val item: HashMap<String, Int>
)