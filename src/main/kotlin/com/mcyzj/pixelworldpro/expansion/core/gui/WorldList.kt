package com.mcyzj.pixelworldpro.expansion.core.gui

import com.mcyzj.pixelworldpro.PixelWorldPro
import com.mcyzj.pixelworldpro.api.interfaces.core.gui.Menu
import com.mcyzj.pixelworldpro.expansion.core.gui.dataclass.ConfigItemData
import com.xbaimiao.easylib.module.item.displayName
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack


class WorldList : Menu {
    val logger = PixelWorldPro.instance.logger
    private fun buildMenu(player: OfflinePlayer, menu: YamlConfiguration): Inventory? {
        //构建基础菜单
        val title = menu.getString("Title")
        val slots = menu.getStringList("Slots")
        val gui = if (title == null){
            Bukkit.createInventory(null, slots.size * 9)
        } else {
            Bukkit.createInventory(null, slots.size * 9, title)
        }
        //构建物品Map
        val menuItemMap = Core.buildItem(menu) ?: return null
        val diamond = ItemStack(Material.DIAMOND)
        gui.setItem(22, diamond)
        val gold = ItemStack(Material.GOLD_INGOT)
        gui.setItem(23, gold)
        player.openInventory(gui)
        return gui
    }

    private fun buildItem(item: String, player: OfflinePlayer, menuItemMap: HashMap<String, ConfigItemData>, menu: YamlConfiguration): Unit? {
        val menuItemData = menuItemMap[item] ?: return null
        return when (menuItemData.type){
            else -> {
                val material = Material.getMaterial(menuItemData.material)
                if (material == null){
                    logger.warning("有问题的菜单文件 ${menu.name} : Item模块中的${item}指定的Material内容无法从服务端内获取为一个有效的物品")
                    return null
                }
                val itemStack = ItemStack(material)
                if (menuItemData.name != null){
                    itemStack.displayName
                    
                }
            }
        }
    }

    override fun open(opener: Player, player: OfflinePlayer, menu: YamlConfiguration, cache: HashMap<String, String>) {
        val gui = buildMenu(player, menu) ?: return
        opener.openInventory(gui)
    }

}