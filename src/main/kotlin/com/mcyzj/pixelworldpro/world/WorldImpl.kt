package com.mcyzj.pixelworldpro.world

import com.mcyzj.pixelworldpro.PixelWorldPro
import com.mcyzj.pixelworldpro.api.interfaces.Permission
import com.mcyzj.pixelworldpro.api.interfaces.WorldAPI
import com.mcyzj.pixelworldpro.compress.Zip
import com.mcyzj.pixelworldpro.config.Config
import com.mcyzj.pixelworldpro.dataclass.WorldCreateData
import com.mcyzj.pixelworldpro.server.World
import com.mcyzj.pixelworldpro.server.World.localWorld
import com.xbaimiao.easylib.module.chat.BuiltInConfiguration
import com.xbaimiao.easylib.module.utils.submit
import org.bukkit.Bukkit
import org.bukkit.WorldCreator
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CompletableFuture

object WorldImpl : WorldAPI {
    private val logger = PixelWorldPro.instance.logger
    private val lang = PixelWorldPro.instance.lang
    private var config = Config.config
    private val database = PixelWorldPro.databaseApi
    private var file = Config.file
    private var worldConfig = Config.world

    private val asyncLoad = config.getBoolean("async.world.load")
    override fun createWorld(owner: UUID, template: String): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        submit(async = asyncLoad) {
            logger.info("§aPixelWorldPro 使用线程：${Thread.currentThread().name} 进行世界创建操作")
            //检查模板文件
            if (!World.checkTemplate(template)) {
                future.complete(false)
                return@submit
            }
            //获取time时间
            val time = System.currentTimeMillis()
            val date = Date(time)
            //把time时间格式化
            val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")
            //把time时间格式化为字符串
            val timeString = formatter.format(date)
            //获取路径下对应的world文件夹
            val worldName = "${owner}_$timeString"
            //复制模板文件
            File(file.getString("Template.Path"), template).copyRecursively(
                File(
                    file.getString("World.Server"),
                    worldName
                )
            )
            //加载世界
            val worldCreator = WorldCreator("PixelWorldPro/$worldName/world")
            val world = Bukkit.createWorld(worldCreator)
            if (world == null) {
                future.complete(false)
                return@submit
            }
            val worldCreateData = WorldCreateData(
                owner,
                owner.toString(),
                worldName,
                Permission.Factory.get().getConfigWorldPermission(),
                HashMap<UUID, String>(),
                HashMap<String, Boolean>()
            )
            val worldData = database.createWorldData(worldCreateData)
            localWorld[worldData.id] = world
        }
        return future
    }

    override fun loadWorld(id: Int): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        submit(async = asyncLoad) {
            logger.info("§aPixelWorldPro 使用线程：${Thread.currentThread().name} 进行世界加载操作")
            //拉取世界数据
            val worldData = database.getWorldData(id)
            if (worldData == null){
                logger.warning("§aPixelWorldPro $id ${lang.getString("world.warning.load.noWorldData")}")
                future.complete(false)
                return@submit
            }
            //解压世界数据
            Zip.unzip(worldData.world, worldData.world)
            //加载世界
            val worldCreator = WorldCreator("PixelWorldPro/${worldData.world}/world")
            val world = Bukkit.createWorld(worldCreator)
            if (world == null) {
                future.complete(false)
                return@submit
            }
            localWorld[worldData.id] = world
        }
        return future
    }

    override fun loadWorld(owner: UUID): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        submit(async = asyncLoad) {
            logger.info("§aPixelWorldPro 使用线程：${Thread.currentThread().name} 进行世界加载操作")
            //拉取世界数据
            val worldData = database.getWorldData(owner)
            if (worldData == null){
                logger.warning("§aPixelWorldPro $owner ${lang.getString("world.warning.load.noWorldData")}")
                future.complete(false)
                return@submit
            }
            //解压世界数据
            Zip.unzip(worldData.world, worldData.world)
            //加载世界
            val worldCreator = WorldCreator("PixelWorldPro/${worldData.world}/world")
            val world = Bukkit.createWorld(worldCreator)
            if (world == null) {
                future.complete(false)
                return@submit
            }
            localWorld[worldData.id] = world
        }
        return future
    }

    override fun unloadWorld(id: Int): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        submit(async = asyncLoad) {
            logger.info("§aPixelWorldPro 使用线程：${Thread.currentThread().name} 进行世界卸载操作")
            //拉取世界数据
            val worldData = database.getWorldData(id)
            if (worldData == null){
                logger.warning("§aPixelWorldPro $id ${lang.getString("world.warning.unload.noWorldData")}")
                future.complete(false)
                return@submit
            }
            //获取世界
            val world = localWorld[worldData.id]
            if (world == null) {
                localWorld.remove(worldData.id)
                future.complete(true)
                return@submit
            }
            for (player in world.players){
                player.teleport(Bukkit.getWorld(worldConfig.getString("Unload.world")?: "world")!!.spawnLocation)
            }
            if (Bukkit.unloadWorld(world, true)){
                localWorld.remove(worldData.id)
                Zip.toZip(worldData.world, worldData.world)
                File(file.getString("World.Server"), worldData.world).deleteRecursively()
                future.complete(true)
            }else{
                future.complete(false)
            }
        }
        return future
    }

    override fun unloadWorld(owner: UUID): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        submit(async = asyncLoad) {
            logger.info("§aPixelWorldPro 使用线程：${Thread.currentThread().name} 进行世界卸载操作")
            //拉取世界数据
            val worldData = database.getWorldData(owner)
            if (worldData == null){
                logger.warning("§aPixelWorldPro $owner ${lang.getString("world.warning.unload.noWorldData")}")
                future.complete(false)
                return@submit
            }
            //获取世界
            val world = localWorld[worldData.id]
            if (world == null) {
                localWorld.remove(worldData.id)
                future.complete(true)
                return@submit
            }
            for (player in world.players){
                player.teleport(Bukkit.getWorld(worldConfig.getString("Unload.world")?: "world")!!.spawnLocation)
            }
            if (Bukkit.unloadWorld(world, true)){
                localWorld.remove(worldData.id)
                Zip.toZip(worldData.world, worldData.world)
                File(file.getString("World.Server"), worldData.world).deleteRecursively()
                future.complete(true)
            }else{
                future.complete(false)
            }
        }
        return future
    }
}