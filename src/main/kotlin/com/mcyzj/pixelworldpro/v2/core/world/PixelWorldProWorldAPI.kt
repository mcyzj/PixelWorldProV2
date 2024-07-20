package com.mcyzj.pixelworldpro.v2.core.world

import com.mcyzj.pixelworldpro.v2.core.PixelWorldPro
import com.mcyzj.pixelworldpro.v2.core.permission.dataclass.ResultData
import com.mcyzj.pixelworldpro.v2.core.world.dataclass.WorldData
import org.bukkit.World
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

interface PixelWorldProWorldAPI {
    fun load(world: PixelWorldProWorld, serverName: String? = null): CompletableFuture<ResultData>

    fun unload(world: PixelWorldProWorld): CompletableFuture<ResultData>
    fun isLoad(world: PixelWorldProWorld): CompletableFuture<Boolean>
    fun teleport(player: Player, world: PixelWorldProWorld): CompletableFuture<ResultData>
}