package com.mcyzj.pixelworldpro.api.interfaces.event.world

import com.mcyzj.pixelworldpro.data.dataclass.WorldData
import org.bukkit.World
import org.bukkit.WorldCreator
import java.util.*

interface WorldAPI {
    //以下为独立世界-主世界操作过程api
    fun createWorld(owner: UUID, template: String)

    fun loadWorld(worldData: WorldData)

    fun unloadWorld(worldData: WorldData)

    fun backupWorld(worldData: WorldData, save: Boolean?)

    fun onWorldFileLoad(worldData: WorldData, worldCreator: WorldCreator): WorldCreator
    fun worldFileLoadSuccess(worldData: WorldData, world: World)
}