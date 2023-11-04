package com.mcyzj.pixelworldpro.server

import com.mcyzj.pixelworldpro.PixelWorldPro
import com.mcyzj.pixelworldpro.config.Config
import com.mcyzj.pixelworldpro.world.WorldImpl
import com.xbaimiao.easylib.module.chat.BuiltInConfiguration
import org.bukkit.World
import java.io.File

object World {
    private val logger = PixelWorldPro.instance.logger
    private var config = Config.config
    private val lang = PixelWorldPro.instance.lang
    private var file = Config.file

    var localWorld = hashMapOf<Int, World>()
    fun checkTemplate(template: String):Boolean{
        val templatePath = file.getString("Template.Path")
        if (templatePath == null){
            Icon.warning()
            logger.warning("${lang.getString("world.warning.template.pathNotSet")}")
            return false
        }
        val templateFile = File(templatePath, template)
        if (!templateFile.exists()){
            Icon.warning()
            logger.warning("${lang.getString("world.warning.template.pathNotFound")}")
            return false
        }
        if ("level.dat" in templateFile.list()!!){
            Icon.warning()
            logger.warning("${lang.getString("world.warning.template.worldInMain")}")
            return false
        }
        if ("world" !in templateFile.list()!!){
            Icon.warning()
            logger.warning("${lang.getString("world.warning.template.noWorld")}")
            return false
        }
        val worldFile = File(templateFile.path, "world")
        if ("uid.dat" in worldFile.list()!!){
            Icon.warning()
            logger.warning("${lang.getString("world.warning.template.uidInWorld")}")
            return false
        }
        return true
    }
}