package com.mcyzj.pixelworldpro.compress

import com.mcyzj.pixelworldpro.PixelWorldPro
import com.mcyzj.pixelworldpro.file.Config
import java.io.File
import java.io.IOException

object None {
    private val logger = PixelWorldPro.instance.logger
    private val config = PixelWorldPro.instance.config
    private val lang = PixelWorldPro.instance.lang
    private val file = Config.file
    fun toZip(from: String, to: String){
        try {
            Folder.create(file.getString("World.Path")!!, to)
            Folder.delete(to, "None")
            val formFile = File(file.getString("World.Server")!!, from)
            val toFile = File(file.getString("World.Path")!!, "$to/world")
            if (toFile.exists()){
                toFile.deleteRecursively()
            }
            toFile.mkdirs()
            formFile.copyRecursively(toFile)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun unZip(to: String, from: String){
        try {
            Folder.create(file.getString("World.Path")!!, to)
            val formFile = File(file.getString("World.Path")!!, "$from/world")
            val toFile = File(file.getString("World.Server")!!, to)
            if (toFile.exists()){
                toFile.deleteRecursively()
            }
            toFile.mkdirs()
            formFile.copyRecursively(toFile)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}