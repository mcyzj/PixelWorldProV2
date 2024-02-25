package com.mcyzj.lib.plugin.file

import com.xbaimiao.easylib.module.utils.colored
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

/**
 * jar外配置文件读取
 */
@Suppress("unused")
class BuiltOutConfiguration(fileName: String) : YamlConfiguration() {

    val file: File

    init {
        file = File(fileName)
        if (!file.exists()) {
            file.mkdirs()
            file.deleteRecursively()
            file.createNewFile()
        }
        super.load(file)
    }

    fun getStringColored(path: String): String {
        return super.getString(path).colored()
    }

    fun getStringListColored(path: String): List<String> {
        return super.getStringList(path).colored()
    }

    fun saveToFile() {
        super.save(file)
    }

}