package com.mcyzj.pixelworldpro.v2.core.world

import com.mcyzj.lib.plugin.PlayerFound
import com.mcyzj.pixelworldpro.v2.core.bungee.BungeeWorld
import com.mcyzj.pixelworldpro.v2.core.permission.dataclass.ResultData
import com.mcyzj.pixelworldpro.v2.core.util.Config
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
import java.lang.Thread.sleep
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object LocalWorld {
    private val worldConfig = Config.world
    private val lang = Config.getLang()
    private val bungeeConfig = Config.bungee

    private val createList = ArrayList<UUID>()

    val loadWorld = HashMap<Int, PixelWorldProWorld>()
    val onlinePlayer = HashMap<Int, ArrayList<Player>>()
    var worldTickets = HashMap<Int, Double>()
    var serverTickets = 0.0
    private val unloadMap = HashMap<Int, Thread>()

    var worldUpdateThread: Thread? = null

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
        try {
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
            if (bungeeConfig.getBoolean("enable")) {
                BungeeWorld.createWorld(owner.uniqueId, template, seed)
            } else {
                createWorldLocal(owner.uniqueId, templates, seed)
            }
            val msg = lang.getString("world.createSuccess") ?: "成功创建世界"
            createList.remove(owner.uniqueId)
            return ResultData(
                true,
                msg
            )
        } catch (e: Exception) {
            createList.remove(owner.uniqueId)
            throw e
        }
    }

    private fun createUse(player: Player, config: ConfigurationSection): ConfigurationSection {
        val groupList = config.getKeys(false)
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
            value.toInt()
        } catch (_:Exception) {
            val owner = PlayerFound.getOfflinePlayer(value)
            val worldData = com.mcyzj.pixelworldpro.v2.core.PixelWorldPro.databaseApi.getWorldData(owner.uniqueId)
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
        val worldData = com.mcyzj.pixelworldpro.v2.core.PixelWorldPro.databaseApi.getWorldData(id)
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

    fun getWorldID(worldName: String): Int? {
        if (!worldName.startsWith("PixelWorldPro")) {
            return null
        }
        val realNameList = worldName.split("/").size
        if (realNameList < 2) {
            return null
        }
        val realName = worldName.split("/")[realNameList - 2]
        return try{
            realName.toInt()
        }catch (_:Exception){
            null
        }
    }

    @Suppress("DEPRECATION")
    fun updateAllWorlds() {
        worldUpdateThread?.stop()
        worldUpdateThread = Thread{
            while (true) {
                var newServerTickets = 0.0
                val newWorldTickets = HashMap<Int, Double>()
                for (world in loadWorld.values) {
                    val tickets = world.tickets()
                    newServerTickets += tickets
                    newWorldTickets[world.worldData.id] = tickets
                    updateWorldPlayer(world)
                }
                serverTickets = newServerTickets
                worldTickets = newWorldTickets
                if (Config.bungee.getBoolean("enable")) {
                    BungeeWorld.saveServerData()
                }
                sleep(5*60*1000)
            }
        }
        worldUpdateThread!!.start()
    }

    @Suppress("DEPRECATION")
    fun updateWorldPlayer(world: PixelWorldProWorld) {
        val worldMap = world.getWorlds()
        val playerList = ArrayList<Player>()
        for (worlds in worldMap.values) {
            for (player in worlds.players) {
                playerList.add(player)
            }
        }
        if (playerList.isNotEmpty()) {
            onlinePlayer[world.worldData.id] = playerList
            val thread = unloadMap[world.worldData.id]
            if (thread != null) {
                Thread {
                    thread.stop()
                    unloadMap.remove(world.worldData.id)
                }.start()
            }
        } else {
            checkUnload(world)
        }
    }

    private fun checkUnload(world: PixelWorldProWorld) {
        val thread = unloadMap[world.worldData.id]
        if (thread == null) {
            Thread {
                val maxTime = worldConfig.getInt("unload.wait.time")
                val maxTickets = worldConfig.getDouble("unload.wait.tickets")
                var time = 0
                while (time < maxTime) {
                    sleep(1 * 1000)
                    time ++
                    updateWorldPlayer(world)
                    val tickets = world.tickets()
                    if (maxTickets < tickets) {
                        break
                    }
                }
                world.unload()
            }.start()
        }
    }
}