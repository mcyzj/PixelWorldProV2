package com.mcyzj.pixelworldpro.expansion

import com.mcyzj.pixelworldpro.api.Expansion
import com.mcyzj.pixelworldpro.data.dataclass.ExpansionData
import org.bukkit.Bukkit
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.YamlConfiguration
import java.io.*
import java.net.URLDecoder
import java.util.*
import java.util.jar.JarFile
import kotlin.collections.HashMap


object ExpansionManager{
    val loadExpansion = HashMap<String, Expansion>()
    val expansionDataMap = HashMap<String, ExpansionData>()

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

    fun loadAllExpansion() {
        val expansionFile = File("./plugins/PixelWorldProV2/Expansion")
        if (!expansionFile.exists()){
            expansionFile.mkdirs()
        }
        val expansionList = expansionFile.list()
        if (expansionList != null) {
            for (file in expansionList) {
                val expansion = File("./plugins/PixelWorldProV2/Expansion", file)
                if (expansion.isFile) {
                    JarFile(expansion).use { jar ->
                        Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 尝试读取本地${file}扩展")
                        val data = expansionDescription(jar)
                        if (data != null) {
                            val expansionData = buildExpansionData(data)
                            if (expansionData != null) {
                                if (expansionData.api < 2){
                                    Bukkit.getConsoleSender()
                                        .sendMessage("§4PixelWorldPro ${expansionData.name}[${file}] 正在使用过时的api版本")
                                    Bukkit.getConsoleSender()
                                        .sendMessage("§4PixelWorldPro 内置api版本：2")
                                    Bukkit.getConsoleSender()
                                        .sendMessage("§4PixelWorldPro ${expansionData.name}[${file}] 使用的API版本：${expansionData.api}")
                                }
                                if (expansionData.api > 2){
                                    Bukkit.getConsoleSender()
                                        .sendMessage("§4PixelWorldPro 无法理解 ${expansionData.name}[${file}] 使用的API版本")
                                    Bukkit.getConsoleSender()
                                        .sendMessage("§4PixelWorldPro 内置api版本：2")
                                    Bukkit.getConsoleSender()
                                        .sendMessage("§4PixelWorldPro ${expansionData.name}[${file}] 使用的API版本：${expansionData.api}")
                                }
                                Bukkit.getConsoleSender()
                                    .sendMessage("§aPixelWorldPro 加载扩展本地扩展 ${expansionData.name}[${file}]")
                                Bukkit.getConsoleSender()
                                    .sendMessage("§aPixelWorldPro 作者：${expansionData.author}")
                                Bukkit.getConsoleSender()
                                    .sendMessage("§aPixelWorldPro 版本：${expansionData.version}")
                                try {
                                    expansionDataMap[expansion.name] = expansionData
                                    ExpansionClassLoader(this, data, expansion, this.javaClass.classLoader, file)
                                } catch (e: Throwable){
                                    Bukkit.getConsoleSender()
                                        .sendMessage("§4PixelWorldPro ${expansionData.name}[${file}] 崩溃啦！")
                                    e.printStackTrace()
                                }
                            } else {
                                Bukkit.getConsoleSender().sendMessage("§4PixelWorldPro ${file}不是一个有效的扩展")
                            }
                        } else {
                            Bukkit.getConsoleSender().sendMessage("§4PixelWorldPro ${file}不是一个有效的扩展")
                        }
                    }
                }
            }
        }
    }

    @Throws(IOException::class, InvalidConfigurationException::class)
    private fun expansionDescription(jar: JarFile): YamlConfiguration? {
        return try {
            val entry = jar.getJarEntry("PixelWorldPro.yml")
            val reader = BufferedReader(InputStreamReader(jar.getInputStream(entry),"UTF-8"))
            val data = YamlConfiguration()
            data.load(reader)
            reader.close()
            data
        }catch (e:Exception){
            Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 该扩展不是一个有效的PixelWorldPro扩展")
            null
        }
    }

    private fun buildExpansionData(config: YamlConfiguration): ExpansionData? {
        return try {
            val name = URLDecoder.decode(config.getString("name")!!, "UTF-8")
            val api = config.getInt("api")
            val author = URLDecoder.decode(config.getString("author")!!, "UTF-8")
            val version = URLDecoder.decode(config.getString("version")!!, "UTF-8")
            ExpansionData(name, api, author, version)
        }catch (e: Exception){
            e.printStackTrace()
            null
        }
    }
}