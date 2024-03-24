package com.mcyzj.lib.bukkit.menu

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent


class MenuListener : Listener {
    @EventHandler
    fun check(e: InventoryClickEvent) {
        val menu = MenuImpl.getOpenMenu(e.inventory) ?: return
        e.isCancelled = true

        menu.onClick(e.slot, e.isLeftClick, e.isRightClick)
    }

    @EventHandler
    fun check(e: InventoryCloseEvent) {
        MenuImpl.removeOpenMenu(e.inventory)
    }
}