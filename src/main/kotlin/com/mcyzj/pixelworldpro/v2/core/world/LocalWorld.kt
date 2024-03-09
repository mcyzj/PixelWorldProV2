package com.mcyzj.pixelworldpro.v2.core.world

import com.mcyzj.pixelworldpro.v2.core.PixelWorldPro
import com.mcyzj.pixelworldpro.v2.core.permission.dataclass.ResultData
import com.mcyzj.pixelworldpro.v2.core.util.Config
import com.mcyzj.pixelworldpro.v2.core.world.dataclass.LocationData
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.WorldCreator
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture


/**
 * PixelWorldPro世界
 * @return: PixelWorldProWorld
 */
@Suppress("unused")
class LocalWorld : PixelWorldProWorldAPI {
    private val log = PixelWorldPro.instance.log
    private val lang = Config.getLang()
    private val worldConfig = Config.world
    private val bungeeConfig = Config.bungee
    private val bungee = bungeeConfig.getBoolean("enable")

    /**
     * 是否已加载
     */
    override fun isLoad(world: PixelWorldProWorld): CompletableFuture<Boolean> {
        val worldData = world.worldData
        val future = CompletableFuture<Boolean>()
        val localWorld = Bukkit.getWorld("PixelWorldPro/cache/world/${worldData.type}/${worldData.id}/world")
        future.complete(localWorld != null)
        return future
    }

    /**
     * 加载世界
     */
    override fun load(world: PixelWorldProWorld): CompletableFuture<ResultData> {
        val worldData = world.worldData
        val future = CompletableFuture<ResultData>()
        if (WorldCache.isInUnUse(world)){
            future.complete(
                ResultData(
                false,
                ""
                )
            )
        }
        if (!isLoad(world).get()) {
            world.decompression()
            log.info(lang.getString("world.load") + "${worldData.name}[${worldData.id}]")
            val worldCreator = WorldCreator("PixelWorldPro/cache/world/${worldData.type}/${worldData.id}/world")
            val worldDataConfig = world.getDataConfig("world")
            when (worldDataConfig.getString("worldCreator")) {
                "auto" -> {}
                else -> {
                    worldCreator.generator(worldDataConfig.getString("worldCreator"))
                }
            }
            val seed = worldDataConfig.getString("seed")
            if (seed != null) {
                worldCreator.seed(seed.toLong())
            }
            val localWorld = Bukkit.createWorld(worldCreator)
            if (localWorld == null) {
                future.complete(
                    ResultData(
                    false,
                    ""
                )
                )
                return future
            }
            localWorld.keepSpawnInMemory = false
            WorldImpl.loadWorld[worldData.id] = world
            future.complete(
                ResultData(
                true,
                ""
            )
            )
        } else {
            future.complete(
                ResultData(
                    false,
                    ""
                )
            )
        }
        return future
    }

    /**
     * 卸载世界
     */
    override fun unload(world: PixelWorldProWorld): CompletableFuture<ResultData> {
        val worldData = world.worldData
        val future = CompletableFuture<ResultData>()
        if (isLoad(world).get()) {
            log.info(lang.getString("world.unload") + "${worldData.name}[${worldData.id}]")
            val localWorld = Bukkit.getWorld("PixelWorldPro/cache/world/${worldData.type}/${worldData.id}/world")!!
            val mainWorld = Bukkit.getWorld("world")!!
            for (player in localWorld.players) {
                player.teleport(mainWorld.spawnLocation)
            }
            Bukkit.unloadWorld(localWorld, true)
            WorldCache.setUnUseWorld(world)
            WorldImpl.loadWorld.remove(worldData.id)
            if (bungee) {
                val bungeeData = world.getDataConfig("bungee")
                bungeeData.set("load.server", null)
                bungeeData.saveToFile()
            }
            future.complete(ResultData(
                true,
                ""
            ))
        } else {
            future.complete(ResultData(
                false,
                ""
            ))
        }
        return future
    }

    /**
     * 传送
     */
  override fun teleport(player: Player, world: PixelWorldProWorld) {
      val worldData = world.worldData
        if (!isLoad(world).get()) {
            load(world).get()
        }
        val localWorld = Bukkit.getWorld("PixelWorldPro/cache/world/${worldData.type}/${worldData.id}/world")
        if (localWorld != null) {
            val worldDataConfig = world.getDataConfig("world")
            val location = if (worldDataConfig.getConfigurationSection("location") != null) {
                LocationData(
                    worldDataConfig.getDouble("location.x"),
                    worldDataConfig.getDouble("location.y"),
                    worldDataConfig.getDouble("location.z")
                )
            } else {
                null
            }
            if (location != null) {
                player.teleport(
                    Location(localWorld,location.x, location.y, location.z)
                )
            } else {
                player.teleport(localWorld.spawnLocation)
            }
        }
    }
}