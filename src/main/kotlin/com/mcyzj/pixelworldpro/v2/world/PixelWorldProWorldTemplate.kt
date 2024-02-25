package com.mcyzj.pixelworldpro.v2.world

import com.mcyzj.lib.plugin.file.BuiltOutConfiguration
import com.mcyzj.pixelworldpro.v2.PixelWorldPro
import com.mcyzj.pixelworldpro.v2.permission.PermissionImpl
import com.mcyzj.pixelworldpro.v2.util.Config
import com.mcyzj.pixelworldpro.v2.world.dataclass.LocationData
import com.mcyzj.pixelworldpro.v2.world.dataclass.WorldCreateData
import com.xbaimiao.easylib.bridge.replacePlaceholder
import com.xbaimiao.easylib.module.utils.colored
import org.bukkit.Bukkit
import java.io.File
import java.util.UUID

/**
 * 创建PixelWorldPro世界
 * @return: PixelWorldProWorld
 */
@Suppress("unused")
class PixelWorldProWorldTemplate(template: String) {
    val templateConfig = BuiltOutConfiguration("./PixelWorldPro/template/$template/template.yml")
    private val templateFile = File("./PixelWorldPro/template/$template")
    private val log = PixelWorldPro.instance.log
    private val lang = Config.getLang()
    private val worldConfig = Config.world
    /**
     * 世界生成器
     */
    var worldCreator: String

    /**
     * 世界规则
     */
    val gameRule: HashMap<String, String>

    /**
     * 世界出生点
     */
    var location: LocationData?

    /**
     * 世界种子
     */
    var seed: Long? = null
    init {
        worldCreator = templateConfig.getString("creator") ?: "auto"
        gameRule = HashMap()
        val ruleConfig = templateConfig.getConfigurationSection("gameRule")
        if (ruleConfig != null) {
            for (key in ruleConfig.getKeys(false)){
                gameRule[key] = ruleConfig.getString(key)!!
            }
        }
        location = if (templateConfig.getConfigurationSection("location") != null){
            try {
                LocationData(
                    templateConfig.getConfigurationSection("location")!!.getDouble("x"),
                    templateConfig.getConfigurationSection("location")!!.getDouble("y"),
                    templateConfig.getConfigurationSection("location")!!.getDouble("z")
                )
            } catch (_:Exception) {
                null
            }
        } else {
            null
        }
    }

    fun createWorld(owner: UUID): PixelWorldProWorld {
        log.info(lang.getString("world.load")!!.replace("[0]", Thread.currentThread().name))
        val offlinePlayer = Bukkit.getOfflinePlayer(owner)
        val name = (worldConfig.getString("create.name") ?: "[uuid]的世界").replacePlaceholder(offlinePlayer).colored()
        val worldData = PixelWorldPro.databaseApi.createWorldData(
            WorldCreateData(
                owner = owner,
                name = name,
                permission = PermissionImpl.getConfigWorldPermission(),
                player = HashMap()
            )
        )
        val worldDataFile = File("./PixelWorldPro/world/${worldData.id}")
        if (worldDataFile.exists()) {
            worldDataFile.deleteRecursively()
        }
        templateFile.copyRecursively(worldDataFile)
        val pwpWorld = PixelWorldProWorld(worldData)
        val worldDataConfig = pwpWorld.getDataConfig("world")
        if (seed != null) {
            worldDataConfig.set("seed", seed)
        }
        worldDataConfig.set("worldCreator", worldCreator)
        if (location != null) {
            worldDataConfig.set("location.x", location!!.x)
            worldDataConfig.set("location.y", location!!.y)
            worldDataConfig.set("location.z", location!!.z)
        }
        if (gameRule.isNotEmpty()) {
            for (key in gameRule.keys) {
                worldDataConfig.set("template.gameRule.$key", gameRule[key])
            }
        }
        worldDataConfig.saveToFile()
        val compress = templateConfig.getString("compress.method") ?: "None"
        val compressConfig = pwpWorld.getDataConfig("compress")
        compressConfig.set("method", compress)
        compressConfig.saveToFile()
        return pwpWorld
    }
}