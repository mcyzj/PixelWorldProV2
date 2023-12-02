package com.mcyzj.pixelworldpro.expansion.listener

import com.mcyzj.pixelworldpro.api.interfaces.event.level.LevelAPI
import com.mcyzj.pixelworldpro.api.interfaces.event.world.WorldAPI
import com.mcyzj.pixelworldpro.api.interfaces.event.world.WorldSuccessAPI
import com.mcyzj.pixelworldpro.data.dataclass.ExpansionData
import com.mcyzj.pixelworldpro.expansion.ExpansionManager
import java.io.File

object ListenerRegister {
    private val worldSuccessListener = HashMap<ExpansionData, WorldSuccessAPI>()
    fun registerWorldSuccessListener(listener: WorldSuccessAPI){
        val l = ExpansionManager.getClassByName(Throwable().stackTrace[1].className) as Class<*>
        val path = l.protectionDomain.codeSource.location.file
        val expansion = File(path)
        val expansionData = ExpansionManager.expansionDataMap[expansion.name]!!
        worldSuccessListener[expansionData] = listener
    }
    fun unregisterWorldSuccessListener(){
        val l = ExpansionManager.getClassByName(Throwable().stackTrace[1].className) as Class<*>
        val path = l.protectionDomain.codeSource.location.file
        val expansion = File(path)
        val expansionData = ExpansionManager.expansionDataMap[expansion.name]!!
        worldSuccessListener.remove(expansionData)
    }
    fun getWorldSuccessListener(): HashMap<ExpansionData, WorldSuccessAPI> {
        return worldSuccessListener
    }

    private val worldListener = HashMap<ExpansionData, WorldAPI>()
    fun registerWorldListener(listener: WorldAPI){
        val l = ExpansionManager.getClassByName(Throwable().stackTrace[1].className) as Class<*>
        val path = l.protectionDomain.codeSource.location.file
        val expansion = File(path)
        val expansionData = ExpansionManager.expansionDataMap[expansion.name]!!
        worldListener[expansionData] = listener
    }
    fun unregisterWorldListener(){
        val l = ExpansionManager.getClassByName(Throwable().stackTrace[1].className) as Class<*>
        val path = l.protectionDomain.codeSource.location.file
        val expansion = File(path)
        val expansionData = ExpansionManager.expansionDataMap[expansion.name]!!
        worldListener.remove(expansionData)
    }
    fun getWorldListener(): HashMap<ExpansionData, WorldAPI> {
        return worldListener
    }

    private val levelListener = HashMap<ExpansionData, LevelAPI>()

    fun registerLevelListener(listener: LevelAPI){
        val l = ExpansionManager.getClassByName(Throwable().stackTrace[1].className) as Class<*>
        val path = l.protectionDomain.codeSource.location.file
        val expansion = File(path)
        val expansionData = ExpansionManager.expansionDataMap[expansion.name]!!
        levelListener[expansionData] = listener
    }
    fun unregisterLevelListener(){
        val l = ExpansionManager.getClassByName(Throwable().stackTrace[1].className) as Class<*>
        val path = l.protectionDomain.codeSource.location.file
        val expansion = File(path)
        val expansionData = ExpansionManager.expansionDataMap[expansion.name]!!
        levelListener.remove(expansionData)
    }
    fun getLevelListener(): HashMap<ExpansionData, LevelAPI> {
        return levelListener
    }
}