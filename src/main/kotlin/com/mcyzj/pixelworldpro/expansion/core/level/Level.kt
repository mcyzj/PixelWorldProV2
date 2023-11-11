package com.mcyzj.pixelworldpro.expansion.core.level

import com.mcyzj.pixelworldpro.PixelWorldPro
import com.mcyzj.pixelworldpro.command.Admin
import com.mcyzj.pixelworldpro.expansion.core.level.admin.Command

object Level {
    private val adminCommand = Command()
    private val logger = PixelWorldPro.instance.logger
    fun enable(){
        logger.info("加载level核心组件")
        Admin.setCommand("PixelWorldPro_Croe_Level", adminCommand.level)
    }
}