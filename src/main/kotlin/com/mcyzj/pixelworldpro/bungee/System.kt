package com.mcyzj.pixelworldpro.bungee

import com.mcyzj.pixelworldpro.config.Config
import com.mcyzj.pixelworldpro.dataclass.ServerData
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

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

    private fun getAllServer(): HashMap<String, ServerData> {
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

    fun getServer(realName: String): ServerData? {
        return getAllServer()[realName]
    }
}