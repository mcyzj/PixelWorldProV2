package com.mcyzj.pixelworldpro.world

import com.mcyzj.pixelworldpro.PixelWorldPro
import com.mcyzj.pixelworldpro.api.interfaces.core.world.DimensionAPI
import com.mcyzj.pixelworldpro.data.dataclass.ResultData
import com.mcyzj.pixelworldpro.data.dataclass.WorldData
import com.mcyzj.pixelworldpro.data.dataclass.WorldDimensionData
import com.mcyzj.pixelworldpro.file.Config
import com.xbaimiao.easylib.module.utils.submit
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.WorldType
import java.util.concurrent.CompletableFuture

object DimensionImpl : DimensionAPI {
    private var config = Config.config
    private var dimensionConfig = Config.dimension
    private val asyncLoad = config.getBoolean("async.world.load")

    private val lang = PixelWorldPro.instance.lang
    private val database = PixelWorldPro.databaseApi
    override fun createDimension(worldData: WorldData, dimension: String): CompletableFuture<ResultData> {
        val future = CompletableFuture<ResultData>()
        submit(async = asyncLoad) {
            //检查维度是否已经创建了
            val worldDimension = worldData.dimension
            if (worldDimension[dimension] != null){
                val reason = lang.getString("world.warning.dimension.create.hasCreate") ?: "无法创建维度：当前世界中该维度已经创建了"
                val resultData = ResultData(false, reason)
                future.complete(resultData)
                return@submit
            }
            //检查配置文件中是否有对应维度的配置
            val dimensionConfig = dimensionConfig.getConfigurationSection("Dimension.$dimension")
            if (dimensionConfig == null){
                val reason = lang.getString("world.warning.dimension.create.noConfigData") ?: "无法创建维度：配置文件中没有对应维度"
                val resultData = ResultData(false, reason)
                future.complete(resultData)
                return@submit
            }
            //配置维度数据
            val environment = dimensionConfig.getString("Environment")!!
            val type = dimensionConfig.getString("Type")!!
            val creator = dimensionConfig.getString("Creator")
            val worldDimensionData = WorldDimensionData(
                dimensionConfig.getString("Name")!!,
                dimension,
                environment,
                type,
                creator
            )
            //保存维度数据
            worldDimension[dimension] = worldDimensionData
            worldData.dimension = worldDimension
            //写入数据库
            database.setWorldData(worldData)
            //加载维度
            val worldCreator = WorldCreator("PixelWorldPro/${worldData.world}/$dimension")
            //设置维度类型
            when (environment){
                "Nether" -> {
                    worldCreator.environment(World.Environment.NETHER)
                    worldCreator.generateStructures(true)
                }
                "The_End" -> {
                    worldCreator.environment(World.Environment.THE_END)
                    worldCreator.generateStructures(true)
                }
                "NORMAL" -> {
                    worldCreator.environment(World.Environment.NORMAL)
                    worldCreator.generateStructures(true)
                }
                "Custom" -> {
                    worldCreator.environment(World.Environment.CUSTOM)
                    worldCreator.generateStructures(true)
                }
            }
            when (type){
                "Normal" -> {
                    worldCreator.type(WorldType.NORMAL)
                    worldCreator.generateStructures(true)
                }
                "Amplified" -> {
                    worldCreator.type(WorldType.AMPLIFIED)
                    worldCreator.generateStructures(true)
                }
                "Flat" -> {
                    worldCreator.type(WorldType.FLAT)
                    worldCreator.generateStructures(true)
                }
                "Large_Biomes" -> {
                    worldCreator.type(WorldType.LARGE_BIOMES)
                    worldCreator.generateStructures(true)
                }
            }
            //设置自定义世界生成器
            if (creator != null){
                worldCreator.generator(creator)
                worldCreator.generateStructures(true)
            }
            //进行world正式加载
            worldCreator.createWorld()
            //完成维度创建
            val reason = lang.getString("world.info.dimension.create.success") ?: "成功创建维度"
            val resultData = ResultData(false, reason)
            future.complete(resultData)
        }
        return future
    }

    override fun loadDimension(worldData: WorldData, dimension: String): CompletableFuture<ResultData> {
        val future = CompletableFuture<ResultData>()
        submit(async = asyncLoad) {
            //检查维度是否创建
            val worldDimension = worldData.dimension[dimension]
            if (worldDimension == null){
                val reason = lang.getString("world.warning.dimension.create.hasCreate") ?: "无法创建维度：当前世界中该维度已经创建了"
                val resultData = ResultData(false, reason)
                future.complete(resultData)
                return@submit
            }
            val environment = worldDimension.environment
            val type = worldDimension.type
            val creator = worldDimension.creator
            //加载维度
            val worldCreator = WorldCreator("PixelWorldPro/${worldData.world}/$dimension")
            //设置维度类型
            when (environment){
                "Nether" -> {
                    worldCreator.environment(World.Environment.NETHER)
                    worldCreator.generateStructures(true)
                }
                "The_End" -> {
                    worldCreator.environment(World.Environment.THE_END)
                    worldCreator.generateStructures(true)
                }
                "NORMAL" -> {
                    worldCreator.environment(World.Environment.NORMAL)
                    worldCreator.generateStructures(true)
                }
                "Custom" -> {
                    worldCreator.environment(World.Environment.CUSTOM)
                    worldCreator.generateStructures(true)
                }
            }
            when (type){
                "Normal" -> {
                    worldCreator.type(WorldType.NORMAL)
                    worldCreator.generateStructures(true)
                }
                "Amplified" -> {
                    worldCreator.type(WorldType.AMPLIFIED)
                    worldCreator.generateStructures(true)
                }
                "Flat" -> {
                    worldCreator.type(WorldType.FLAT)
                    worldCreator.generateStructures(true)
                }
                "Large_Biomes" -> {
                    worldCreator.type(WorldType.LARGE_BIOMES)
                    worldCreator.generateStructures(true)
                }
            }
            //设置自定义世界生成器
            if (creator != null){
                worldCreator.generator(creator)
                worldCreator.generateStructures(true)
            }
            //进行world正式加载
            worldCreator.createWorld()
            //完成维度创建
            val reason = lang.getString("world.info.dimension.create.success") ?: "成功创建维度"
            val resultData = ResultData(false, reason)
            future.complete(resultData)
        }
        return future
    }

    override fun unloadDimension(worldData: WorldData, dimension: String): CompletableFuture<ResultData> {
        TODO("Not yet implemented")
    }
}