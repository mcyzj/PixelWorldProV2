package com.mcyzj.pixelworldpro.gui

import com.mcyzj.pixelworldpro.gui.GuiCore
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.PrepareAnvilEvent


class MenuListen : Listener {
    @EventHandler
    fun check(e: InventoryClickEvent) {
        GuiCore.menuClick(e)
    }

    @EventHandler
    fun check(e: InventoryCloseEvent) {
        GuiCore.menuClose(e)
    }
    @EventHandler
    fun checkAnvil(e: PrepareAnvilEvent) {
        GuiCore.menuClick(e)
    }
}