package com.mcyzj.pixelworldpro.api.interfaces.core.world

import com.mcyzj.pixelworldpro.data.dataclass.ResultData
import com.mcyzj.pixelworldpro.data.dataclass.WorldData
import com.mcyzj.pixelworldpro.world.DimensionImpl
import com.mcyzj.pixelworldpro.world.WorldImpl
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture

interface DimensionAPI {
    //以下为独立世界-独立世界api
    /**
     * 在本地服务器创建世界
     * @param worldData 世界数据 WorldData
     * @param dimension 维度名称 String
     */
    fun createDimension(worldData: WorldData, dimension: String): CompletableFuture<ResultData>
    /**
     * 在本地服务器加载维度
     * @param worldData 世界数据 WorldData
     * @param dimension 维度名称 String
     */
    fun loadDimension(worldData: WorldData, dimension: String): CompletableFuture<ResultData>
    /**
     * 在本地服务器卸载指定维度
     * @param worldData 世界数据 WorldData
     * @param dimension 维度名称 String
     */
    fun unloadDimension(worldData: WorldData, dimension: String): CompletableFuture<ResultData>
    /**
     * 在本地服务器内传送维度
     * @param player 传送玩家 Player
     * @param worldData 世界数据 WorldData
     * @param dimension 维度名称 String
     */
    fun tpDimension(player: Player, worldData: WorldData, dimension: String): CompletableFuture<ResultData>
    object Get {
        fun getLocal() : DimensionAPI {
            return DimensionImpl
        }
    }
}