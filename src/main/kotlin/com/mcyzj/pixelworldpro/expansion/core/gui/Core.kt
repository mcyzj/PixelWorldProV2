package com.mcyzj.pixelworldpro.expansion.core.gui

import com.mcyzj.pixelworldpro.PixelWorldPro
import com.mcyzj.pixelworldpro.expansion.core.gui.dataclass.ConfigItemData
import com.mcyzj.pixelworldpro.expansion.core.gui.dataclass.MenuData
import com.xbaimiao.easylib.module.chat.BuiltInConfiguration
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent

object Core {
    val logger = PixelWorldPro.instance.logger
    val menuOpenMap = HashMap<Player, MenuData>()
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

    fun setOpenMenu(player: Player, menuData: MenuData){
        menuOpenMap[player] = menuData
    }

    fun menuClick(event: InventoryClickEvent){
        val player = event.whoClicked as Player
        val menu = menuOpenMap[player] ?: return
        event.isCancelled = true
        menu.menu.onClick(player, event.rawSlot, event.click, menu)
    }

    fun menuClose(event: InventoryCloseEvent){
        val player = event.player
        menuOpenMap.remove(player)
    }

    fun runCommand(commandList: List<String>, player: Player, cache: HashMap<String, Any>){
        if (commandList.isEmpty()){
            return
        }
        for (command in commandList){
            val strList = command.split("]")
            val runner = strList.first().replace("[", "")
            when (runner){
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
                    if (strList.last().endsWith(".yml")) {
                        val config = BuiltInConfiguration("gui/${strList.last()}")
                        Open.open(player, config, cache)
                    } else {
                        Open.open(player, strList.last(), cache)
                    }
                }
            }
        }
    }
}