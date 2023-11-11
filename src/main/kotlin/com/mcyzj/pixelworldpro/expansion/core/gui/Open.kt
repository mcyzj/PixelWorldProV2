package com.mcyzj.pixelworldpro.expansion.core.gui

import org.bukkit.entity.Player

object Open {
    fun open(player: Player, gui: String){
        when (gui){
            "create" -> {
                WorldCreate(player).open()
            }
            "list" -> {
                WorldList(player).open()
            }
        }
    }
}