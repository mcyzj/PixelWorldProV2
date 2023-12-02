package com.mcyzj.pixelworldpro.api.objects

import com.mcyzj.pixelworldpro.data.dataclass.WorldData
import com.mcyzj.pixelworldpro.expansion.ExpansionManager
import com.mcyzj.pixelworldpro.expansion.ExpansionManager.getClassByName
import com.mcyzj.pixelworldpro.file.World
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.YamlConfiguration
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.util.jar.JarFile

object Config {
    @Throws(IOException::class, InvalidConfigurationException::class)
    fun buildConfig(config: String): YamlConfiguration {
        val l = getClassByName(Throwable().stackTrace[1].className) as Class<*>
        val path = l.protectionDomain.codeSource.location.file
        val expansion = File(path)
        val expansionData = ExpansionManager.expansionDataMap[expansion.name]!!
        val expansionConfig = File("./plugins/PixelWorldProV2/Expansion", expansionData.name)
        if (!expansionConfig.exists()){
            expansionConfig.mkdirs()
        }
        val getConfig = File(expansionConfig, config)
        val data = YamlConfiguration()
        if (!getConfig.exists()){
            getConfig.createNewFile()
            JarFile(expansion).use { jar ->
                val entry = jar.getJarEntry(config)
                val reader = BufferedReader(InputStreamReader(jar.getInputStream(entry),"UTF-8"))
                data.load(reader)
                reader.close()
                data.save(getConfig)
                return data
            }
        }
        data.load(getConfig)
        return data
    }

    fun getWorldConfig(world: WorldData, name: String, default: YamlConfiguration): YamlConfiguration {
        return World.getWorldConfig(world, name, default)
    }

    fun saveWorldConfig(world: WorldData, name: String, configData: YamlConfiguration){
        World.saveWorldConfig(world, name, configData)
    }
}