package com.mcyzj.lib.bukkit.menu

import com.mcyzj.pixelworldpro.v2.core.menu.WorldList


object MenuImpl {
    private val menuDriver = HashMap<String, MenuAPI>()

    fun registerMenuDriver(name: String, driver: MenuAPI) {
        menuDriver[name] = driver
    }

    fun getMenuDriver(name: String?): MenuAPI {
        val driver = menuDriver[name]?: WorldList()
        return driver::class.java.newInstance()
    }
}