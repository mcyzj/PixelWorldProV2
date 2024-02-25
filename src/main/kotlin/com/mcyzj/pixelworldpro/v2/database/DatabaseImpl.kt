package com.mcyzj.pixelworldpro.v2.database

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.j256.ormlite.dao.Dao
import com.mcyzj.lib.plugin.database.SQLite
import com.mcyzj.pixelworldpro.v2.PixelWorldPro
import com.mcyzj.pixelworldpro.v2.database.dao.WorldDao
import com.mcyzj.pixelworldpro.v2.util.Config
import com.mcyzj.pixelworldpro.v2.world.dataclass.WorldCreateData
import com.mcyzj.pixelworldpro.v2.world.dataclass.WorldData
import com.xbaimiao.easylib.module.database.Ormlite
import com.xbaimiao.easylib.module.utils.submit
import org.json.simple.JSONObject
import java.util.*


abstract class DatabaseImpl(ormlite: Ormlite) : DatabaseAPI {
    private val worldDaoTable: Dao<WorldDao, Int> = ormlite.createDao(WorldDao::class.java)
    private val logger = PixelWorldPro().logger
    private val lang = Config.getLang()
    private val config = Config.config

    private val asyncWrite = config.getBoolean("async.database")
    override fun createWorldData(worldData: WorldCreateData): WorldData {
        val json = createJoinToJson(worldData)
        val queryBuilder = worldDaoTable.queryBuilder()
        queryBuilder.where().eq("owner", worldData.owner)
        var worldDao = queryBuilder.queryForFirst()
        if (worldDao == null) {
            worldDao = WorldDao()
            worldDao.owner = worldData.owner
            worldDao.data = json.toString()
            worldDaoTable.create(worldDao)
        } else {
            worldDao.owner = worldData.owner
            worldDao.data = json.toString()
            worldDaoTable.update(worldDao)
        }
        return getWorldData(worldData.owner)!!
    }
    override fun setWorldData(worldData: WorldData) {
        submit(async = asyncWrite) {
            val json = joinToJson(worldData)
            val queryBuilder = worldDaoTable.queryBuilder()
            queryBuilder.where().eq("id", worldData.id)
            var worldDao = queryBuilder.queryForFirst()
            if (worldDao == null) {
                worldDao = WorldDao()
                worldDao.owner = worldData.owner
                worldDao.data = json.toString()
                worldDaoTable.create(worldDao)
            } else {
                worldDao.owner = worldData.owner
                worldDao.data = json.toString()
                worldDaoTable.update(worldDao)
            }
        }
    }

    override fun getWorldData(id: Int): WorldData? {
        val queryBuilder = worldDaoTable.queryBuilder()
        queryBuilder.where().eq("id", id)
        val worldDao = queryBuilder.queryForFirst()?:return null
        return getFromJson(worldDao)
    }

    override fun getWorldData(owner: UUID): WorldData? {
        val queryBuilder = worldDaoTable.queryBuilder()
        queryBuilder.where().eq("owner", owner)
        val worldDao = queryBuilder.queryForFirst()?:return null
        return getFromJson(worldDao)
    }

    override fun deleteWorldData(id: Int) {
        val queryBuilder = worldDaoTable.queryBuilder()
        queryBuilder.where().eq("id", id)
        val worldDao = queryBuilder.queryForFirst()?:return
        worldDaoTable.delete(worldDao)
    }

    override fun deleteWorldData(owner: UUID) {
        val queryBuilder = worldDaoTable.queryBuilder()
        queryBuilder.where().eq("owner", owner)
        val worldDao = queryBuilder.queryForFirst()?:return
        worldDaoTable.delete(worldDao)
    }

    override fun getWorldIdMap(): HashMap<Int, WorldData> {
        val queryBuilder = worldDaoTable.queryBuilder()
        val list = queryBuilder.query()
        val idMap = HashMap<Int, WorldData>()
        for (world in list){
            val worldData = getFromJson(world)?:continue
            idMap[worldData.id] = worldData
        }
        return idMap
    }

    override fun getWorldUUIDMap(): HashMap<UUID, WorldData> {
        val queryBuilder = worldDaoTable.queryBuilder()
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
        val dataMap = this.getWorldIdMap()
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
        val dataMap = this.getWorldUUIDMap()
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
        val dimension = JSONObject()
        //for (key in worldData.dimension.keys){
        //    val dimensionData = worldData.dimension[key]!!
        //    val dimensionJson = JSONObject()
        //    dimensionJson["name"] = dimensionData.name
        //    dimensionJson["world"] = dimensionData.world
        //    dimensionJson["environment"] = dimensionData.environment
        //    dimensionJson["type"] = dimensionData.type
        //    dimensionJson["creator"] = dimensionData.creator
        //    dimension[key] = dimensionJson
        //}
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
        val dimension = JSONObject()
        //for (key in worldData.dimension.keys){
        //    val dimensionData = worldData.dimension[key]!!
        //    val dimensionJson = JSONObject()
        //    dimensionJson["name"] = dimensionData.name
        //    dimensionJson["world"] = dimensionData.world
        //    dimensionJson["environment"] = dimensionData.environment
        //    dimensionJson["type"] = dimensionData.type
        //    dimensionJson["creator"] = dimensionData.creator
        //    dimension[key] = dimensionJson
        //}
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
        /*
        val dimension = HashMap<String, WorldDimensionData>()
        val dimensionJson = gson.fromJson(dataJson["dimension"].asJsonObject, JsonObject::class.java)
        for (key in dimensionJson.entrySet()){
            val dimensionDataJson = (dimensionJson[key.key] ?: continue).asJsonObject
            val dimensionData = if (dimensionDataJson.has("creator")) {
                WorldDimensionData(
                    dimensionDataJson["name"].asString,
                    dimensionDataJson["world"].asString,
                    dimensionDataJson["environment"].asString,
                    dimensionDataJson["type"].asString,
                    //dimensionDataJson["creator"].asString
                    null
                    )
            }else{
                WorldDimensionData(
                    dimensionDataJson["name"].asString,
                    dimensionDataJson["world"].asString,
                    dimensionDataJson["environment"].asString,
                    dimensionDataJson["type"].asString,
                    null
                    )
            }
            dimension[key.key] = dimensionData
        }

         */
        return WorldData(
            id,
            owner,
            name,
            world,
            permissionMap,
            player as HashMap<UUID, String>,
            //dimension
        )
    }
}