package com.mcyzj.pixelworldpro.v2.util

import com.mcyzj.lib.plugin.file.Path
import com.mcyzj.pixelworldpro.v2.PixelWorldPro
import java.io.File

object Install {
    private val config = Config.config
    fun start(){
        //创建初始文件夹
        if (config.getString("dataPath") == null) {
            config.set("dataPath", "./PixelWorldPro")
            config.saveToFile()
        }
        val path = config.getString("dataPath")?: "./PixelWorldPro"
        val dataFile = File(path)
        if (!dataFile.exists()) {
            dataFile.mkdirs()
        }
        //创建世界存放文件夹
        if (!File(dataFile, "world").exists()) {
            File(dataFile, "world").mkdirs()
        }
        //创建缓存存放文件夹
        val cacheFile = File(dataFile, "cache")
        if (!cacheFile.exists()) {
            cacheFile.mkdirs()
        }
        //创建世界缓存文件夹
        if (!File(cacheFile, "world").exists()) {
            File(cacheFile, "world").mkdirs()
        }
        //创建模板范例
        val templateFile = File(dataFile, "template")
        if (!templateFile.exists()) {
            templateFile.mkdirs()
            val exampleFile = File(templateFile, "example")
            exampleFile.mkdirs()
            File(exampleFile, "data").mkdirs()
            val exampleWorldFile = File(exampleFile, "world")
            exampleWorldFile.mkdirs()
            File(exampleWorldFile, "world").mkdirs()
            File(exampleWorldFile, "nether").mkdirs()
            File(exampleWorldFile, "the_end").mkdirs()
            val readme = File(exampleWorldFile, "ReadMe请阅读我!!!.txt")
            readme.createNewFile()
            readme.writeText("world -> 存放主世界|put world\nnether -> 存放地狱世界|put nether world\nthe_end -> 存放末地|put the_end world")
        }
        //如果不在服务端目录下则映射
        if (path != "./PixelWorldPro") {
            val pathList = Path().getJarPath(this::class.java)!!.split("\\") as ArrayList
            pathList.removeLast()
            val local = pathList.joinToString("\\")
            Runtime.getRuntime().exec("cmd /c MKLINK /d /j $local\\PixelWorldPro ${path.replace("/", "\\")}")
            Runtime.getRuntime().exec("cmd /c MKLINK /d /j $local\\world\\PixelWorldPro ${path.replace("/", "\\")}")
        }
        PixelWorldPro.instance.log.info(Config.getLang().getString("plugin.install.successful"))
        config.set("install", null)
        config.saveToFile()
    }
}