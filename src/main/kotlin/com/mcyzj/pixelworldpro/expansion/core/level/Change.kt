package com.mcyzj.pixelworldpro.expansion.core.level

import com.mcyzj.pixelworldpro.data.dataclass.WorldData
import com.mcyzj.pixelworldpro.expansion.listener.trigger.level.Level
import com.mcyzj.pixelworldpro.file.Config
import com.mcyzj.pixelworldpro.file.World
import org.bukkit.configuration.file.YamlConfiguration

object Change {
    fun setLevel(worldData: WorldData, level: Int){
        val data = YamlConfiguration()
        data.set("level", level)
        val levelConfig = World.getWorldConfig(worldData, "level", data)
        levelConfig.set("level", level)
        World.saveWorldConfig(worldData, "level", levelConfig)
        Level.levelChange(worldData, level)
        if (Config.bungee.getBoolean("Enable")){
            Send.send(worldData.id, level)
        }
    }
    fun getLevel(worldData: WorldData): Int {
        val data = YamlConfiguration()
        data.set("level", 1)
        val levelConfig = World.getWorldConfig(worldData, "level", data)
        return levelConfig.getInt("level")
    }
    fun addLevel(worldData: WorldData){
        val data = YamlConfiguration()
        data.set("level", 1)
        val levelConfig = World.getWorldConfig(worldData, "level", data)
        val level = levelConfig.getInt("level") + 1
        levelConfig.set("level", level)
        World.saveWorldConfig(worldData, "level", levelConfig)
        Level.levelChange(worldData, level)
        if (Config.bungee.getBoolean("Enable")){
            Send.send(worldData.id, level)
        }
    }
    fun addLevel(worldData: WorldData, number: Int){
        val data = YamlConfiguration()
        data.set("level", 1)
        val levelConfig = World.getWorldConfig(worldData, "level", data)
        val level = levelConfig.getInt("level") + number
        levelConfig.set("level", level)
        World.saveWorldConfig(worldData, "level", levelConfig)
        Level.levelChange(worldData, level)
        if (Config.bungee.getBoolean("Enable")){
            Send.send(worldData.id, level)
        }
    }
    fun removeLevel(worldData: WorldData){
        val data = YamlConfiguration()
        data.set("level", 1)
        val levelConfig = World.getWorldConfig(worldData, "level", data)
        val level = if (levelConfig.getInt("level") - 1 < 1 ){
            1
        } else {
            levelConfig.getInt("level") - 1
        }
        levelConfig.set("level", level)
        World.saveWorldConfig(worldData, "level", levelConfig)
        Level.levelChange(worldData, level)
        if (Config.bungee.getBoolean("Enable")){
            Send.send(worldData.id, level)
        }
    }
    fun removeLevel(worldData: WorldData, number: Int){
        val data = YamlConfiguration()
        data.set("level", 1)
        val levelConfig = World.getWorldConfig(worldData, "level", data)
        val level = if (levelConfig.getInt("level") - number < 1 ){
            1
        } else {
            levelConfig.getInt("level") - number
        }
        levelConfig.set("level", level)
        World.saveWorldConfig(worldData, "level", levelConfig)
        Level.levelChange(worldData, level)
        if (Config.bungee.getBoolean("Enable")){
            Send.send(worldData.id, level)
        }
    }
}