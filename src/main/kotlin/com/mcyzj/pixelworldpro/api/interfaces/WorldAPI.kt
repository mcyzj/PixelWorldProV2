package com.mcyzj.pixelworldpro.api.interfaces

import com.mcyzj.pixelworldpro.world.WorldImpl
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
     * 使用拥有者uuid在本地服务器加载世界
     * @param owner 世界拥有者 UUID
     */
    fun loadWorld(owner: UUID): CompletableFuture<Boolean>
    /**
     * 使用世界id卸载本地服务器中的世界
     * @param id 世界id Int
     */
    fun unloadWorld(id: Int): CompletableFuture<Boolean>
    /**
     * 使用拥有者uuid卸载本地服务器中的世界
     * @param owner 世界拥有者 UUID
     */
    fun unloadWorld(owner: UUID): CompletableFuture<Boolean>
    object Factory {
        fun get() : WorldAPI{
            return WorldImpl
        }
    }
}