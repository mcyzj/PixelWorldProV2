package com.mcyzj.pixelworldpro.api.interfaces.core.bungee

import java.util.*
import java.util.concurrent.CompletableFuture

interface BungeeAPI {
    fun adminCreateWorld(owner: UUID, template: String?): CompletableFuture<Boolean>
    fun loadWorld(id: Int): CompletableFuture<Boolean>
    fun loadWorld(owner: UUID): CompletableFuture<Boolean>
    fun unloadWorld(id: Int): CompletableFuture<Boolean>
    fun unloadWorld(owner: UUID): CompletableFuture<Boolean>
}