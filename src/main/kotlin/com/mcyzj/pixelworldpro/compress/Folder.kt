package com.mcyzj.pixelworldpro.compress

import com.mcyzj.pixelworldpro.file.Config
import java.io.File

object Folder {
    private val file = Config.file
    fun create(folder: String, to: String){
        val file = File(folder, to)
        file.mkdirs()
        File(file, "config").mkdirs()
        File(file, "backup").mkdirs()
        File(file, "log").mkdirs()
    }

    fun delete(world: String, type: String){
        when (type){
            "None" -> {
                val zip = File(file.getString("World.Path"), "$world/$world.zip")
                if (zip.exists()){
                    zip.delete()
                }
                val `7z` = File(file.getString("World.Path"), "$world/$world.7z")
                if (`7z`.exists()){
                    `7z`.delete()
                }
            }
            "Zip" -> {
                val worldFile = File(file.getString("World.Path"), "$world/world")
                if (worldFile.exists()){
                    worldFile.delete()
                }
                val `7z` = File(file.getString("World.Path"), "$world/$world.7z")
                if (`7z`.exists()){
                    `7z`.delete()
                }
            }
            "7z" -> {
                val worldFile = File(file.getString("World.Path"), "$world/world")
                if (worldFile.exists()){
                    worldFile.delete()
                }
                val zip = File(file.getString("World.Path"), "$world/$world.zip")
                if (zip.exists()){
                    zip.delete()
                }
            }
        }
    }
}