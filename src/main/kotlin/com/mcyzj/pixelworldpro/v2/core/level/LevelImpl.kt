﻿package com.mcyzj.pixelworldpro.v2.core.level
import com.mcyzj.lib.bukkit.bridge.economy.PlayerPoints
import com.mcyzj.lib.bukkit.bridge.economy.Vault
import com.mcyzj.lib.bukkit.item.hasItem
import com.mcyzj.lib.bukkit.item.takeItem
import com.mcyzj.pixelworldpro.v2.core.api.PixelWorldProApi
import com.mcyzj.pixelworldpro.v2.core.permission.dataclass.ItemData
import com.mcyzj.pixelworldpro.v2.core.util.Config
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.HashMap


object LevelImpl {
    private var config = Config.level
    private fun buildLevel(): HashMap<String, HashMap<Int, LevelData>> {
        val configLevelList = config.getConfigurationSection("level")!!.getKeys(false)
        val levelMap = HashMap<String, HashMap<Int, LevelData>>()
        for (group in configLevelList) {
            val levelDataMap = HashMap<Int, LevelData>()
            var configLevelSizeStart = 0
            var level = 1
            var lastConfigLevel = 1
            val groupLevelList = config.getConfigurationSection("level.${group}")!!.getKeys(false)
            while (configLevelSizeStart < groupLevelList.size) {
                if (level.toString() in groupLevelList) {
                    lastConfigLevel = level
                    val maxLevel = config.getBoolean("level.$group.$level.maxLevel")
                    val points = config.getDouble("level.$group.$level.up.points")
                    val money = config.getDouble("level.$group.$level.up.money")
                    val itemConfig = config.getConfigurationSection("level.$group.$level.up.item")
                    val itemMap = HashMap<String, Int>()
                    if (itemConfig != null) {
                        if (itemConfig.getKeys(false).isNotEmpty()) {
                            for (key in itemConfig.getKeys(false)) {
                                itemMap[key as String] = itemConfig.getInt(key)
                            }
                        }
                    }
                    val levelData = LevelData(
                        level,
                        maxLevel,
                        points,
                        money,
                        itemMap
                    )
                    levelDataMap[level] = levelData
                    level += 1
                    configLevelSizeStart += 1
                } else {
                    val levelData = levelDataMap[lastConfigLevel]!!
                    levelData.level = level
                    levelDataMap[level] = levelData
                    level += 1
                }
            }
            levelMap[group] = levelDataMap
        }
        return levelMap
    }
    private fun getItemData(name: String): ItemData {
        val material = config.getString("item.$name.material")!!.uppercase(Locale.getDefault())
        val lore = config.getStringList("item.$name.lore")
        return ItemData(
            material,
            lore
        )
    }
    fun levelUp(player: Player){
        //提升等级的准备工作
        val worldData = PixelWorldProApi().getWorld(player.uniqueId) ?: return
        val level = worldData.getLevel()
        val levelMap = buildLevel()
        var levelDataMap = levelMap["default"]!!
        levelMap.remove("default")
        for (key in levelMap.keys){
            if (player.hasPermission(key)){
                levelDataMap = levelMap[key]!!
            }
        }
        if (levelDataMap[level + 1] == null){
            player.sendMessage("已达到最大等级")
            return
        }
        val levelData = levelDataMap[level]?:return
        if (levelData.maxLevel){
            player.sendMessage("已达到最大等级")
            return
        }
        //进入提升等级流程
        //检验经济
        if (levelData.points > 0.0) {
            if (!PlayerPoints().has(player, levelData.points)) {
                player.sendMessage("点券不足:\n你需要 ${levelData.points} 个点券 \n你只有 ${PlayerPoints()[player]} 个点券")
                return
            }
        }
        if (levelData.money > 0.0) {
            if (!Vault().has(player, levelData.money)) {
                player.sendMessage("点券不足:\n你需要 ${levelData.money} 个金币 \n你只有 ${Vault()[player]} 个金币")
                return
            }
        }
        //检验物品
        val itemMap = levelData.item
        for (key in itemMap.keys){
            val itemData = getItemData(key)
            val material = Material.getMaterial(itemData.material)!!
            val item = ItemStack(material)
            for (lore in itemData.lore){
                item.lore?.add(lore)
            }
            if (!player.inventory.hasItem(item, itemMap[key]!!)) {
                player.sendMessage("物品不足: 你需要 ${itemMap[key]!!} 个 ${material.name}")
                return
            }
        }
        //拿走物品
        for (key in itemMap.keys) {
            val itemData = getItemData(key)
            player.inventory.takeItem(itemMap[key]!!) {
                return@takeItem this.type == Material.getMaterial(itemData.material)!!
            }
        }
        if (levelData.points > 0.0) {
            PlayerPoints().take(player, levelData.points)
        }
        if (levelData.money > 0.0) {
            Vault().take(player, levelData.money)
        }
        //增加等级
        player.sendMessage("等级增加！\n当前等级: ${level + 1}")
        worldData.setLevel(level + 1)
    }
}