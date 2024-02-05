@file:Suppress("DEPRECATION")

package com.mcyzj.pixelworldpro.gui.type

import com.mcyzj.pixelworldpro.PixelWorldPro
import com.mcyzj.pixelworldpro.api.interfaces.core.gui.Menu
import com.mcyzj.pixelworldpro.data.dataclass.WorldData
import com.mcyzj.pixelworldpro.data.dataclass.gui.MenuData
import com.mcyzj.pixelworldpro.data.dataclass.gui.MenuItemData
import com.mcyzj.pixelworldpro.expansion.core.level.admin.Admin
import com.mcyzj.pixelworldpro.gui.GuiCore
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


class WorldList : Menu {
    private val logger = PixelWorldPro.instance.logger
    private var cache = HashMap<String, Any>()
    private fun buildMenu(player: OfflinePlayer, menu: YamlConfiguration, cache: HashMap<String, Any>): MenuData? {
        //构建基础菜单
        val title = menu.getString("Title")
        val slots = menu.getStringList("Slots")
        val gui = if (title == null) {
            Bukkit.createInventory(null, slots.size * 9)
        } else {
            Bukkit.createInventory(null, slots.size * 9, title.replace("{Menu.Cache.Search}", this.cache["Search"].toString()))
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
            ArrayList()
        }
        return when (configItemData.getString("Type")){
            "List" -> {
                val worldData = getList(player)
                if (worldData != null) {
                    val itemStack = GuiCore.buildItem(configItemData, player)!!
                    val itemMeta = itemStack.itemMeta
                    itemMeta.setDisplayName(replaceList(worldData, configItemData.name, player))
                    if (itemMeta.lore != null) {
                        val menuItemLore = ArrayList<String>()
                        for (str in itemMeta.lore!!){
                            menuItemLore.add(replaceList(worldData, str, player))
                        }
                        itemMeta.lore = menuItemLore
                    }
                    itemStack.setItemMeta(itemMeta)
                    val itemCache = HashMap<String, String>()
                    itemCache["Id"] = worldData.id.toString()
                    this.cache["FillNumber"] = (this.cache["FillNumber"].toString()).toInt() + 1
                    MenuItemData(
                        itemStack,
                        configItemData.getString("Type"),
                        command,
                        configItemData.getString("Value"),
                        itemCache
                    )
                } else {
                    val itemStack = ItemStack(Material.AIR)
                    MenuItemData(
                        itemStack,
                        configItemData.getString("Type"),
                        command,
                        configItemData.getString("Value"),
                        HashMap()
                    )
                }
            }

            "Page" -> {
                when (configItemData.getString("Value")){
                    "Next" -> {
                        if (this.cache["Last"] == "true"){
                            val itemStack = GuiCore.buildItem(configItemData, player)!!
                            val itemMeta = itemStack.itemMeta
                            if (itemMeta.lore != null) {
                                val menuItemLore = ArrayList<String>()
                                for (str in itemMeta.lore!!){
                                    menuItemLore.add(str.replace("{page}", "末"))
                                }
                                itemMeta.lore = menuItemLore
                            }
                            itemStack.setItemMeta(itemMeta)
                            MenuItemData(
                                itemStack,
                                configItemData.getString("Type"),
                                command,
                                configItemData.getString("Value"),
                                HashMap()
                            )
                        } else {
                            val itemStack = GuiCore.buildItem(configItemData, player)!!
                            val itemMeta = itemStack.itemMeta
                            if (itemMeta.lore != null) {
                                val menuItemLore = ArrayList<String>()
                                for (str in itemMeta.lore!!){
                                    menuItemLore.add(str.replace("{page}", (((this.cache["Page"]?:"1").toString().toInt()) + 1).toString()))
                                }
                                itemMeta.lore = menuItemLore
                            }
                            itemStack.setItemMeta(itemMeta)
                            MenuItemData(
                                itemStack,
                                configItemData.getString("Type"),
                                command,
                                configItemData.getString("Value"),
                                HashMap()
                            )
                        }
                    }

                    "Back" -> {
                        if (this.cache["Page"] == null){
                            this.cache["Page"] = "1"
                        }
                        if ((this.cache["Page"]!!.toString().toInt()) < 2){
                            val itemStack = GuiCore.buildItem(configItemData, player)!!
                            val itemMeta = itemStack.itemMeta
                            if (itemMeta.lore != null) {
                                val menuItemLore = ArrayList<String>()
                                for (str in itemMeta.lore!!){
                                    menuItemLore.add(str.replace("{page}", "首"))
                                }
                                itemMeta.lore = menuItemLore
                            }
                            itemStack.setItemMeta(itemMeta)
                            MenuItemData(
                                itemStack,
                                configItemData.getString("Type"),
                                command,
                                configItemData.getString("Value"),
                                HashMap()
                            )
                        } else {
                            val itemStack = GuiCore.buildItem(configItemData, player)!!
                            val itemMeta = itemStack.itemMeta
                            if (itemStack.lore != null) {
                                val menuItemLore = ArrayList<String>()
                                for (str in itemStack.lore!!){
                                    menuItemLore.add(str.replace("{page}", ((this.cache["Page"].toString().toInt()) - 1).toString()))
                                }
                                itemMeta.lore = menuItemLore
                            }
                            itemStack.setItemMeta(itemMeta)
                            MenuItemData(
                                itemStack,
                                configItemData.getString("Type"),
                                command,
                                configItemData.getString("Value"),
                                HashMap()
                            )
                        }
                    }

                    else -> {
                        val itemStack = GuiCore.buildItem(configItemData, player)!!
                        val itemMeta = itemStack.itemMeta
                        if (itemMeta.lore != null){
                            itemMeta.lore = itemMeta.lore
                        }
                        itemStack.setItemMeta(itemMeta)
                        MenuItemData(
                            itemStack,
                            configItemData.getString("Type"),
                            command,
                            configItemData.getString("Value"),
                            HashMap()
                        )
                    }
                }
            }

            else -> {
                val itemStack = GuiCore.buildItem(configItemData, player)!!
                val itemMeta = itemStack.itemMeta
                if (itemMeta.lore != null){
                    itemMeta.lore = itemMeta.lore
                }
                itemStack.setItemMeta(itemMeta)
                MenuItemData(
                    itemStack,
                    configItemData.getString("Type"),
                    command,
                    configItemData.getString("Value"),
                    HashMap()
                )
            }
        }
    }

