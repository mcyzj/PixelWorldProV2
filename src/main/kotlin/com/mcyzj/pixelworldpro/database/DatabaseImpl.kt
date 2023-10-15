package com.mcyzj.pixelworldpro.database

import com.google.common.collect.Maps
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.j256.ormlite.dao.Dao
import com.mcyzj.pixelworldpro.PixelWorldPro
import com.mcyzj.pixelworldpro.api.interfaces.DatabaseApi
import com.mcyzj.pixelworldpro.dataclass.PlayerData
import com.mcyzj.pixelworldpro.dataclass.WorldData
import com.xbaimiao.easylib.module.database.Ormlite
import com.xbaimiao.easylib.module.utils.submit
import org.json.simple.JSONObject
import java.util.*
import kotlin.collections.HashMap


abstract class DatabaseImpl(ormlite: Ormlite) : DatabaseApi {
    private val worldTable: Dao<WorldDao, Int> = ormlite.createDao(WorldDao::class.java)
    private val playerTable: Dao<PlayerDao, Int> = ormlite.createDao(PlayerDao::class.java)
    private val logger = PixelWorldPro.instance.logger
    private val lang = PixelWorldPro.instance.lang

    private val asyncWrite = PixelWorldPro.instance.config.getBoolean("async.database.write")
    override fun setWorldData(worldData: WorldData) {
        submit(async = asyncWrite) {
            val json = joinToJson(worldData)
            val queryBuilder = worldTable.queryBuilder()
            queryBuilder.where().eq("id", worldData.id)
            var worldDao = queryBuilder.queryForFirst()
            if (worldDao == null) {
                worldDao = WorldDao()
                worldDao.owner = worldData.owner
                worldDao.data = json.toString()
                worldTable.create(worldDao)
            } else {
                worldDao.owner = worldData.owner
                worldDao.data = json.toString()
                worldTable.update(worldDao)
            }
        }
    }

    override fun getWorldData(id: Int): WorldData? {
        val queryBuilder = worldTable.queryBuilder()
        queryBuilder.where().eq("id", id)
        val worldDao = queryBuilder.queryForFirst()?:return null
        return getFromJson(worldDao)
    }

    override fun getWorldData(owner: UUID): WorldData? {
        val queryBuilder = worldTable.queryBuilder()
        queryBuilder.where().eq("owner", owner)
        val worldDao = queryBuilder.queryForFirst()?:return null
        return getFromJson(worldDao)
    }

    override fun deleteWorldData(id: Int) {
        val queryBuilder = worldTable.queryBuilder()
        queryBuilder.where().eq("id", id)
        val worldDao = queryBuilder.queryForFirst()?:return
        worldTable.delete(worldDao)
    }

    override fun deleteWorldData(owner: UUID) {
        val queryBuilder = worldTable.queryBuilder()
        queryBuilder.where().eq("owner", owner)
        val worldDao = queryBuilder.queryForFirst()?:return
        worldTable.delete(worldDao)
    }

    override fun getWorldIdMap(): HashMap<Int, WorldData> {
        val queryBuilder = worldTable.queryBuilder()
        val list = queryBuilder.query()
        val idMap = HashMap<Int, WorldData>()
        for (world in list){
            val worldData = getFromJson(world)?:continue
            idMap[worldData.id] = worldData
        }
        return idMap
    }

    override fun getWorldUUIDMap(): HashMap<UUID,WorldData> {
        val queryBuilder = worldTable.queryBuilder()
        val list = queryBuilder.query()
        val uuidMap = HashMap<UUID,WorldData>()
        for (world in list){
            val worldData = getFromJson(world)?:continue
            uuidMap[worldData.owner] = worldData
        }
        return uuidMap
    }

    override fun getPlayerData(uuid: UUID): PlayerData? {
        TODO("Not yet implemented")
    }

    override fun setPlayerData(uuid: UUID, playerData: PlayerData) {
        TODO("Not yet implemented")
    }
    private fun joinToJson(worldData: WorldData): JSONObject {
        val json = JSONObject()
        json["world"] = worldData.world
        val permission = JSONObject(worldData.permission)
        json["permission"] = permission
        val player = JSONObject(worldData.player)
        json["player"] = player
        val dimension = JSONObject(worldData.player)
        json["dimension"] = dimension
        return json
    }
    @Suppress("UNCHECKED_CAST")
    private fun getFromJson(worldDao: WorldDao): WorldData? {
        val id = worldDao.id
        val owner = worldDao.owner
        val dataString = worldDao.data
        val gson = Gson()
        val dataJson = gson.fromJson(dataString, JsonObject::class.java)
        val world = dataJson["world"].asString
        if (world == null){
            logger.warning("§aPixelWorldPro ${lang.getString("database.warning.world.worldIsNULL")}")
            return null
        }
        val permission = gson.fromJson(dataJson["permission"].asJsonObject, HashMap::class.java)
        if (permission == null){
            logger.warning("§aPixelWorldPro ${lang.getString("database.warning.world.permissionIsNULL")}")
            return null
        }
        val player = gson.fromJson(dataJson["player"].asJsonObject, HashMap::class.java)
        if (player == null){
            logger.warning("§aPixelWorldPro ${lang.getString("database.warning.world.permissionIsNULL")}")
            return null
        }
        val dimension = gson.fromJson(dataJson["dimension"].asJsonObject, HashMap::class.java)
        if (dimension == null){
            logger.warning("§aPixelWorldPro ${lang.getString("database.warning.world.permissionIsNULL")}")
            return null
        }
        return WorldData(
            id,
            owner,
            world,
            permission as HashMap<String, String>,
            player as HashMap<UUID, String>,
            dimension as HashMap<String, Boolean>
        )
    }
}