package com.mcyzj.pixelworldpro.gui

import org.bukkit.entity.Player

object Open {
    fun open(player: Player, gui: String){
        WorldCreate(player).open()
    }
}