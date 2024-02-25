package com.mcyzj.lib.plugin.file

import java.io.File
import java.net.URL
import java.net.URLDecoder

class Path {
    fun getJarPath(name: Class<out Any>): String? {
        var filePath: String
        val url: URL = name.getProtectionDomain().getCodeSource().getLocation()
        try {
            filePath = URLDecoder.decode(url.getPath(), "utf-8") // 转化为utf-8编码，支持中文
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        if (filePath.endsWith(".jar")) {
            // 可执行jar包运行的结果里包含".jar"
            // 获取jar包所在目录
            filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1)
        }

        val file = File(filePath)
        filePath = file.absolutePath //得到windows下的正确路径
        //System.out.println("jar包所在目录：$filePath")
        return filePath
    }
}