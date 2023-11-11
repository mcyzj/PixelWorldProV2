package com.mcyzj.pixelworldpro.expansion

import com.mcyzj.pixelworldpro.api.Expansion
import org.bukkit.Bukkit
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.YamlConfiguration
import java.io.*
import java.util.*
import java.util.jar.JarFile
import kotlin.collections.HashMap


object ExpansionManager{
    val loadExpansion = HashMap<String, Expansion>()

    private val classes: MutableMap<String, Class<*>>
    private val loaders: MutableMap<Expansion, ExpansionClassLoader>

    init {
        loaders = HashMap()
        classes = HashMap()
    }

    fun getClassByName(name: String): Any? {
        try {
            return classes.getOrDefault(name, loaders.values.stream().filter(Objects::nonNull).map { l: ExpansionClassLoader -> l.findClass(name, false) }.filter(Objects::nonNull).findFirst().orElse(null))
        } catch (ignored: java.lang.Exception) {
            // Ignored.
        }
        return null
    }

    fun setClass(name: String, clazz: Class<*>) {
        classes.putIfAbsent(name, clazz)
    }

    fun loadExpansion() {
        val expansionFile = File("./plugins/PixelWorldProV2/Expansion")
        if (!expansionFile.exists()){
            expansionFile.mkdirs()
        }
        val expansionList = expansionFile.list()
        if (expansionList != null) {
            for (name in expansionList) {
                val expansion = File("./plugins/PixelWorldProV2/Expansion", name)
                JarFile(expansion).use { jar ->
                    Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 尝试读取本地${name}扩展")
                    val data = expansionDescription(jar)
                    if (data != null) {
                        if (data.getInt("api-version") >= 1) {
                            Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 加载扩展本地扩展${name}")
                            ExpansionClassLoader(this, data, expansion, this.javaClass.classLoader, name)
                        } else {
                            Bukkit.getConsoleSender().sendMessage("§4PixelWorldPro 无法理解${name}使用的API版本")
                        }
                    } else {
                        Bukkit.getConsoleSender().sendMessage("§4PixelWorldPro ${name}不是一个有效的扩展")
                    }
                }
            }
        }
    }

    @Throws(IOException::class, InvalidConfigurationException::class)
    private fun expansionDescription(jar: JarFile): YamlConfiguration? {
        return try {
            val entry = jar.getJarEntry("PixelWorldPro.yml")
            val reader = BufferedReader(InputStreamReader(jar.getInputStream(entry)))
            val data = YamlConfiguration()
            data.load(reader)
            reader.close()
            data
        }catch (e:Exception){
            Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 该扩展不是一个有效的PixelWorldPro扩展")
            null
        }
    }
}

data class ExpansionData(
    val name: String,
    val api: Int,
    val author: String,
    val version: String
)