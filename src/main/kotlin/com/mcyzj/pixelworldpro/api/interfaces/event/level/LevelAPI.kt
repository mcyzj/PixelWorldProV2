package com.mcyzj.pixelworldpro.api.interfaces.event.level

import com.mcyzj.pixelworldpro.data.dataclass.WorldData

interface LevelAPI {
    fun levelChange(worldData: WorldData, level: Int)
}