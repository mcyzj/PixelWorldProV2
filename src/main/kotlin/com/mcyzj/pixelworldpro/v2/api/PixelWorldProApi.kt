package com.mcyzj.pixelworldpro.v2.api

import com.mcyzj.pixelworldpro.v2.PixelWorldPro
import com.mcyzj.pixelworldpro.v2.world.PixelWorldProWorld
import com.mcyzj.pixelworldpro.v2.world.PixelWorldProWorldTemplate
import org.bukkit.Bukkit
import java.io.File
import java.util.*

class PixelWorldProApi {
    val database = PixelWorldPro.databaseApi
    fun getWorld(id: Int): PixelWorldProWorld? {
        val worldData = database.getWorldData(id) ?: return null
        return PixelWorldProWorld(worldData)
    }
    fun getWorld(owner: UUID): PixelWorldProWorld? {
        val worldData = database.getWorldData(owner) ?: return null
        return PixelWorldProWorld(worldData)
    }
    fun createWorld(owner: UUID, template: String?, seed: Long?) {
        val templates = if (template == null) {
            val templateFileList = File("./PixelWorldPro/template").listFiles()!!
            templateFileList[(Math.random() * templateFileList.size).toInt()].name
        } else {
            template
        }
        val templateData = PixelWorldProWorldTemplate(templates)
        templateData.seed = seed
        val world = templateData.createWorld(owner)
        world.load()
        val player = Bukkit.getPlayer(owner)
        if (player != null) {
            world.teleport(player)
        }
    }
}