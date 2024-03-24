package com.mcyzj.lib.bukkit.menu

import com.mcyzj.lib.plugin.JiangPlugin
import com.mcyzj.pixelworldpro.v2.core.menu.WorldList
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.Inventory
import java.io.File


object MenuImpl {
    private val menuDriver = HashMap<String, MenuAPI>()
    private val menuMap = HashMap<JiangPlugin, HashMap<String, File>>()
    private val openMenu = HashMap<Inventory, Menu>()

    fun registerMenuDriver(name: String, driver: MenuAPI) {
        menuDriver[name] = driver
    }

    fun getMenuDriver(name: String?): MenuAPI {
        return menuDriver[name] ?: WorldList()
    }

    fun registerMenuConfig(menu: File, plugin: JiangPlugin) {
        val cache = YamlConfiguration()
        cache.load(menu)
        val command = (cache.getList("command") ?: ArrayList()).toArrayStringList()
        for (str in command) {
            val map = menuMap[plugin] ?: HashMap()
            map[str] = menu
            menuMap[plugin] = map
        }
    }

    fun removePluginMenu(plugin: JiangPlugin) {
        menuMap.remove(plugin)
    }

    fun getMenu(menu: String, plugin: JiangPlugin): YamlConfiguration? {
        val map = menuMap[plugin] ?: return null
        val cache = map[menu] ?: return null
        val config = YamlConfiguration()
        config.load(cache)
        return config
    }

    fun getMenuList(plugin: JiangPlugin, file: Boolean = false): ArrayList<String> {
        val list = ArrayList<String>()
        val map = menuMap[plugin] ?: return list
        for (str in map.keys) {
            list.add(str)
            if (file) {
                list.add(map[str]!!.name)
            }
        }
        return list
    }

    fun setOpenMenu(menu: Menu) {
        val openInventory = menu.openInventory ?: return
        openMenu[openInventory] = menu
    }

    fun removeOpenMenu(inventory: Inventory) {
        openMenu.remove(inventory)
    }

    fun getOpenMenu(inventory: Inventory): Menu? {
        return openMenu[inventory]
    }
}

private fun <E> MutableList<E>.toArrayStringList(): ArrayList<String> {
    val newList = ArrayList<String>()
    for (str in this) {
        newList.add(str.toString())
    }
    return newList
}
