package com.mcyzj.pixelworldpro

import com.mcyzj.pixelworldpro.file.Config
import com.mcyzj.pixelworldpro.server.Player
import com.mcyzj.pixelworldpro.world.Local
import com.xbaimiao.easylib.bridge.PlaceholderExpansion
import org.bukkit.OfflinePlayer


object Papi: PlaceholderExpansion() {
    private var config = Config.config
    private val database = PixelWorldPro.databaseApi
    override val identifier: String
        get() = config.getString("mainPapi")!!
    override val version: String
        get() = "2.0.0-alpha"

    override fun onRequest(p: OfflinePlayer, params: String): String? {
        val paramsList = params.split("_")
        when(paramsList[0]){
            "User" -> {
                if (paramsList.size < 2){
                    return null
                }
                when(paramsList[1]){
                    "WorldCreate" -> {
                        val worldData = database.getWorldData(p.uniqueId)
                        return (worldData != null).toString()
                    }
                    "WorldName" -> {
                        val worldData = database.getWorldData(p.uniqueId) ?: return null
                        return worldData.name
                    }
                    "WorldRealName" -> {
                        val worldData = database.getWorldData(p.uniqueId) ?: return null
                        return worldData.world
                    }
                    "WorldId" -> {
                        val worldData = database.getWorldData(p.uniqueId) ?: return null
                        return worldData.id.toString()
                    }
                    "WorldPermission" -> {
                        if (paramsList.size < 4){
                            return null
                        }
                        val worldData = database.getWorldData(p.uniqueId) ?: return null
                        val group = paramsList[2]
                        val permissionData = worldData.permission[group] ?: return null
                        val key = paramsList[3]
                        return permissionData[key]
                    }
                    "WorldPlayer" -> {
                        if (paramsList.size < 3){
                            return null
                        }
                        val worldData = database.getWorldData(p.uniqueId) ?: return null
                        val value = paramsList[2]
                        val player = Player.getOfflinePlayer(value)
                        if (player.uniqueId == worldData.owner){
                            val groupData = worldData.permission["Owner"] ?: return null
                            return groupData["Name"]
                        }
                        val group = worldData.player[player.uniqueId] ?: "Visitor"
                        val groupData = worldData.permission[group] ?: return null
                        return groupData["Name"]
                    }
                }
            }

            "Local" -> {
                val player = p.player ?: return null
                val uuid = Local.getWorldNameUUID(player.world.name) ?: return null
                if (paramsList.size < 2){
                    return null
                }
                when(paramsList[1]){
                    "WorldCreate" -> {
                        val worldData = database.getWorldData(uuid)
                        return (worldData != null).toString()
                    }
                    "WorldName" -> {
                        val worldData = database.getWorldData(uuid) ?: return null
                        return worldData.name
                    }
                    "WorldRealName" -> {
                        val worldData = database.getWorldData(uuid) ?: return null
                        return worldData.world
                    }
                    "WorldId" -> {
                        val worldData = database.getWorldData(uuid) ?: return null
                        return worldData.id.toString()
                    }
                    "WorldPermission" -> {
                        if (paramsList.size < 4){
                            return null
                        }
                        val worldData = database.getWorldData(uuid) ?: return null
                        val group = paramsList[2]
                        val permissionData = worldData.permission[group] ?: return null
                        val key = paramsList[3]
                        return permissionData[key]
                    }
                    "WorldPlayer" -> {
                        if (paramsList.size < 3){
                            return null
                        }
                        val worldData = database.getWorldData(uuid) ?: return null
                        val value = paramsList[2]
                        val offlinePlayer = Player.getOfflinePlayer(value)
                        if (offlinePlayer.uniqueId == worldData.owner){
                            val groupData = worldData.permission["Owner"] ?: return null
                            return groupData["Name"]
                        }
                        val group = worldData.player[offlinePlayer.uniqueId] ?: "Visitor"
                        val groupData = worldData.permission[group] ?: return null
                        return groupData["Name"]
                    }
                }
            }

            "Player" -> {
                if (paramsList.size < 3){
                    return null
                }
                val player = Player.getOfflinePlayer(paramsList[1])
                val uuid = player.uniqueId
                when(paramsList[2]){
                    "WorldCreate" -> {
                        val worldData = database.getWorldData(uuid)
                        return (worldData != null).toString()
                    }
                    "WorldName" -> {
                        val worldData = database.getWorldData(uuid) ?: return null
                        return worldData.name
                    }
                    "WorldRealName" -> {
                        val worldData = database.getWorldData(uuid) ?: return null
                        return worldData.world
                    }
                    "WorldId" -> {
                        val worldData = database.getWorldData(uuid) ?: return null
                        return worldData.id.toString()
                    }
                    "WorldPermission" -> {
                        if (paramsList.size < 5){
                            return null
                        }
                        val worldData = database.getWorldData(uuid) ?: return null
                        val group = paramsList[3]
                        val permissionData = worldData.permission[group] ?: return null
                        val key = paramsList[4]
                        return permissionData[key]
                    }
                    "WorldPlayer" -> {
                        if (paramsList.size < 4){
                            return null
                        }
                        val worldData = database.getWorldData(uuid) ?: return null
                        val value = paramsList[3]
                        val offlinePlayer = Player.getOfflinePlayer(value)
                        if (offlinePlayer.uniqueId == worldData.owner){
                            val groupData = worldData.permission["Owner"] ?: return null
                            return groupData["Name"]
                        }
                        val group = worldData.player[offlinePlayer.uniqueId] ?: "Visitor"
                        val groupData = worldData.permission[group] ?: return null
                        return groupData["Name"]
                    }
                }
            }

            "ID" -> {
                if (paramsList.size < 3){
                    return null
                }
                val id = try {
                    paramsList[1].toInt()
                } catch (_:Exception) {
                    return null
                }
                when(paramsList[2]){
                    "WorldCreate" -> {
                        val worldData = database.getWorldData(id)
                        return (worldData != null).toString()
                    }
                    "WorldName" -> {
                        val worldData = database.getWorldData(id) ?: return null
                        return worldData.name
                    }
                    "WorldRealName" -> {
                        val worldData = database.getWorldData(id) ?: return null
                        return worldData.world
                    }
                    "WorldId" -> {
                        val worldData = database.getWorldData(id) ?: return null
                        return worldData.id.toString()
                    }
                    "WorldPermission" -> {
                        if (paramsList.size < 5){
                            return null
                        }
                        val worldData = database.getWorldData(id) ?: return null
                        val group = paramsList[3]
                        val permissionData = worldData.permission[group] ?: return null
                        val key = paramsList[4]
                        return permissionData[key]
                    }
                    "WorldPlayer" -> {
                        if (paramsList.size < 4){
                            return null
                        }
                        val worldData = database.getWorldData(id) ?: return null
                        val value = paramsList[3]
                        val offlinePlayer = Player.getOfflinePlayer(value)
                        if (offlinePlayer.uniqueId == worldData.owner){
                            val groupData = worldData.permission["Owner"] ?: return null
                            return groupData["Name"]
                        }
                        val group = worldData.player[offlinePlayer.uniqueId] ?: "Visitor"
                        val groupData = worldData.permission[group] ?: return null
                        return groupData["Name"]
                    }
                }
            }
        }
        return null
    }
}