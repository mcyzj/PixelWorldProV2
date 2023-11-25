package com.mcyzj.pixelworldpro.data.database

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.j256.ormlite.dao.Dao
import com.mcyzj.pixelworldpro.PixelWorldPro
import com.mcyzj.pixelworldpro.api.interfaces.core.database.DatabaseAPI
import com.mcyzj.pixelworldpro.data.dataclass.PlayerData
import com.mcyzj.pixelworldpro.data.dataclass.WorldCreateData
import com.mcyzj.pixelworldpro.data.dataclass.WorldData
import com.xbaimiao.easylib.module.database.Ormlite
import com.xbaimiao.easylib.module.utils.submit
import org.json.simple.JSONObject
import java.util.*
import kotlin.collections.HashMap


abstract class DatabaseImpl(ormlite: Ormlite) : DatabaseAPI {
    private val worldTable: Dao<WorldDao, Int> = ormlite.createDao(WorldDao::class.java)
    private val playerTable: Dao<PlayerDao, Int> = ormlite.createDao(PlayerDao::class.java)
    private val logger = PixelWorldPro.instance.logger
    private val lang = PixelWorldPro.instance.lang

    private val asyncWrite = PixelWorldPro.instance.config.getBoolean("async.database.write")
    override fun createWorldData(worldData: WorldCreateData): WorldData {
        val json = createJoinToJson(worldData)
        val queryBuilder = worldTable.queryBuilder()
        queryBuilder.where().eq("owner", worldData.owner)
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
        return getWorldData(worldData.owner)!!
    }
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

    override fun getWorldUUIDMap(): HashMap<UUID, WorldData> {
        val queryBuilder = worldTable.queryBuilder()
        val list = queryBuilder.query()
        val uuidMap = HashMap<UUID, WorldData>()
        for (world in list){
            val worldData = getFromJson(world)?:continue
            uuidMap[worldData.owner] = worldData
        }
        return uuidMap
    }

    override fun getWorldIdList(start:Int,number: Int): List<Int> {
        val list = mutableListOf<Int>()
        val dataMap = PixelWorldPro.databaseApi.getWorldIdMap()
        for (data in dataMap) {
            val id = data.key
            list.add(id)
        }
        if (list.size < start) {
            return listOf()
        }
        if (list.size < start + number) {
            return list.subList(start, list.size)
        }
        return list.subList(start, start + number)
    }

    override fun getWorldUUIDList(start:Int,number: Int): List<UUID> {
        val list = mutableListOf<UUID>()
        val dataMap = PixelWorldPro.databaseApi.getWorldUUIDMap()
        for (data in dataMap) {
            val uuid = data.key
            list.add(uuid)
        }
        if (list.size < start) {
            return listOf()
        }
        if (list.size < start + number) {
            return list.subList(start, list.size)
        }
        return list.subList(start, start + number)
    }

    override fun getPlayerData(uuid: UUID): PlayerData? {
        TODO("Not yet implemented")
    }

    override fun setPlayerData(uuid: UUID, playerData: PlayerData) {
        TODO("Not yet implemented")
    }
    override fun joinToJson(worldData: WorldData): JSONObject {
        val json = JSONObject()
        json["name"] = worldData.name
        json["world"] = worldData.world
        val permissionData = JSONObject()
        val permissionList = worldData.permission.keys
        for (key in permissionList){
            val permissionJson = JSONObject(worldData.permission[key])
            permissionData[key] = permissionJson
        }
        json["permission"] = permissionData
        val player = JSONObject(worldData.player)
        json["player"] = player
        val dimension = JSONObject(worldData.player)
        json["dimension"] = dimension
        return json
    }
    private fun createJoinToJson(worldData: WorldCreateData): JSONObject {
        val json = JSONObject()
        json["name"] = worldData.name
        json["world"] = worldData.world
        val permissionData = JSONObject()
        val permissionList = worldData.permission.keys
        for (key in permissionList){
            val permissionJson = JSONObject(worldData.permission[key])
            permissionData[key] = permissionJson
        }
        json["permission"] = permissionData
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
        val name = dataJson["name"].asString
        val world = dataJson["world"].asString
        if (world == null){
            logger.warning("§aPixelWorldPro ${lang.getString("database.warning.world.worldIsNULL")}")
            return null
        }
        val permission = gson.fromJson(dataJson["permission"].asJsonObject, JsonObject::class.java)
        if (permission == null){
            logger.warning("§aPixelWorldPro ${lang.getString("database.warning.world.permissionIsNULL")}")
            return null
        }
        val permissionMap = HashMap<String, HashMap<String, String>>()
        for (key in permission.asMap().keys){
            val permissionData = gson.fromJson(permission.asMap()[key], HashMap::class.java)
            permissionMap[key] = permissionData as HashMap<String, String>
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
            name,
            world,
            permissionMap,
            player as HashMap<UUID, String>,
            dimension as HashMap<String, Boolean>
        )
    }
}