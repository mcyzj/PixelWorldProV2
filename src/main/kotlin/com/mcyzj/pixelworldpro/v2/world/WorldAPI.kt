package com.mcyzj.pixelworldpro.v2.world

import com.mcyzj.pixelworldpro.v2.world.dataclass.WorldData
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
    fun zipWorld(world: PixelWorldProWorld)
    /**
     * 解压世界
     */
    fun unzipWorld(world: PixelWorldProWorld)
    /**
     * 还原世界备份
     */
    fun restoreBackup(worldData: WorldData, backup: File): CompletableFuture<Boolean>
}