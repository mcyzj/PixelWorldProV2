package com.mcyzj.pixelworldpro.bungee

import com.mcyzj.pixelworldpro.file.Config
import com.mcyzj.pixelworldpro.data.dataclass.ServerData
import com.mcyzj.pixelworldpro.data.dataclass.WorldData
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.lang.Thread.sleep

object System {
    private var file = Config.file
    private fun getServerConfig(): YamlConfiguration {
        val config = File(file.getString("World.Path"), "Server.yml")
        val data = YamlConfiguration()
        if (!config.exists()) {
            config.createNewFile()
        }
        data.load(config)
        return data
    }

    private fun saveServerConfig(data: YamlConfiguration){
        val config = File(file.getString("World.Path"), "Server.yml")
        if (!config.exists()) {
            config.createNewFile()
        }
        data.save(config)
    }

    fun getAllServer(): HashMap<String, ServerData> {
        val data = getServerConfig()
        val server = HashMap<String, ServerData>()
        for (key in data.getKeys(false)){
            val serverData = data.getConfigurationSection(key) ?: continue
            server[key] = ServerData(
                serverData.getString("ShowName")!!,
                key,
                serverData.getString("Mode")!!,
                serverData.getDouble("Tps"),
                serverData.getString("Type")
            )
        }
        return server
    }

    fun setServer(){
        Thread {
            while (true) {
                val local = Server.getLocalServer()
                val config = getServerConfig()
                config.set("${local.realName}.ShowName", local.showName)
                config.set("${local.realName}.Mode", local.mode)
                config.set("${local.realName}.Tps", local.tps)
                config.set("${local.realName}.Type", local.type)
                saveServerConfig(config)
                sleep(30000)
            }
        }.start()
    }
    fun getServer(realName: String): ServerData? {
        return getAllServer()[realName]
    }
    private fun getWorldConfig(): YamlConfiguration {
        val config = File(file.getString("World.Path"), "World.yml")
        val data = YamlConfiguration()
        if (!config.exists()) {
            config.createNewFile()
        }
        data.load(config)
        return data
    }

    private fun saveWorldConfig(data: YamlConfiguration){
        val config = File(file.getString("World.Path"), "World.yml")
        if (!config.exists()) {
            config.createNewFile()
        }
        data.save(config)
    }
    fun setWorldLock(world: WorldData){
        val worldConfig = getWorldConfig()
        val server = Server.getLocalServer()
        worldConfig.set("${world.world}.server", server.realName)
        saveWorldConfig(worldConfig)
    }
    fun getWorldLock(world: WorldData): String? {
        val worldConfig = getWorldConfig()
        return worldConfig.getString("${world.world}.server")
    }
    fun removeWorldLock(world: WorldData){
        val worldConfig = getWorldConfig()
        worldConfig.set(world.world, null)
        saveWorldConfig(worldConfig)
    }
}