    private fun getList(player: OfflinePlayer): WorldData? {
        var number = if (this.cache["Number"] == null){
            0
        } else {
            this.cache["Number"].toString().toInt()
        }
        val worldMap = getWorldList()
        while ((number + 1) <= worldMap.size) {
            val worldData = worldMap.values.toList()[number]
            //构建世界禁止进入的权限组列表
            val banList = ArrayList<String>()
            for (permission in worldData.permission.keys) {
                val permissionData = worldData.permission[permission]!!
                if (permissionData["Teleport"] == "false") {
                    banList.add(permission)
                }
            }
            if (worldData.player[player.uniqueId] !in banList) {
                this.cache["Number"] = (number + 1).toString()
                return worldData
            }
            number ++
        }
        this.cache["Last"] = "True"
        return null
    }

    private fun getWorldList(): HashMap<Int, WorldData> {
        val any = this.cache["WorldMap"]
        return if (any == null){
            val map = PixelWorldPro.databaseApi.getWorldIdMap()
            this.cache["WorldMap"] = map
            map
        } else {
            any as HashMap<Int, WorldData>
        }
    }

    private fun replaceList(worldData: WorldData, msg: String, player: OfflinePlayer): String {
        var new = msg
        val owner = Bukkit.getOfflinePlayer(worldData.owner)
        val permission = if (owner == player){
            worldData.permission["Owner"] !!
        } else {
            worldData.permission[(worldData.player[player.uniqueId] ?: "Visitor")]!!
        }
        new = new.replace("{WorldData.Name}", worldData.name)
        new = new.replace("{WorldData.Permission}", permission["Name"] ?: "")
        if (owner.name != null) {
            new = new.replace("{WorldData.Owner.Name}", owner.name!!)
        }
        new = new.replace("{WorldData.Owner.UUID}", owner.uniqueId.toString())
        new = new.replace("{WorldData.Level}", Admin.getLevel(worldData).toString())
        return new
    }

    override fun open(opener: Player, player: OfflinePlayer, menu: YamlConfiguration, cache: HashMap<String, Any>) {
        this.cache = cache
        this.cache["UUID"] = player.uniqueId.toString()
        this.cache["FillNumber"] = "0"
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
            "List" -> {
                val id = itemData.cache["Id"]
                if (id == null){
                    player.closeInventory()
                    return
                }
                Local.tpWorldId(player, id.toInt())
            }

            "Page" -> {
                when (itemData.value) {
                    "Next" -> {
                        if (menuData.cache["Last"] != "True"){
                            player.closeInventory()
                            menuData.cache["LastFillNumber"] = menuData.cache["FillNumber"]!!
                            menuData.cache["Page"] = ((menuData.cache["Page"].toString().toInt()) + 1 ).toString()
                            val uuid = UUID.fromString(menuData.cache["UUID"].toString())
                            val offlinePlayer = Bukkit.getOfflinePlayer(uuid)
                            WorldCreate().open(player, offlinePlayer, menuData.config, menuData.cache)
                        }
                    }
                    "Back" -> {
                        if ((menuData.cache["Page"].toString().toInt()) > 1){
                            player.closeInventory()
                            menuData.cache["Last"] = "False"
                            menuData.cache["Page"] = ((menuData.cache["Page"].toString().toInt()) - 1 ).toString()
                            menuData.cache["Number"] = ((menuData.cache["Number"].toString().toInt()) - (menuData.cache["LastFillNumber"].toString().toInt()) - (menuData.cache["FillNumber"].toString().toInt())).toString()
                            val uuid = UUID.fromString(menuData.cache["UUID"].toString())
                            val offlinePlayer = Bukkit.getOfflinePlayer(uuid)
                            WorldCreate().open(player, offlinePlayer, menuData.config, menuData.cache)
                        }
                    }
                }
            }
        }
        GuiCore.runCommand(itemData.command, player, menuData.cache)
    }

}
