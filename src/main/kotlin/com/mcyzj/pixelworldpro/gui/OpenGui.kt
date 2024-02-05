package com.mcyzj.pixelworldpro.gui

import com.mcyzj.pixelworldpro.gui.type.WorldCreate
import com.mcyzj.pixelworldpro.gui.type.WorldList
import com.mcyzj.pixelworldpro.file.Config
import com.xbaimiao.easylib.module.chat.BuiltInConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player

object OpenGui {
    private var gui = Config.gui
    fun open(player: Player, menu: String){
        val name = gui.getString("Command.$menu") ?: return
        val config = BuiltInConfiguration("gui/$name")
        when (config.getString("Type")){
            "WorldCreate" -> {
                WorldCreate().open(player, player, config, HashMap())
            }
            "WorldList" -> {
                WorldList().open(player, player, config, HashMap())
            }
        }
    }
    fun open(player: Player, menu: String, cache: HashMap<String, Any>){
        val name = gui.getString("Command.$menu") ?: return
        val config = BuiltInConfiguration("gui/$name")
        when (config.getString("Type")){
            "WorldCreate" -> {
                WorldCreate().open(player, player, config, cache)
            }
            "WorldList" -> {
                WorldList().open(player, player, config, cache)
            }
        }
    }
    fun open(player: Player, menu: YamlConfiguration, cache: HashMap<String, Any>){
        when (menu.getString("Type")){
            "WorldCreate" -> {
                WorldCreate().open(player, player, menu, cache)
            }
            "WorldList" -> {
                WorldList().open(player, player, menu, cache)
            }
        }
    }
}