package com.mcyzj.pixelworldpro.file

import com.mcyzj.pixelworldpro.data.dataclass.WorldData
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

object World {
    private var fileConfig = Config.file
    fun saveWorldConfig(world: WorldData, name: String, configData: YamlConfiguration){
        val worldFile = File(fileConfig.getString("World.Path"), world.world)
        val config = File(worldFile, "/config/$name.yml")
        configData.save(config)
    }
    fun getWorldConfig(world: WorldData, name: String, default: YamlConfiguration): YamlConfiguration {
        val worldFile = File(fileConfig.getString("World.Path"), world.world)
        val config = File(worldFile, "/config/$name.yml")
        val data = YamlConfiguration()
        if (!config.exists()) {
            config.createNewFile()
            default.save(config)
        }
        data.load(config)
        data.save(config)
        return data
    }
}
