package com.mcyzj.lib.bukkit.menu

import com.cryptomorin.xseries.XItemStack
import com.mcyzj.lib.bukkit.bridge.replacePlaceholder
import com.mcyzj.lib.bukkit.menu.dataclass.MenuItemData
import com.mcyzj.lib.bukkit.menu.dataclass.SlotData
import com.mcyzj.lib.bukkit.utils.Color.colored
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player

class Menu(val opener: Player, val player: OfflinePlayer, val menu: YamlConfiguration) {
    private val driver = MenuImpl.getMenuDriver(menu.getString("type"))
    private val itemMap = HashMap<String, SlotData>()

    private val listNumber = HashMap<String, Int>()
    private val listLastNumber = HashMap<String, Int>()

    fun build() {
        val title = menu.getString("title")?: "新建菜单"
        if (itemMap.isEmpty()) {
            buildItemMap()
            if (itemMap.isEmpty()) {
                return
            }
        }

        val slots = menu.getStringList("slots")
        val menu = Bukkit.createInventory(null, slots.size * 9, title.replacePlaceholder(player).colored())
        //填充物品Map
        //初始化菜单格子数
        var menuNumber = 0
        //遍历菜单的行数
        for (menuList in slots) {
            //将每一行的字符数量填充至9
            val itemList = menuList.split("") as java.util.ArrayList
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
                if (itemMap[itemKey] != null) {
                    val finalSlotData = fillSlot(itemMap[itemKey]!!)
                    if (finalSlotData != null) {
                        val menuItemData = buildItem(finalSlotData)
                        menu.setItem(menuNumber, menuItemData.itemStack)
                        //menuItemMap[menuNumber] = menuItemData
                    }
                }
                menuListNumber++
                menuNumber++
            }
        }

        opener.openInventory(menu)
    }

    @Suppress("UNCHECKED_CAST")
    private fun buildItemMap() {
        val item = menu.getConfigurationSection("item")!!
        //填充item数据
        for (key in item.getKeys(false)) {
            val itemConfig = item.getConfigurationSection(key)!!

            val material = itemConfig.getString("material") ?: continue

            val lore = (itemConfig.getList("lore") ?: ArrayList()) as ArrayList<String>
            val operate = (itemConfig.getList("operate") ?: ArrayList()) as ArrayList<String>

            val name = itemConfig.getString("name")
            val type = itemConfig.getString("type")
            val value = itemConfig.getString("value")

            itemMap[key] = SlotData(
                material,
                itemConfig,
                lore,
                operate,
                YamlConfiguration(),
                name,
                type,
                value
            )
        }
    }

    fun buildItem(slotData: SlotData): MenuItemData {
        val configuration = slotData.config
        configuration.set("name", slotData.name)
        configuration.set("lore", slotData.lore)
        if (configuration.contains("Skull"))
            configuration.set("skull",configuration.getString("Skull")!!.replacePlaceholder(player))
        if (configuration.contains("Custom-Model-Data"))
            configuration.set("custom-model-data",configuration.getStringList("Custom-Model-Data").replacePlaceholder(player).colored())
        val item = XItemStack.deserialize(configuration)
        return MenuItemData(
            item,
            slotData
        )
    }

    private fun fillSlot(slotData: SlotData): SlotData? {
        val filledTemplate = when(slotData.type) {
            "list" -> {
                if (slotData.value == null) {
                    return null
                }
                val number = listNumber[slotData.value] ?: 0
                val cache = driver.getList(player, slotData, number) ?: return null
                listNumber[slotData.value] = number + 1
                cache
            }
            else -> {
                slotData
            }
        }

        val lore = ArrayList<String>()
        for (str in filledTemplate.lore) {
            lore.add(fillVariables(str, slotData))
        }

        val operate = ArrayList<String>()
        for (str in filledTemplate.operate) {
            operate.add(fillVariables(str, slotData))
        }
        val name = if (filledTemplate.name != null) {
            fillVariables(filledTemplate.name, slotData)
        } else {
            null
        }
        return filledTemplate.copy(lore = lore, operate = operate, name = name)
    }

    private fun fillVariables(str: String, slotData: SlotData): String {
        var finalStr = str
        if (("{" in str).and("}" in str)) {
            val internalVariablesList = str.split("}")
            for (surplusStr in internalVariablesList) {
                if ("{" !in surplusStr) {
                    continue
                }
                val finalVariables = surplusStr.split("{")[1]
                if (finalVariables.startsWith("SlotData")) {
                    finalStr = finalStr.replace(
                        "{$finalVariables}",
                        slotData.data.getString(finalVariables.replace("SlotData.", "")) ?: ""
                    )
                } else if (finalVariables.startsWith("MenuData")) {
                    finalStr = finalStr.replace(
                        "{$finalVariables}",
                        slotData.data.getString(finalVariables.replace("MenuData.", "")) ?: ""
                    )
                }
            }
        }
        return finalStr.replacePlaceholder(player).colored()
    }
}