package com.mcyzj.pixelworldpro.expansion.core.level

import com.mcyzj.pixelworldpro.PixelWorldPro
import com.mcyzj.pixelworldpro.command.Admin
import com.mcyzj.pixelworldpro.expansion.core.level.admin.Command
import com.mcyzj.pixelworldpro.expansion.`object`.bungee.ClientManager

object Level {
    private val adminCommand = Command()
    private val logger = PixelWorldPro.instance.logger
    fun enable(){
        logger.info("加载level核心组件")
        Admin.setCommand("PixelWorldPro_Core_Level", adminCommand.level)
        ClientManager.register(Client, arrayListOf("LevelChange"))
    }
}