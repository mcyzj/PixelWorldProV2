package com.mcyzj.pixelworldpro.world

import com.mcyzj.pixelworldpro.PixelWorldPro
import com.mcyzj.pixelworldpro.api.interfaces.core.permission.PermissionAPI
import com.mcyzj.pixelworldpro.api.interfaces.core.world.WorldAPI
import com.mcyzj.pixelworldpro.compress.None
import com.mcyzj.pixelworldpro.compress.SevenZip
import com.mcyzj.pixelworldpro.compress.Zip
import com.mcyzj.pixelworldpro.file.Config
import com.mcyzj.pixelworldpro.data.dataclass.WorldCreateData
import com.mcyzj.pixelworldpro.data.dataclass.WorldData
import com.mcyzj.pixelworldpro.expansion.listener.trigger.world.WorldSuccess
import com.mcyzj.pixelworldpro.server.World
import com.mcyzj.pixelworldpro.server.World.localWorld
import com.xbaimiao.easylib.module.utils.submit
import org.bukkit.Bukkit
import org.bukkit.WorldCreator
import org.bukkit.configuration.file.YamlConfiguration
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.lang.Thread.sleep
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.collections.ArrayList


object WorldImpl : WorldAPI {
    private val logger = PixelWorldPro.instance.logger
    private val lang = PixelWorldPro.instance.lang
    private var config = Config.config
    private val database = PixelWorldPro.databaseApi
    private var fileConfig = Config.file
    private var worldConfig = Config.world
    private var bungee = Config.bungee

