package com.mcyzj.pixelworldpro.gui

import com.mcyzj.pixelworldpro.PixelWorldPro
import com.mcyzj.pixelworldpro.data.dataclass.gui.ConfigItemData
import com.mcyzj.pixelworldpro.data.dataclass.gui.MenuData
import com.xbaimiao.easylib.bridge.replacePlaceholder
import com.xbaimiao.easylib.module.chat.BuiltInConfiguration
import com.xbaimiao.easylib.module.utils.colored
import com.xbaimiao.easylib.xseries.XItemStack
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.inventory.ItemStack

object GuiCore {
    val logger = PixelWorldPro.instance.logger
    private val menuOpenMap = HashMap<Player, MenuData>()
    private val notClose = ArrayList<Player>()
    fun buildItem(menu: YamlConfiguration): HashMap<String, ConfigurationSection>? {
        //初始化itemMap
        val itemMap = HashMap<String, ConfigurationSection>()
        //读取item模块
        val itemConfig = menu.getConfigurationSection("Item")
        if (itemConfig == null){
            logger.warning("无效的菜单文件 ${menu.name} : 没有Item模块")
            return null
        }
        //历遍item模块中的键值进行item模块读取
        for (itemKey in itemConfig.getKeys(true)) {
            val item = itemConfig.getConfigurationSection(itemKey) ?: continue
            val material = item.getString("Material")
            if (material == null) {
                logger.warning("有问题的菜单文件 ${menu.name} : Item模块中的${itemKey}没有指定Material")
                continue
            }
            itemMap[itemKey] = item
        }
        //返回item数据
        return itemMap
    }

    fun buildItem(configuration: ConfigurationSection, player: OfflinePlayer): ItemStack? {
        val name = configuration.getString("Name")
        val lore = configuration.getStringList("Lore")
        val skull = configuration.getString("Skull")
        if (configuration.getString("Material") == null)
            return null
        if (configuration.contains("Name"))
            configuration.set("name",configuration.getString("Name")!!.replacePlaceholder(player).colored())
        if (configuration.contains("Lore"))
            configuration.set("lore",configuration.getStringList("Lore").replacePlaceholder(player).colored())
        if (configuration.contains("Skull"))
            configuration.set("skull",configuration.getString("Skull")!!.replacePlaceholder(player))
        if (configuration.contains("Custom-Model-Data"))
            configuration.set("custom-model-data",configuration.getStringList("Custom-Model-Data").replacePlaceholder(player).colored())
        val item = XItemStack.deserialize(configuration)
        configuration.set("name",name)
        configuration.set("lore",lore)
        configuration.set("skull",skull)
        return item
    }

    fun setOpenMenu(player: Player, menuData: MenuData){
        menuOpenMap[player] = menuData
    }

    fun menuClick(event: InventoryClickEvent){
        val player = event.whoClicked as Player
        val menu = menuOpenMap[player] ?: return
        event.isCancelled = true
        menu.menu.onClick(player, event.rawSlot, event.click, menu)
    }
    fun menuClick(event: PrepareAnvilEvent) {
        val player = event.view.player as Player
        println(player.name)
        val menu = menuOpenMap[player] ?: return
        menu.cache["Rename"] = event.inventory.renameText ?: ""
        val i = event.inventory
    }

    fun menuClose(event: InventoryCloseEvent){
        val player = event.player
        if (player !in notClose) {
            menuOpenMap.remove(player)
        } else {
            notClose.remove(player)
        }
    }

    fun runCommand(commandList: List<String>, player: Player, cache: HashMap<String, Any>){
        if (commandList.isEmpty()){
            return
        }
        for (command in commandList){
            val strList = command.split("]")
            when (strList.first().replace("[", "")){
                //执行命令
                "Player" -> {
                    player.performCommand(strList.last())
                }
                "OP" -> {
                    if (player.isOp){
                        player.performCommand(strList.last())
                    } else {
                        player.isOp = true
                        try {
                            player.performCommand(strList.last())
                        }catch (e:Exception){
                            e.printStackTrace()
                        }
                        player.isOp = false
                    }
                }
                "Console" -> {
                    var msg = strList.last()
                    msg = msg.replace("{Player.Name}", player.name)
                    msg = msg.replace("{Player.UUID}", player.uniqueId.toString())
                    Bukkit.getConsoleSender().sendMessage("/$msg")
                }
                //关闭窗口
                "Close" -> {
                    player.closeInventory()
                }
                //跳转菜单
                "Menu" -> {
                    notClose.add(player)
                    val type = strList[1].replace("[", "")
                    if (type == "Cache"){
                        if (strList.last().endsWith(".yml")) {
                            val config = BuiltInConfiguration("gui/${strList.last()}")
                            OpenGui.open(player, config, cache)
                        } else {
                            OpenGui.open(player, strList.last(), cache)
                        }
                    } else {
                        if (strList.last().endsWith(".yml")) {
                            val config = BuiltInConfiguration("gui/${strList.last()}")
                            OpenGui.open(player, config, HashMap())
                        } else {
                            OpenGui.open(player, strList.last(), HashMap())
                        }
                    }
                }
            }
        }
    }
}