package com.mcyzj.pixelworldpro.expansion.core.gui

import com.mcyzj.pixelworldpro.PixelWorldPro
import com.mcyzj.pixelworldpro.expansion.core.gui.dataclass.ConfigItemData
import org.bukkit.configuration.file.YamlConfiguration

object Core {
    val logger = PixelWorldPro.instance.logger
    fun buildItem(menu: YamlConfiguration): HashMap<String, ConfigItemData>? {
        //初始化itemMap
        val itemMap = HashMap<String, ConfigItemData>()
        //读取item模块
        val itemConfig = menu.getConfigurationSection("Item")
        if (itemConfig == null){
            logger.warning("无效的菜单文件 ${menu.name} : 没有Item模块")
            return null
        }
        //历遍item模块中的键值进行item模块读取
        for (itemKey in itemConfig.getKeys(true)){
            val item = itemConfig.getConfigurationSection(itemKey) ?: continue
            val material = item.getString("Material")
            if (material == null){
                logger.warning("有问题的菜单文件 ${menu.name} : Item模块中的${itemKey}没有指定Material")
                continue
            }
            val menuItemMap = ConfigItemData(
                material,
                item.getString("Name"),
                item.getStringList("Lore"),
                item.getString("Type"),
                item.getStringList("Command"),
                item.getString("Value"),
                HashMap()
            )
            itemMap[itemKey] = menuItemMap
        }
        //返回item数据
        return itemMap
    }
}