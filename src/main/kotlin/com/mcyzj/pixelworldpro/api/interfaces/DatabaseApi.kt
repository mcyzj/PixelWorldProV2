package com.mcyzj.pixelworldpro.api.interfaces

import com.mcyzj.pixelworldpro.PixelWorldPro
import com.mcyzj.pixelworldpro.dataclass.PlayerData
import com.mcyzj.pixelworldpro.dataclass.WorldData
import java.util.*
import kotlin.collections.HashMap

interface DatabaseApi {

    /**
     * 创建或覆盖一个世界记录
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
     * 获取所有世界id map,按照在线人数排序
     */
    fun getWorldIdMap(): HashMap<Int, WorldData>
    /**
     * 获取所有世界的uuid map,按照在线人数排序
     */
    fun getWorldUUIDMap(): HashMap<UUID,WorldData>
    /**
     * 创建或覆盖一个玩家记录,此操作为同步数据库操作
     */
    fun setPlayerData(uuid: UUID, playerData: PlayerData)
    /**
     * 获取一个玩家记录，此操作为同步数据库操作
     */
    fun getPlayerData(uuid: UUID): PlayerData?
    fun getInstance(): DatabaseApi {
        return PixelWorldPro.databaseApi
    }


}