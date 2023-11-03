package com.mcyzj.pixelworldpro.compress

import java.io.File

object Folder {
    fun create(folder: String, to: String){
        val file = File(folder, to)
        file.mkdirs()
        File(file, "config").mkdirs()
        File(file, "backup").mkdirs()
        File(file, "log").mkdirs()
    }
}