package com.mcyzj.pixelworldpro.server

import com.mcyzj.pixelworldpro.PixelWorldPro
import java.io.File

object World {
    private val logger = PixelWorldPro.instance.logger
    private val config = PixelWorldPro.instance.config
    private val lang = PixelWorldPro.instance.lang
    fun checkTemplate(template: String):Boolean{
        val templatePath = config.getString("WorldTemplatePath")
        if (templatePath == null){
            logger.warning("§aPixelWorldPro ${lang.getString("world.warning.templatePathNotSet")}")
        }
        val templateFile = File(templatePath + template)
        return true
    }
}