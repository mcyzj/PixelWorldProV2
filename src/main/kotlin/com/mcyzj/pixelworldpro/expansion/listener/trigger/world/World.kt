package com.mcyzj.pixelworldpro.expansion.listener.trigger.world

import com.mcyzj.pixelworldpro.data.dataclass.WorldData
import com.mcyzj.pixelworldpro.expansion.listener.ListenerRegister
import org.bukkit.World
import org.bukkit.WorldCreator
import java.util.*

object World {
    fun createWorld(owner: UUID, template: String){
        val map = ListenerRegister.getWorldListener()
        for (value in map.values){
            try {
                value.createWorld(owner, template)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }
    fun loadWorld(worldData: WorldData){
        val map = ListenerRegister.getWorldListener()
        for (value in map.values) {
            try {
                value.loadWorld(worldData)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun unloadWorld(worldData: WorldData){
        val map = ListenerRegister.getWorldListener()
        for (value in map.values) {
            try {
                value.unloadWorld(worldData)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun backupWorld(worldData: WorldData, save: Boolean?){
        val map = ListenerRegister.getWorldListener()
        for (value in map.values) {
            try {
                value.backupWorld(worldData, save)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onWorldFileLoad(worldData: WorldData, worldCreator: WorldCreator): WorldCreator{
        val map = ListenerRegister.getWorldListener()
        var newCreator = worldCreator
        for (value in map.values) {
            try {
                newCreator = value.onWorldFileLoad(worldData, newCreator)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return newCreator
    }

    fun worldFileLoadSuccess(worldData: WorldData, world: World){
        val map = ListenerRegister.getWorldListener()
        for (value in map.values) {
            try {
                value.worldFileLoadSuccess(worldData, world)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}