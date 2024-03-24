package com.mcyzj.lib.bukkit.menu

import com.cryptomorin.xseries.XItemStack
import com.mcyzj.lib.bukkit.bridge.replacePlaceholder
import com.mcyzj.lib.bukkit.menu.dataclass.MenuItemData
import com.mcyzj.lib.bukkit.menu.dataclass.OperateData
import com.mcyzj.lib.bukkit.menu.dataclass.SlotData
import com.mcyzj.lib.bukkit.utils.Color.colored
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

class Menu(private val opener: Player, val player: OfflinePlayer, private val menu: YamlConfiguration) {
    private val driver = MenuImpl.getMenuDriver(menu.getString("type"))
    private val itemMap = HashMap<String, SlotData>()

    private val listNumber = HashMap<String, Int>()
    private val listLast = HashMap<String, Boolean>()

    private val pageList = HashMap<Int, Inventory>()
    private var page = 1

    var openInventory: Inventory? = null
    private var openItem: HashMap<Int, SlotData>? = null

    private val menuData = YamlConfiguration()

    private fun buildMenu(): Inventory? {
        if (listLast.isNotEmpty()) {
            if (!listLast.values.equals(false)) {
                menuData.set("Page.Next", "*")
                return null
            } else {
                menuData.set("Page.Next", page)
            }
        }

        if (page < 2) {
            menuData.set("Page.Last", "*")
        } else {
            menuData.set("Page.Last", page - 1)
        }

        openItem = HashMap()

        val title = menu.getString("title")?: "新建菜单"
        if (itemMap.isEmpty()) {
            buildItemMap()
            if (itemMap.isEmpty()) {
                return null
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
                        openItem!![menuNumber] = finalSlotData
                    }
                }
                menuListNumber++
                menuNumber++
            }
        }
        return menu

    }

    fun open() {
        val menu = pageList[page] ?: buildMenu() ?: return
        openInventory = menu
        MenuImpl.setOpenMenu(this)
        pageList[page] = menu
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

            val basicOperate = (itemConfig.getList("operate") ?: ArrayList()) as ArrayList<String>
            val operate = ArrayList<OperateData>()
            for (str in basicOperate) {
                var leftClick = false
                var rightClick = false
                var value = str

                val flagList = ArrayList<String>()
                if (("[" in str).and("]" in str)) {
                    val internalVariablesList = str.split("]")
                    for (surplusStr in internalVariablesList) {
                        if ("[" !in surplusStr) {
                            continue
                        }
                        flagList.add(surplusStr.split("[")[1])
                    }
                }

                if (flagList.isNotEmpty()) {
                    for (flag in flagList) {
                        value = value.replace("[$flag]", "").trim()
                    }
                }

                if (flagList.contains("left")) {
                    leftClick = true
                } else if (flagList.contains("right")) {
                    rightClick = true
                } else {
                    leftClick = true
                    rightClick = true
                }
                flagList.remove("all")
                flagList.remove("left")
                flagList.remove("right")

                val operateType = if (flagList.size >= 1) {
                    flagList[0]
                    flagList.removeFirst()
                } else {
                    "command"
                }
                operate.add(
                    OperateData(
                    leftClick,
                    rightClick,
                    operateType,
                    flagList,
                    value
                )
                )
            }

            val name = itemConfig.getString("name")
            val type = itemConfig.getString("type")
            val value = itemConfig.getString("value")

            val skull = itemConfig.getString("skull")
            val customModelData = itemConfig.getString("custom-model-data")

            itemMap[key] = SlotData(
                material,
                itemConfig,
                lore,
                operate,
                YamlConfiguration(),
                name,
                type,
                value,
                skull,
                customModelData
            )
        }
    }

    private fun buildItem(slotData: SlotData): MenuItemData {
        val configuration = slotData.config
        configuration.set("material", slotData.material)
        configuration.set("name", slotData.name)
        configuration.set("lore", slotData.lore)
        configuration.set("skull", slotData.skull)
        configuration.set("custom-model-data", slotData.customModelData)
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
                val cache = driver.getList(player, slotData, number)
                if (cache == null) {
                    listLast[slotData.value] = true
                    return null
                }
                listNumber[slotData.value] = number + 1
                cache
            }
            "page" -> {
                when (slotData.value) {
                    "next" -> {
                        if (listLast.isEmpty()) {
                            slotData
                        } else if (!listLast.values.equals(false)) {
                            slotData.copy(material = "BARRIER")
                        } else {
                            slotData
                        }
                    }
                    "back" -> {
                        if (page < 2) {
                            slotData.copy(material = "BARRIER")
                        } else {
                            slotData
                        }
                    }
                    else -> {
                        slotData
                    }
                }
            }
            else -> {
                slotData
            }
        }

        val lore = ArrayList<String>()
        for (str in filledTemplate.lore) {
            lore.add(fillVariables(str, slotData))
        }

        val operate = ArrayList<OperateData>()
        for (basicOperateData in filledTemplate.operate) {
            operate.add(basicOperateData.copy(value = fillVariables(basicOperateData.value, slotData)))
        }
        val name = if (filledTemplate.name != null) {
            fillVariables(filledTemplate.name, slotData)
        } else {
            null
        }

        val skull = if (filledTemplate.skull != null) {
            fillVariables(filledTemplate.skull, slotData)
        } else {
            null
        }
        val customModelData = if (filledTemplate.customModelData != null) {
            fillVariables(filledTemplate.customModelData, slotData)
        } else {
            null
        }
        return filledTemplate.copy(lore = lore, operate = operate, name = name, skull = skull, customModelData = customModelData)
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
                        menuData.getString(finalVariables.replace("MenuData.", "")) ?: ""
                    )
                }
            }
        }
        return finalStr.replacePlaceholder(player).colored()
    }

    private fun runOperate(item: SlotData, left: Boolean, right: Boolean) {
        var close = false

        val operateList = item.operate

        if (operateList.isEmpty()) {
            return
        }

        for (operate in operateList) {
            if (!(((operate.leftClick).and(left)).or((operate.rightClick).and(right)))) {
                continue
            }

            when (operate.type) {
                "command" -> {
                    if (operate.flag.contains("console")) {
                        Bukkit.getConsoleSender().sendMessage("/" + operate.value)
                        continue
                    }
                    if (operate.flag.contains("admin")) {
                        if (player.isOp) {
                            opener.performCommand(operate.value)
                        } else {
                            player.isOp = true
                            opener.performCommand(operate.value)
                            player.isOp = false
                        }
                        continue
                    }
                    if (operate.flag.contains("player")) {
                        opener.performCommand(operate.value)
                        continue
                    }
                }

                "close" -> {
                    close = true
                    continue
                }
            }
        }

        if (close) {
            opener.closeInventory()
        }
    }

    fun onClick(index: Int, left: Boolean, right: Boolean) {
        val itemMap = openItem ?: return
        val itemData = itemMap[index] ?: return
        runOperate(itemData, left, right)

        when (itemData.type) {
            "list" -> {
            }

            "page" -> {
                when (itemData.value) {
                    "back" -> {
                        if (page < 2) {
                            return
                        }
                        page -= 1
                        open()
                    }

                    "next" -> {
                        if (pageList[page + 1] == null) {
                            if (listLast.isNotEmpty()) {
                                if (!listLast.values.equals(false)) {
                                    return
                                }
                            }
                        }
                        page += 1
                        open()
                    }
                }
            }
        }
    }
}