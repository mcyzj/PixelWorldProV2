package com.mcyzj.pixelworldpro.v2.world

import com.mcyzj.lib.plugin.PlayerFound
import com.mcyzj.pixelworldpro.v2.PixelWorldPro
import com.mcyzj.pixelworldpro.v2.permission.dataclass.ResultData
import com.mcyzj.pixelworldpro.v2.util.Config
import com.xbaimiao.easylib.bridge.economy.PlayerPoints
import com.xbaimiao.easylib.bridge.economy.Vault
import com.xbaimiao.easylib.module.item.hasItem
import com.xbaimiao.easylib.module.item.takeItem
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.io.File
import java.util.*

object LocalWorld {
    private val worldConfig = Config.world
    private val lang = Config.getLang()

    private val createList = ArrayList<UUID>()
    @Suppress("DEPRECATION")
    fun createWorld(owner: Player, template: String?, seed: Long?): ResultData {
        if (owner.uniqueId in createList) {
            val msg = lang.getString("check.notEnough.request") ?: "短时间内不得多次提交请求"
            return ResultData(
                false,
                msg
            )
        } else {
            createList.add(owner.uniqueId)
        }
        val templates = if (template == null) {
            val templateFileList = File("./PixelWorldPro/template").listFiles()
            if (templateFileList == null) {
                val msg = lang.getString("check.template.empty") ?: "没有模板"
                createList.remove(owner.uniqueId)
                return ResultData(
                    false,
                    msg
                )
            }
            templateFileList[(Math.random() * templateFileList.size).toInt()].name
        } else {
            if (!File("./PixelWorldPro/template/$template").exists()) {
                val msg = lang.getString("check.template.notFind") ?: "没有找到模板"
                createList.remove(owner.uniqueId)
                return ResultData(
                    false,
                    msg
                )
            }
            template
        }
        val templateData = PixelWorldProWorldTemplate(templates)
        val templateConfig = templateData.templateConfig
        val group = if (templateConfig.getConfigurationSection("use") != null) {
            createUse(owner, templateConfig.getConfigurationSection("use")!!)
        } else {
            createUse(owner, worldConfig.getConfigurationSection("create.use")!!)
        }
        val points = group.getDouble("points")
        if (points > 0.0) {
            if (!PlayerPoints().has(owner, points)) {
                var msg = lang.getString("check.notEnough.points") ?: "没有足够的点券，你需要{member}个点券"
                msg = msg.replace("{member}", points.toString())
                createList.remove(owner.uniqueId)
                return ResultData(
                    false,
                    msg
                )
            }
        }
        val money = group.getDouble("money")
        if (money > 0.0) {
            if (!Vault().has(owner, money)) {
                var msg = lang.getString("check.notEnough.money") ?: "没有足够的金币，你需要{member}个金币"
                msg = msg.replace("{member}", money.toString())
                createList.remove(owner.uniqueId)
                return ResultData(
                    false,
                    msg
                )
            }
        }
        //检验物品
        val itemConfig = group.getConfigurationSection("item")
        if (itemConfig != null) {
            val itemMap = Config.buildItemMap(worldConfig.getConfigurationSection("item")!!)
            for (key in itemConfig.getKeys(false)) {
                val itemData = itemMap[key]!!
                val material = Material.getMaterial(itemData.material)!!
                val item = ItemStack(material)
                for (lore in itemData.lore) {
                    item.lore?.add(lore)
                }
                if (!owner.inventory.hasItem(item, itemConfig.getInt(key))) {
                    var msg = lang.getString("check.notEnough.item") ?: "没有足够的物品，你需要{member}个{item}"
                    msg = msg.replace("{member}", itemConfig.getInt(key).toString())
                    msg = msg.replace("{item}", material.name)
                    createList.remove(owner.uniqueId)
                    return ResultData(
                        false,
                        msg
                    )
                }
            }
            //拿走物品
            for (key in itemConfig.getKeys(false)) {
                val itemData = itemMap[key]!!
                owner.inventory.takeItem(itemConfig.getInt(key)) {
                    return@takeItem this.type == Material.getMaterial(itemData.material)!!
                }
            }
        }
        createWorldLocal(owner.uniqueId, templates, seed)
        val msg = lang.getString("world.createSuccess") ?: "成功创建世界"
        createList.remove(owner.uniqueId)
        return ResultData(
            true,
            msg
        )
    }

    private fun createUse(player: Player, config: ConfigurationSection): ConfigurationSection {
        val groupList = config.getKeys(false) as ArrayList<*>
        groupList.remove("default")
        for (key in groupList) {
            val group = config.getConfigurationSection(key.toString())!!
            val permission = group.getString("permission") ?: continue
            if (player.hasPermission(permission)) {
                return group
            }
        }
        return config.getConfigurationSection("default")!!
    }

    fun createWorldLocal(owner: UUID, template: String?, seed: Long?) {
        val templates = if (template == null) {
            val templateFileList = File("./PixelWorldPro/template").listFiles()!!
            templateFileList[(Math.random() * templateFileList.size).toInt()].name
        } else {
            template
        }
        val templateData = PixelWorldProWorldTemplate(templates)
        templateData.seed = seed
        val world = templateData.createWorld(owner)
        world.load()
        val player = Bukkit.getPlayer(owner)
        if (player != null) {
            world.teleport(player)
        }
    }

    fun teleport(value: String, player: Player): ResultData {
        try {
            val id = value.toInt()
        } catch (_:Exception) {
            val owner = PlayerFound.getOfflinePlayer(value)
            val worldData = PixelWorldPro.databaseApi.getWorldData(owner.uniqueId)
            if (worldData == null) {
                val msg = lang.getString("teleport.notFound") ?: "没有找到世界"
                return ResultData(
                    false,
                    msg
                )
            }
            return teleport(PixelWorldProWorld(worldData), player)
        }
        return teleport(value.toInt(), player)
    }

    fun teleport(id: Int, player: Player): ResultData {
        val worldData = PixelWorldPro.databaseApi.getWorldData(id)
        if (worldData == null) {
            val msg = lang.getString("teleport.notFound") ?: "没有找到世界"
            return ResultData(
                false,
                msg
            )
        }
        return teleport(PixelWorldProWorld(worldData), player)
    }

    fun teleport(world: PixelWorldProWorld, player: Player): ResultData {
        val worldData = world.worldData
        val group = worldData.player[player.uniqueId] ?: "visitor"
        val permission = worldData.permission[group]!!
        if (permission["teleport"] == "false"){
            val msg = lang.getString("teleport.noPermission") ?: "没有权限进入该世界"
            return ResultData(
                false,
                msg
            )
        }
        if ((!world.isLoad()).and(permission["blockRightClick"] == "false")) {
            val msg = lang.getString("teleport.notLoad") ?: "世界还未加载"
            return ResultData(
                false,
                msg
            )
        }
        world.teleport(player)
        val msg = lang.getString("teleport.successful") ?: "传送中"
        return ResultData(
            false,
            msg
        )
    }
}