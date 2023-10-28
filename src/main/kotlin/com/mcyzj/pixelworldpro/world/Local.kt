package com.mcyzj.pixelworldpro.world

import com.mcyzj.pixelworldpro.PixelWorldPro
import com.mcyzj.pixelworldpro.api.interfaces.WorldAPI
import com.mcyzj.pixelworldpro.config.Config
import java.io.File
import java.util.*
import java.util.concurrent.CompletableFuture

object Local {
    private val logger = PixelWorldPro.instance.logger
    private var config = Config.config
    private val lang = PixelWorldPro.instance.lang
    private val database = PixelWorldPro.databaseApi
    private var file = Config.file
    fun adminCreateWorld(owner: UUID, template: String?): CompletableFuture<Boolean>{
        val future = CompletableFuture<Boolean>()
        val temp = if (template == null){
            val templatePath = file.getString("Template.Path")
            if (templatePath == null){
                logger.warning("§aPixelWorldPro ${lang.getString("world.warning.template.pathNotSet")}")
                future.complete(false)
                return future
            } else {
                val templateFile = File(templatePath)
                val templateList = templateFile.list()!!
                templateList[Random().nextInt(templateList.size)]
            }
        }else{
            template
        }
        val worldApi = WorldAPI.Factory.get()
        return worldApi.createWorld(owner, temp)
    }
    fun adminLoadWorld(owner: UUID): CompletableFuture<Boolean>{
        val worldApi = WorldAPI.Factory.get()
        return worldApi.loadWorld(owner)
    }
    fun adminLoadWorld(id: Int): CompletableFuture<Boolean>{
        val worldApi = WorldAPI.Factory.get()
        return worldApi.loadWorld(id)
    }
    fun adminUnloadWorld(owner: UUID): CompletableFuture<Boolean>{
        val worldApi = WorldAPI.Factory.get()
        return worldApi.unloadWorld(owner)
    }
    fun adminUnloadWorld(id: Int): CompletableFuture<Boolean>{
        val worldApi = WorldAPI.Factory.get()
        return worldApi.unloadWorld(id)
    }

    fun createWorld(owner: UUID, template: String?){

        val temp = if (template == null){
            val templatePath = file.getString("Template.Path")
            if (templatePath == null){
                logger.warning("§aPixelWorldPro ${lang.getString("world.warning.template.pathNotSet")}")
                return
            } else {
                val templateFile = File(templatePath)
                val templateList = templateFile.list()!!
                templateList[Random().nextInt(templateList.size)]
            }
        }else{
            template
        }
        val worldApi = WorldAPI.Factory.get()
        val future = worldApi.createWorld(owner, temp)
        future.thenApply {

        }
    }
}