package com.mcyzj.pixelworldpro.v2.core.expansion

import com.mcyzj.lib.plugin.file.Path
import com.mcyzj.pixelworldpro.v2.core.PixelWorldPro
import com.mcyzj.pixelworldpro.v2.core.util.Config
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.YamlConfiguration
import java.io.*
import java.net.URLDecoder
import java.util.*
import java.util.jar.JarFile
import kotlin.collections.HashMap

@Suppress("unused")
object ExpansionManager{
    val loadExpansion = HashMap<String, ExpansionData>()
    val expansionDataMap = HashMap<String, ExpansionData>()
    val expansionFileMap = HashMap<String, File> ()
    private val lang = Config.getLang()
    private val log = PixelWorldPro.instance.log

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

    fun enableAllExpansion() {
        val dataList = expansionDataMap.values
        for (data in dataList) {
            enableExpansion(data.name)
        }
    }

    fun enableExpansion(expansionName: String) {
        val expansionData = expansionDataMap[expansionName] ?: return
        val expansionFile = expansionFileMap[expansionName] ?: return
        val enableMsg = lang.getString("expansion.enable.tip") ?: "加载扩展{expansion.name}\n作者：{expansion.auther}\n版本：{expansion.version}"
        enableMsg.replace("{expansion.name}", expansionData.name)
        enableMsg.replace("{expansion.auther}", expansionData.author)
        enableMsg.replace("{expansion.version}", expansionData.version)
        log.info(enableMsg)
        try {
            ExpansionClassLoader(this, expansionData, expansionFile, this.javaClass.classLoader)
            loadExpansion[expansionData.name] = expansionData
        } catch (e: Throwable){
            log.info(lang.getString("expansion.enable.crash")?.replace("{expansion.name}", expansionData.name))
            e.printStackTrace()
        }
    }

    fun loadAllExpansion() {
        val local = Path().getJarPath(this::class.java)!!
        val expansionFile = File("${local}/PixelWorldProV2/expansion")
        if (!expansionFile.exists()){
            expansionFile.mkdirs()
        }
        val expansionList = expansionFile.list()
        if (expansionList != null) {
            for (file in expansionList) {
                val expansion = File("${local}/PixelWorldProV2/expansion", file)
                if (expansion.isFile) {
                    loadExpansion(expansionFile)
                }
            }
        }
    }

    fun loadExpansion(file: File) {
        JarFile(file).use { jar ->
            log.info(lang.getString("expansion.load")?.replace("{file}", file.name))
            val data = expansionDescription(jar)
            if (data != null) {
                val expansionData = buildExpansionData(data)
                if (expansionData != null) {
                    expansionDataMap[expansionData.name] = expansionData
                    expansionFileMap[expansionData.name] = file
                } else {
                    log.info(lang.getString("expansion.load.notFoundData")?.replace("{file}", jar.name))
                }
            } else {
                log.info(lang.getString("expansion.load.notFoundData")?.replace("{file}", jar.name))
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
            null
        }
    }

    private fun buildExpansionData(config: YamlConfiguration): ExpansionData? {
        return try {
            val name = URLDecoder.decode(config.getString("name")!!, "UTF-8")
            val api = config.getInt("api")
            val author = URLDecoder.decode(config.getString("author")!!, "UTF-8")
            val version = URLDecoder.decode(config.getString("version")!!, "UTF-8")
            val main = URLDecoder.decode(config.getString("main")!!, "UTF-8")
            ExpansionData(name, api, author, version, main)
        }catch (e: Exception){
            e.printStackTrace()
            null
        }
    }
}