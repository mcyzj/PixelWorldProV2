package com.mcyzj.pixelworldpro.expansion.core.gui

import com.mcyzj.pixelworldpro.data.dataclass.BasicCharMap
import com.mcyzj.pixelworldpro.world.Local
import org.bukkit.entity.Player

class WorldCreate(val player: Player) {
    private fun build(gui: String = "WorldCreate.yml", templateChoose: String? = null): BasicCharMap {
        val basicCharMap = Core.buildBaseGui(gui, player)
        val basic = basicCharMap.basic
        val charMap = basicCharMap.charMap
        var defaultTemplate: String?
        for (guiData in charMap) {
            if (guiData.value.type == "CreateWorld") {
                defaultTemplate = Core.getWorldCreateConfig().getStringColored("Template.random")
                if (templateChoose != null) {
                    defaultTemplate = Core.getWorldCreateConfig().getStringColored("Template.$templateChoose")
                }
                val item = basic.items[guiData.key]
                val itemMeta = item?.itemMeta
                itemMeta?.setDisplayName(itemMeta.displayName.replace("{template}", defaultTemplate))
                val lore =
                    itemMeta?.lore?.map { it.replace("{template}", defaultTemplate) }?.toMutableList() ?: continue
                itemMeta.lore = lore
                item.itemMeta = itemMeta
                basic.set(guiData.key, item)
                break
            }
        }
        return BasicCharMap(basic, charMap)
    }

    fun open(gui: String = "WorldCreate.yml", templateChoose: String? = null) {
        val basicCharMap = build(gui, templateChoose)
        val basic = basicCharMap.basic
        val charMap = basicCharMap.charMap
        var template: String? = null
        if (templateChoose != null) {
            template = templateChoose
        }
        basic.openAsync()
        //取消点击事件
        basic.onClick {
            it.isCancelled = true
        }
        for (guiData in charMap) {
            basic.onClick(guiData.key) {
                val type = guiData.value.type
                val value = guiData.value.value
                val commands = guiData.value.commands
                //执行命令
                if (commands != null) {
                    Core.runCommand(player, commands)
                }
                if (type != null) {
                    when (type) {
                        "Template" -> {
                            template = value!!
                            open(gui, template)
                        }

                        "CreateWorld" -> {
                            Local.createWorld(player.uniqueId, template)
                        }
                    }

                }
            }
        }
    }
}