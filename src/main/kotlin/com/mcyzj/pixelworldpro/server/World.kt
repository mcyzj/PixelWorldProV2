package com.mcyzj.pixelworldpro.server

import com.mcyzj.pixelworldpro.PixelWorldPro
import com.mcyzj.pixelworldpro.api.interfaces.core.world.WorldAPI
import com.mcyzj.pixelworldpro.file.Config
import com.mcyzj.pixelworldpro.world.WorldImpl
import org.bukkit.World
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

object World {
    private val logger = PixelWorldPro.instance.logger
    private var config = Config.config
    private val lang = PixelWorldPro.instance.lang
    private var file = Config.file

    var localWorld = hashMapOf<Int, World>()
    fun checkTemplate(template: String):Boolean{
        val templatePath = file.getString("Template.Path")
        if (templatePath == null){
            Icon.warning()
            logger.warning("${lang.getString("world.warning.template.pathNotSet")}")
            return false
        }
        val templateFile = File(templatePath, template)
        if (!templateFile.exists()){
            Icon.warning()
            logger.warning("${lang.getString("world.warning.template.pathNotFound")}")
            return false
        }
        if ("level.dat" in templateFile.list()!!){
            Icon.warning()
            logger.warning("${lang.getString("world.warning.template.worldInMain")}")
            return false
        }
        if ("world" !in templateFile.list()!!){
            Icon.warning()
            logger.warning("${lang.getString("world.warning.template.noWorld")}")
            return false
        }
        val worldFile = File(templateFile.path, "world")
        if ("uid.dat" in worldFile.list()!!){
            Icon.warning()
            logger.warning("${lang.getString("world.warning.template.uidInWorld")}")
            return false
        }
        return true
    }

    private fun getLockConfig(): YamlConfiguration {
        val config = File(file.getString("World.Server"), "world.yml")
        val data = YamlConfiguration()
        if (!config.exists()) {
            config.createNewFile()
        }
        data.load(config)
        return data
    }

    private fun saveLockConfig(data: YamlConfiguration){
        val config = File(file.getString("World.Server"), "world.yml")
        if (!config.exists()) {
            config.createNewFile()
        }
        data.save(config)
    }

    fun getLock(): ArrayList<Int>? {
        val data = getLockConfig()
        val worldList = data.getList("load") ?: return null
        val newWorldList = ArrayList<Int>()
        for (value in worldList){
            newWorldList.add(value as Int)
        }
        return newWorldList
    }

    fun setLock(id: Int){
        val data = getLockConfig()
        val worldList = data.getList("load")
        if (worldList == null){
            val newWorldList = ArrayList<Int>()
            newWorldList.add(id)
            data.set("load", newWorldList)
            saveLockConfig(data)
            return
        }
        val newWorldList = ArrayList<Int>()
        for (value in worldList){
            newWorldList.add(value as Int)
        }
        if (id in worldList){
            return
        }
        newWorldList.add(id)
        data.set("load", newWorldList)
        saveLockConfig(data)
    }

    fun removeLock(id: Int){
        val data = getLockConfig()
        val worldList = data.getList("load") ?: return
        val newWorldList = ArrayList<Int>()
        for (value in worldList){
            newWorldList.add(value as Int)
        }
        if (id !in worldList){
            return
        }
        newWorldList.remove(id)
        data.set("load", newWorldList)
        saveLockConfig(data)
    }

    fun setDeleteLock(file: String){
        val data = getLockConfig()
        val deleteList = data.getList("delete")
        if (deleteList == null){
            val newDeleteList = ArrayList<String>()
            newDeleteList.add(file)
            data.set("delete", newDeleteList)
            saveLockConfig(data)
            return
        }
        val newDeleteList = ArrayList<String>()
        for (value in deleteList){
            newDeleteList.add(value as String)
        }
        if (file in deleteList){
            return
        }
        newDeleteList.add(file)
        data.set("delete", newDeleteList)
        saveLockConfig(data)
    }

    fun removeDeleteLock(file: String){
        val data = getLockConfig()
        val deleteList = data.getList("delete") ?: return
        val newDeleteList = ArrayList<String>()
        for (value in deleteList){
            newDeleteList.add(value as String)
        }
        if (file !in deleteList){
            return
        }
        newDeleteList.remove(file)
        data.set("delete", newDeleteList)
        saveLockConfig(data)
    }

    private fun getDeleteLock(): ArrayList<String>? {
        val data = getLockConfig()
        val deleteList = data.getList("delete") ?: return null
        val newDeleteList = ArrayList<String>()
        for (value in deleteList){
            newDeleteList.add(value as String)
        }
        return newDeleteList
    }

    fun deleteLock(){
        val deleteList = getDeleteLock() ?: return
        for (path in deleteList){
            val file = File(path)
            if (file.exists()){
                WorldAPI.Factory.get().zipWorld(file.name, file.name)
                file.deleteRecursively()
            }
            removeDeleteLock(path)
        }
    }
}