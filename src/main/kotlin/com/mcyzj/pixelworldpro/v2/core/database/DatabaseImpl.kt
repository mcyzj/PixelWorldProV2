package com.mcyzj.pixelworldpro.v2.core.database

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.j256.ormlite.dao.Dao
import com.mcyzj.pixelworldpro.v2.core.PixelWorldPro
import com.mcyzj.pixelworldpro.v2.core.database.dao.WorldDao
import com.mcyzj.pixelworldpro.v2.core.util.Config
import com.mcyzj.pixelworldpro.v2.core.world.dataclass.WorldCreateData
import com.mcyzj.pixelworldpro.v2.core.world.dataclass.WorldData
import com.mcyzj.lib.core.database.Ormlite
import com.mcyzj.lib.bukkit.submit
import com.mcyzj.pixelworldpro.v2.core.world.WorldDimensionData
import org.json.simple.JSONObject
import java.util.*


class DatabaseImpl : DatabaseAPI {

    override var ormlite: Ormlite = DataBase.getOrmlite()

    private val worldDaoTable: Dao<WorldDao, Int> = ormlite.createDao(WorldDao::class.java)
    private val logger = PixelWorldPro.instance.logger
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
        if (worldData.owner == UUID.fromString("00000000-0000-0000-0000-000000000000")) {
            return
        }
        submit(async = asyncWrite) {
            val json = joinToData(worldData)
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
        if (worldDao.owner == UUID.fromString("00000000-0000-0000-0000-000000000000")) {
            return WorldData(
                //世界ID
                -1,
                //世界主人
                UUID.fromString("00000000-0000-0000-0000-000000000000"),
                //世界名称
                "delete",
                //世界权限表
                HashMap<String, HashMap<String, String>>(),
                //玩家权限表
                HashMap<UUID, String>(),
                //世界维度表
                HashMap<String, WorldDimensionData>(),
                //世界模式
                "close"
            )
        }
        return getFromJson(worldDao)
    }

    override fun getWorldData(owner: UUID): WorldData? {
        if (owner == UUID.fromString("00000000-0000-0000-0000-000000000000")) {
            return WorldData(
                //世界ID
                -1,
                //世界主人
                UUID.fromString("00000000-0000-0000-0000-000000000000"),
                //世界名称
                "delete",
                //世界权限表
                HashMap<String, HashMap<String, String>>(),
                //玩家权限表
                HashMap<UUID, String>(),
                //世界维度表
                HashMap<String, WorldDimensionData>(),
                //世界模式
                "close"
            )
        }
        val queryBuilder = worldDaoTable.queryBuilder()
        queryBuilder.where().eq("owner", owner)
        val worldDao = queryBuilder.queryForFirst()?:return null
        return getFromJson(worldDao)
    }

    override fun deleteWorldData(worldData: WorldData) {
        val queryBuilder = worldDaoTable.queryBuilder()
        queryBuilder.where().eq("id", worldData.id)
        val worldDao = queryBuilder.queryForFirst()?:return
        worldDao.owner = UUID.fromString("00000000-0000-0000-0000-000000000000")
        worldDao.data = "close"
        worldDaoTable.update(worldDao)
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
    override fun joinToData(worldData: WorldData): JSONObject {
        val json = JSONObject()
        json["name"] = worldData.name
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
        val permissionData = JSONObject()
        val permissionList = worldData.permission.keys
        for (key in permissionList){
            val permissionJson = JSONObject(worldData.permission[key])
            permissionData[key] = permissionJson
        }
        json["permission"] = permissionData
        val player = JSONObject(worldData.player)
        json["player"] = player
        json["type"] = worldData.type
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
        val type = try {
            dataJson["type"].asString
        } catch (_:Exception) {
            "local"
        }
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

        return WorldData(
            id,
            owner,
            name,
            permissionMap,
            player as HashMap<UUID, String>,
            dimension,
            type
        )
    }
}