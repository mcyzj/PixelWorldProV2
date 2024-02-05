package com.mcyzj.pixelworldpro.api.interfaces.core.world

import com.mcyzj.pixelworldpro.data.dataclass.WorldData
import com.mcyzj.pixelworldpro.world.WorldImpl
import java.io.File
import java.util.UUID
import java.util.concurrent.CompletableFuture

interface WorldAPI {
    //以下为独立世界-主世界api
    /**
     * 在本地服务器创建世界
     * @param owner 世界拥有者 UUID
     * @param template 模板名称 String
     */
    fun createWorld(owner: UUID, template: String): CompletableFuture<Boolean>
    /**
     * 使用世界id在本地服务器加载世界
     * @param id 世界id Int
     */
    fun loadWorld(id: Int): CompletableFuture<Boolean>
    /**
     * 使用世界id卸载本地服务器中的世界
     * @param id 世界id Int
     */
    fun unloadWorld(id: Int): CompletableFuture<Boolean>
    /**
     * 使用世界id备份本地服务器中的世界
     * @param id 世界id Int
     */
    fun backupWorld(id: Int, save: Boolean?)

    /**
     * 压缩世界
     */
    fun zipWorld(from: String, to: String)
    /**
     * 解压世界
     */
    fun unzipWorld(zip: String, to: String)
    /**
     * 还原世界备份
     */
    fun restoreBackup(worldData: WorldData, backup: File): CompletableFuture<Boolean>
    object Factory {
        fun get() : WorldAPI {
            return WorldImpl
        }
    }
}