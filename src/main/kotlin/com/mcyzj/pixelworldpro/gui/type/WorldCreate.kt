package com.mcyzj.pixelworldpro.gui.type

import com.mcyzj.pixelworldpro.PixelWorldPro
import com.mcyzj.pixelworldpro.api.interfaces.core.gui.Menu
import com.mcyzj.pixelworldpro.gui.GuiCore
import com.mcyzj.pixelworldpro.data.dataclass.gui.ConfigItemData
import com.mcyzj.pixelworldpro.data.dataclass.gui.MenuData
import com.mcyzj.pixelworldpro.data.dataclass.gui.MenuItemData
import com.mcyzj.pixelworldpro.file.Config
import com.mcyzj.pixelworldpro.world.Local
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class WorldCreate : Menu {
    private val logger = PixelWorldPro.instance.logger
    private var cache = HashMap<String, Any>()
    private var worldConfig = Config.world
    private fun buildMenu(player: OfflinePlayer, menu: YamlConfiguration, cache: HashMap<String, Any>): MenuData? {
        //构建基础菜单
        val title = menu.getString("Title")
        val slots = menu.getStringList("Slots")
        val gui = if (title == null) {
            Bukkit.createInventory(null, slots.size * 9)
        } else {
            Bukkit.createInventory(null, slots.size * 9, title)
        }
        //构建物品Map
        val configItemMap = GuiCore.buildItem(menu) ?: return null
        val menuItemMap = HashMap<Int, MenuItemData>()
        //填充物品Map
        //初始化菜单格子数
        var menuNumber = 0
        //遍历菜单的行数
        for (menuList in slots) {
            //将每一行的字符数量填充至9
            val itemList = menuList.split("") as ArrayList
            //清除split导致的第一个和最后一个的空格
            itemList.removeFirst()
            itemList.removeLast()
            while (itemList.size < 9) {
                itemList.add(" ")
            }
            //初始化当前行数起始
            var menuListNumber = 0
            for (itemKey in itemList) {
                if (menuListNumber >= 9) {
                    continue
                }
                val menuItemData = buildItem(itemKey, player, configItemMap, menu, cache)
                if (menuItemData != null) {
                    gui.setItem(menuNumber, menuItemData.itemStack)
                    menuItemMap[menuNumber] = menuItemData
                }
                menuListNumber++
                menuNumber++
            }
        }
        return MenuData(
            gui,
            menu,
            menuItemMap,
            this.cache,
            this
        )
    }

    private fun buildItem(item: String, player: OfflinePlayer, configItemMap: HashMap<String, ConfigurationSection>, menu: YamlConfiguration, cache: HashMap<String, Any>): MenuItemData? {
        val configItemData = configItemMap[item] ?: return null
        val command = if (configItemData.getList("Command") != null){
            configItemData.getStringList("Command")
        }else{
            java.util.ArrayList()
        }
        return when (configItemData.getString("Type")){
            else -> {
                val itemStack = GuiCore.buildItem(configItemData, player)!!
                val itemMeta = itemStack.itemMeta
                if (configItemData.getString("name") != null){
                    itemMeta.setDisplayName(replaceCreate(configItemData.getString("name")!!, player, menu))
                }
                val newLore = ArrayList<String>()
                if (itemMeta.lore != null){
                    for (lore in itemMeta.lore!!){
                        newLore.add(replaceCreate(lore, player, menu))
                    }
                    itemMeta.lore = newLore
                }
                itemStack.setItemMeta(itemMeta)
                MenuItemData(
                    itemStack,
                    configItemData.getString("Type"),
                    command,
                    configItemData.getString("Value"),
                    java.util.HashMap()
                )
            }
        }
    }


    private fun replaceCreate(msg: String, offlinePlayer: OfflinePlayer, menu: YamlConfiguration): String {
        val player = offlinePlayer as Player
        var new = msg
        //填充模板名称
        val template = this.cache["Template"] ?: "Random"
        val templateName = menu.getString("Template.$template")
        if (templateName != null) {
            new = new.replace("{Menu.Cache.Template}", templateName)
        }
        //填充购买需要
        val useList = worldConfig.getConfigurationSection("Create.Use")!!.getKeys(false)
        useList.remove("Default")
        if (useList.isNotEmpty()){
            for (use in useList){
                val permission = worldConfig.getString("Create.Use.$use.Permission")!!
                if (!player.hasPermission(permission)){
                    continue
                }
                new = new.replace("{CreateUse.Money}", worldConfig.getDouble("Create.Use.$use.Money").toString())
                new = new.replace("{CreateUse.Point}", worldConfig.getDouble("Create.Use.$use.Point").toString())
                return new
            }
        }
        val permission = worldConfig.getString("Create.Use.Default.Permission")!!
        if (!player.hasPermission(permission)){
            new = new.replace("{CreateUse.Money}", "没有权限")
            new = new.replace("{CreateUse.Point}", "没有权限")
            return new
        }
        new = new.replace("{CreateUse.Money}", worldConfig.getDouble("Create.Use.Default.Money").toString())
        new = new.replace("{CreateUse.Point}", worldConfig.getDouble("Create.Use.Default.Point").toString())
        return new
    }

    override fun open(opener: Player, player: OfflinePlayer, menu: YamlConfiguration, cache: HashMap<String, Any>) {
        this.cache = cache
        if (this.cache["Template"] == null){
            this.cache["Template"] = "Random"
        }
        this.cache["UUID"] = player.uniqueId.toString()
        val menuData = buildMenu(player, menu, cache) ?: return
        GuiCore.setOpenMenu(opener, menuData)
        opener.openInventory(menuData.inventory)
    }

    override fun onClick(player: Player, number: Int, clickType: ClickType, menuData: MenuData) {
        val itemMap = menuData.menuItemMap
        val itemData = itemMap[number]
        if (itemData == null){
            player.closeInventory()
            return
        }
        when (itemData.type){
            "CreateWorld" -> {
                val uuid = UUID.fromString(menuData.cache["UUID"].toString())
                var template = menuData.cache["Template"]
                player.closeInventory()
                if (template != null) {
                    template = template.toString()
                    if (template != "Random") {
                        Local.createWorld(uuid, template)
                    } else {
                        Local.createWorld(uuid, null)
                    }
                } else {
                    Local.createWorld(uuid, null)
                }
            }
            "Template" -> {
                if (itemData.value == null){
                    logger.warning("有问题的菜单文件 ${menuData.config.name} : Item模块中的一个Template模块没有指定的Value值")
                    return
                }
                menuData.cache["Template"] = itemData.value
                val uuid = UUID.fromString(menuData.cache["UUID"].toString())
                val offlinePlayer = Bukkit.getOfflinePlayer(uuid)
                player.closeInventory()
                WorldCreate().open(player, offlinePlayer, menuData.config, menuData.cache)
            }
        }
        GuiCore.runCommand(itemData.command, player, menuData.cache)
    }

}
