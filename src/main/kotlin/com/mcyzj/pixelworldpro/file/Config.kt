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
    var gui = BuiltInConfiguration("Gui.yml")
    fun reload(){
        config = BuiltInConfiguration("Config.yml")
        file = BuiltInConfiguration("File.yml")
        world = BuiltInConfiguration("World.yml")
        permission = BuiltInConfiguration("Permission.yml")
        bungee = BuiltInConfiguration("BungeeSet.yml")
        level = BuiltInConfiguration("Level.yml")
        gui = BuiltInConfiguration("Gui.yml")
        PixelWorldPro.instance.reloadAll()
    }
    fun update(){
        //File配置文件更新
        when (file.getInt("Version")){
            1 -> {
                file.set("Version", 2)
                file.set("Backup.time", 1800)
                file.set("Backup.number", 32)
                file.saveToFile()
            }
        }
        //World配置文件更新
        while (world.getInt("Version") < 3) {
            when (world.getInt("Version")) {
                1 -> {
                    world.set("Version", 2)
                    world.set("Create.Name", "{Player.Name}的世界")
                    world.saveToFile()
                }

                2 -> {
                    world.set("Version", 3)
                    world.set("Name.Use.Default.Permission", "pwp.user.name")
                    world.set("Name.Use.Default.Money", 0.0)
                    world.set("Name.Use.Default.Point", 100.0)
                    world.set("Name.Vip.Default.Permission", "group.vip")
                    world.set("Name.Vip.Default.Money", 0.0)
                    world.set("Name.Vip.Default.Point", 0.0)
                    world.saveToFile()
                }
            }
        }
        //Permission配置文件更新
        when (permission.getInt("Version")){
            1 -> {
                permission.set("Version", 2)
                permission.set("World.Owner.Name", "拥有者")
                permission.set("World.Member.Name", "信任者")
                permission.set("World.Visitor.Name", "参观者")
                permission.set("World.BlackList.Name", "黑名单")
                permission.saveToFile()
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