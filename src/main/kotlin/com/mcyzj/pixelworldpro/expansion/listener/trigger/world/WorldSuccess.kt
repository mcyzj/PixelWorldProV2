package com.mcyzj.pixelworldpro.expansion.listener.trigger.world

import com.mcyzj.pixelworldpro.data.dataclass.WorldData
import com.mcyzj.pixelworldpro.expansion.listener.ListenerRegister

object WorldSuccess {
    fun createWorldSuccess(worldData: WorldData, template: String){
        val map = ListenerRegister.getWorldSuccessListener()
        for (value in map.values){
            try {
                value.createWorld(worldData, template)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }
    fun loadWorldSuccess(worldData: WorldData){
        val map = ListenerRegister.getWorldSuccessListener()
        for (value in map.values) {
            try {
                value.loadWorld(worldData)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun unloadWorldSuccess(worldData: WorldData){
        val map = ListenerRegister.getWorldSuccessListener()
        for (value in map.values) {
            try {
                value.unloadWorld(worldData)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun backupWorldSuccess(worldData: WorldData, save: Boolean?){
        val map = ListenerRegister.getWorldSuccessListener()
        for (value in map.values) {
            try {
                value.backupWorld(worldData, save)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}