    private val asyncLoad = config.getBoolean("async.world.load")
    private val burialWorld = ArrayList<WorldData>()

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
            File(fileConfig.getString("Template.Path"), template).copyRecursively(
                File(
                    fileConfig.getString("World.Server"),
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
                PermissionAPI.Factory.get().getConfigWorldPermission(),
                HashMap<UUID, String>(),
                HashMap<String, Boolean>()
            )
            val worldData = database.createWorldData(worldCreateData)
            localWorld[worldData.id] = world
            World.setLock(worldData.id)
            submit {
                WorldSuccess.createWorldSuccess(worldData, template)
            }
            future.complete(true)
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
                logger.warning("$id ${lang.getString("world.warning.load.noWorldData")}")
                future.complete(false)
                return@submit
            }
            if (worldData in burialWorld){
                logger.warning("$id ${lang.getString("world.warning.load.inBurial")}")
                future.complete(false)
                return@submit
            }
            //解压世界数据
            unzipWorld(worldData.world, worldData.world)
            //加载世界
            val worldCreator = WorldCreator("PixelWorldPro/${worldData.world}/world")
            val world = Bukkit.createWorld(worldCreator)
            if (world == null) {
                future.complete(false)
                return@submit
            }
            localWorld[worldData.id] = world
            World.setLock(worldData.id)
            world.keepSpawnInMemory = false
            submit {
                WorldSuccess.loadWorldSuccess(worldData)
            }
            future.complete(true)
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
            if (worldData in burialWorld){
                logger.warning("$owner ${lang.getString("world.warning.load.inBurial")}")
                future.complete(false)
                return@submit
            }
            //解压世界数据
            unzipWorld(worldData.world, worldData.world)
            //加载世界
            val worldCreator = WorldCreator("PixelWorldPro/${worldData.world}/world")
            val world = Bukkit.createWorld(worldCreator)
            if (world == null) {
                future.complete(false)
                return@submit
            }
            localWorld[worldData.id] = world
            World.setLock(worldData.id)
            world.keepSpawnInMemory = false
            submit {
                WorldSuccess.loadWorldSuccess(worldData)
            }
            future.complete(true)
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
                World.removeLock(worldData.id)
                if (bungee.getBoolean("Enable")){
                    com.mcyzj.pixelworldpro.bungee.System.removeWorldLock(worldData)
                }
                submit {
                    WorldSuccess.unloadWorldSuccess(worldData)
                }
                Thread{
                    burialWorld.add(worldData)
                    World.setDeleteLock(File(fileConfig.getString("World.Server"), worldData.world).path)
                    sleep(30000)
                    zipWorld(worldData.world, worldData.world)
                    checkBackUp(worldData)
                    File(fileConfig.getString("World.Server"), worldData.world).deleteRecursively()
                    World.removeDeleteLock(File(fileConfig.getString("World.Server"), worldData.world).path)
                    burialWorld.remove(worldData)
                }.start()
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
                World.removeLock(worldData.id)
                if (bungee.getBoolean("Enable")){
                    com.mcyzj.pixelworldpro.bungee.System.removeWorldLock(worldData)
                }
                submit {
                    WorldSuccess.unloadWorldSuccess(worldData)
                }
                Thread{
                    burialWorld.add(worldData)
                    World.setDeleteLock(File(fileConfig.getString("World.Server"), worldData.world).path)
                    sleep(30000)
                    zipWorld(worldData.world, worldData.world)
                    checkBackUp(worldData)
                    File(fileConfig.getString("World.Server"), worldData.world).deleteRecursively()
                    World.removeDeleteLock(File(fileConfig.getString("World.Server"), worldData.world).path)
                    burialWorld.remove(worldData)
                }.start()
                future.complete(true)
            }else{
                future.complete(false)
            }
        }
        return future
    }

    override fun backupWorld(id: Int, save: Boolean?) {
        val isSave = save ?: true
        //拉取世界数据
        val worldData = database.getWorldData(id)
        if (worldData == null){
            logger.warning("§aPixelWorldPro $id ${lang.getString("world.warning.unload.noWorldData")}")
            return
        }
        //备份世界文件
        val world = localWorld[worldData.id]
        if (world == null) {
            localWorld.remove(worldData.id)
            return
        }
        val future = CompletableFuture<Boolean>()
        submit {
            if (isSave) {
                world.save()
            }
            future.complete(true)
        }
        future.thenApply {
            zipWorld(worldData.world, worldData.world)
            checkBackUp(worldData)
            WorldSuccess.backupWorldSuccess(worldData, save)
        }
    }

    override fun backupWorld(owner: UUID, save: Boolean?) {
        val isSave = save ?: true
        //拉取世界数据
        val worldData = database.getWorldData(owner)
        if (worldData == null){
            logger.warning("§aPixelWorldPro $owner ${lang.getString("world.warning.unload.noWorldData")}")
            return
        }
        //备份世界文件
        val world = localWorld[worldData.id]
        if (world == null) {
            localWorld.remove(worldData.id)
            return
        }
        val future = CompletableFuture<Boolean>()
        submit {
            if (isSave) {
                world.save()
            }
            future.complete(true)
        }
        future.thenApply {
            zipWorld(worldData.world, worldData.world)
            checkBackUp(worldData)
            WorldSuccess.backupWorldSuccess(worldData, save)
        }
    }

    private fun checkBackUp(worldData: WorldData){
        val time = System.currentTimeMillis()
        val date = Date(time)
        //把time时间格式化
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")
        //把time时间格式化为字符串
        val timeString = formatter.format(date)
        val file = File(fileConfig.getString("World.Path")!!, worldData.world)
        val backup = File(file, "backup/$timeString")
        backup.mkdirs()
        for (backupFile in file.listFiles()!!){
            if (backupFile.name == "backup"){
                continue
            }
            if (backupFile.isFile) {
                val newFile = File(backup, backupFile.name)
                backupFile.copyTo(newFile)
            } else {
                File(backup, backupFile.name).mkdirs()
                backupFile.copyRecursively(File(backup, backupFile.name))
            }
        }
        //备份数据文件
        val json = File(backup, "database.json")
        json.createNewFile()
        val fileWriter = FileWriter(json.absoluteFile, false)
        val bw = BufferedWriter(fileWriter)
        bw.write(database.joinToJson(worldData).toString())
        bw.close()
        //删除过期的备份
        val backupFile = File(file, "backup")
        if (fileConfig.getInt("Backup.number") < (backupFile.listFiles()?.size ?: 1)) {
            for (files in backupFile.listFiles()!!) {
                files.deleteRecursively()
                if (fileConfig.getInt("Backup.number") >= (backupFile.listFiles()?.size ?: 1)) {
                    break
                }
            }
        }
    }

    override fun zipWorld(from: String, to: String) {
        val data = getWorldConfig(to)
        data.set("Zip", fileConfig.getString("World.Compress.Method"))
        saveWorldConfig(to, data)
        when (fileConfig.getString("World.Compress.Method")){
            "None" -> {
                None.toZip(from, to)
            }

            "Zip" -> {
                Zip.toZip(from, to)
            }

            "7z" -> {
                SevenZip.toZip(from, to)
            }
        }
    }

    override fun unzipWorld(zip: String, to: String) {
        when (getWorldConfig(to).getString("Zip") ?: fileConfig.getString("World.Compress.Method")!!){
            "None" -> {
                None.unZip(zip, to)
            }

            "Zip" -> {
                Zip.unZip(zip, to)
            }

            "7z" -> {
                SevenZip.unZip(zip, to)
            }
        }
    }
    private fun getWorldConfig(world: String): YamlConfiguration {
        val file = File(fileConfig.getString("World.Path")!!, "$world/World.yml")
        if (!File(fileConfig.getString("World.Path")!!, world).exists()){
            File(fileConfig.getString("World.Path")!!, world).mkdirs()
        }
        if (!file.exists()){
            file.createNewFile()
        }
        val data = YamlConfiguration()
        data.load(file)
        return data
    }

    private fun saveWorldConfig(world: String, data: YamlConfiguration){
        val file = File(fileConfig.getString("World.Path")!!, "$world/World.yml")
        if (!file.exists()){
            file.createNewFile()
        }
        data.save(file)
    }
}