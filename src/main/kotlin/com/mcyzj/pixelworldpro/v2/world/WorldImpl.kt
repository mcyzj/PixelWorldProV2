package com.mcyzj.pixelworldpro.v2.world

import com.mcyzj.pixelworldpro.v2.PixelWorldPro
import com.mcyzj.pixelworldpro.v2.util.Config
import com.mcyzj.pixelworldpro.v2.world.compress.None
import com.mcyzj.pixelworldpro.v2.world.compress.Zip
import com.mcyzj.pixelworldpro.v2.world.dataclass.WorldCreateData
import com.mcyzj.pixelworldpro.v2.world.dataclass.WorldData
import com.xbaimiao.easylib.bridge.replacePlaceholder
import com.xbaimiao.easylib.module.utils.colored
import com.xbaimiao.easylib.module.utils.submit
import org.bukkit.Bukkit
import java.io.File
import java.util.*
import java.util.concurrent.CompletableFuture

class WorldImpl: WorldAPI{
    private val worldConfig = Config.world
    private val config = Config.config

    private val lang = Config.getLang()
    private val log = PixelWorldPro.instance.log

    private val asyncLoad = config.getBoolean("async.worldLoad")
    override fun createWorld(owner: UUID, template: String): CompletableFuture<Boolean> {
        TODO("Not yet implemented")
    }

    override fun loadWorld(id: Int): CompletableFuture<Boolean> {
        TODO("Not yet implemented")
    }

    override fun unloadWorld(id: Int): CompletableFuture<Boolean> {
        TODO("Not yet implemented")
    }

    override fun backupWorld(id: Int, save: Boolean?) {
        TODO("Not yet implemented")
    }

    override fun zipWorld(world: PixelWorldProWorld) {
        //开启压缩
        world.compress()
    }

    override fun unzipWorld(world: PixelWorldProWorld) {
        world.decompression()
    }

    override fun restoreBackup(worldData: WorldData, backup: File): CompletableFuture<Boolean> {
        TODO("Not yet implemented")
    }

}
