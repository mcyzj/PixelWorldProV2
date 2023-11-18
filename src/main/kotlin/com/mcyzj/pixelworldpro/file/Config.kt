package com.mcyzj.pixelworldpro.file

import com.mcyzj.pixelworldpro.PixelWorldPro
import com.mcyzj.pixelworldpro.data.dataclass.WorldData
import com.xbaimiao.easylib.module.chat.BuiltInConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

object Config {
    var config = BuiltInConfiguration("Config.yml")
    var file = BuiltInConfiguration("File.yml")
    var world = BuiltInConfiguration("World.yml")
    var permission = BuiltInConfiguration("Permission.yml")
    var bungee = BuiltInConfiguration("BungeeSet.yml")
    var level = BuiltInConfiguration("Level.yml")
    fun reload(){
        config = BuiltInConfiguration("Config.yml")
        file = BuiltInConfiguration("File.yml")
        world = BuiltInConfiguration("World.yml")
        permission = BuiltInConfiguration("Permission.yml")
        bungee = BuiltInConfiguration("BungeeSet.yml")
        level = BuiltInConfiguration("Level.yml")
        PixelWorldPro.instance.reloadAll()
    }
    fun update(){
        //config
        //file
        when (file.getInt("Version")){
            1 -> {
                file.set("Version", 2)
                file.set("Backup.time", 1800)
                file.set("Backup.number", 32)
                file.saveToFile()
            }
        }
    }

    fun saveWorldConfig(world: WorldData, configData: YamlConfiguration){
        val worldFile = File(file.getString("World.Path"), world.world)
        val config = File(worldFile, "/World.yml")
        if (!config.exists()){
            config.createNewFile()
        }
        configData.save(config)
    }
    fun getWorldConfig(world: WorldData): YamlConfiguration {
        val worldFile = File(file.getString("World.Path"), world.world)
        val config = File(worldFile, "/World.yml")
        val data = YamlConfiguration()
        if (!config.exists()) {
            config.createNewFile()
        }
        data.load(config)
        return data
    }
}