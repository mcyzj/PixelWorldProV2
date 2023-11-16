package com.mcyzj.pixelworldpro.api.interfaces.event.world

import com.mcyzj.pixelworldpro.data.dataclass.WorldData
import com.mcyzj.pixelworldpro.world.WorldImpl
import java.util.UUID
import java.util.concurrent.CompletableFuture

interface WorldSuccessAPI {
    //以下为独立世界-主世界操作成功api

    fun createWorld(worldData: WorldData, template: String)

    fun loadWorld(worldData: WorldData)

    fun unloadWorld(worldData: WorldData)

    fun backupWorld(worldData: WorldData, save: Boolean?)
}