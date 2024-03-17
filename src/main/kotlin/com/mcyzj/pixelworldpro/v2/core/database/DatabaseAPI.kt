package com.mcyzj.pixelworldpro.v2.core.database

import com.mcyzj.pixelworldpro.v2.core.world.dataclass.WorldData
import com.mcyzj.pixelworldpro.v2.core.world.dataclass.WorldCreateData
import com.xbaimiao.easylib.module.database.Ormlite
import org.json.simple.JSONObject
import java.util.*
import kotlin.collections.HashMap

interface DatabaseAPI {

    var ormlite: Ormlite
    /**
     * 创建一个世界记录
     */
    fun createWorldData(worldData: WorldCreateData): WorldData

    /**
     * 覆盖一个世界记录
     */
    fun setWorldData(worldData: WorldData)
    /**
     * 获取一个世界记录
     * @param id 世界id
     */
    fun getWorldData(id: Int): WorldData?
    /**
     * 获取一个世界记录
     * @param owner 玩家UUID
     */
    fun getWorldData(owner: UUID): WorldData?
    /**
     * 删除一个世界记录
     * @param id 世界id
     */
    fun deleteWorldData(id: Int)
    /**
     * 删除一个世界记录
     * @param owner 玩家UUID
     */
    fun deleteWorldData(owner: UUID)
    /**
     * 获取所有世界id map
     */
    fun getWorldIdMap(): HashMap<Int, WorldData>
    /**
     * 获取所有世界的uuid map
     */
    fun getWorldUUIDMap(): HashMap<UUID, WorldData>
    /**
     * 获取所有世界的uuid list
     */
    fun getWorldIdList(start:Int,number: Int): List<Int>
    /**
     * 获取所有世界的uuid list
     */
    fun getWorldUUIDList(start:Int,number: Int): List<UUID>

    fun joinToData(worldData: WorldData): JSONObject


}