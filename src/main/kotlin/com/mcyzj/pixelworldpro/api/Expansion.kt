package com.mcyzj.pixelworldpro.api

import com.mcyzj.pixelworldpro.PixelWorldPro
import com.mcyzj.pixelworldpro.expansion.listener.ListenerManager
import com.mcyzj.pixelworldpro.expansion.`object`.bungee.ClientManager
import com.mcyzj.pixelworldpro.file.Config

abstract class Expansion protected constructor() {
    val logger = PixelWorldPro.instance.logger
    val bungee = Config.bungee.getBoolean("Enable")
    val listenerManager = ListenerManager
    val clientManager = ClientManager

    abstract fun onEnable()

    abstract fun onDisable()
}