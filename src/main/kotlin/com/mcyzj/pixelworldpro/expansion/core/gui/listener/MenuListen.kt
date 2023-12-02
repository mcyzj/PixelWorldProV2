package com.mcyzj.pixelworldpro.expansion.core.gui.listener

import com.mcyzj.pixelworldpro.expansion.core.gui.Core
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.PrepareAnvilEvent


class MenuListen : Listener {
    @EventHandler
    fun check(e: InventoryClickEvent) {
        Core.menuClick(e)
    }

    @EventHandler
    fun check(e: InventoryCloseEvent) {
        Core.menuClose(e)
    }
    @EventHandler
    fun checkAnvil(e: PrepareAnvilEvent) {
        Core.menuClick(e)
    }
}