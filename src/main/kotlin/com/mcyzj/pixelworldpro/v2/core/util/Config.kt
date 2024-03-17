package com.mcyzj.pixelworldpro.v2.core.util

import com.mcyzj.pixelworldpro.v2.core.permission.dataclass.ItemData
import com.mcyzj.lib.plugin.file.BuiltInConfiguration
import org.bukkit.configuration.ConfigurationSection

object Config {
    var config = BuiltInConfiguration("config.yml")
    var world = BuiltInConfiguration("world.yml")
    var permission = BuiltInConfiguration("permission.yml")
    var bungee = BuiltInConfiguration("bungeeSet.yml")

    fun getLang(): BuiltInConfiguration {
        val lang = config.get("lang") ?: "zh_cn"
        return BuiltInConfiguration("lang/${lang}.yml")
    }

    fun buildItemMap(config: ConfigurationSection): HashMap<String, ItemData> {
        val itemMap = HashMap<String, ItemData>()
        for (key in config.getKeys(false)) {
            val itemConfig = config.getConfigurationSection(key)!!
            val loreList = ArrayList<String>()
            if (itemConfig.getList("lore") != null) {
                for (value in itemConfig.getList("lore")!!) {
                    loreList.add(value.toString())
                }
            }
            itemMap[key] = ItemData(
                itemConfig.getString("material")!!,
                loreList
            )
        }
        return itemMap
    }
}