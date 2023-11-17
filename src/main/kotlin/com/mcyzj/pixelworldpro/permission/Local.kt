package com.mcyzj.pixelworldpro.permission

import com.mcyzj.pixelworldpro.data.dataclass.PermissionData
import com.mcyzj.pixelworldpro.data.dataclass.PermissionGroupData
import com.mcyzj.pixelworldpro.data.dataclass.PermissionUpData
import com.mcyzj.pixelworldpro.file.Config

object Local {
    private var config = Config.permission
    private fun buildLevel(): HashMap<String, PermissionData> {
        //循环依次构建权限组
        val permissionMap = HashMap<String, PermissionData>()
        for (permission in config.getConfigurationSection("Permission")!!.getKeys(false)){
            val permissionConfig = config.getConfigurationSection("Permission.$permission")!!
            //循环构建不同权限组的数据
            val groupMap = HashMap<String, PermissionGroupData>()
            for (group in permissionConfig.getKeys(false)){
                val levelMap = HashMap<Int, PermissionUpData>()
                //构建基础值
                val groupConfig = permissionConfig.getConfigurationSection(group)!!
                val least = groupConfig.getInt("Least")
                val max = groupConfig.getInt("Max")
                //构建扩展槽消耗
                val useConfig = groupConfig.getConfigurationSection("Use")!!
                var level = 0
                var lastLevel = useConfig.getKeys(false).first().toInt()
                //构建第一个扩展消耗数据
                val firstLevelConfig = useConfig.getConfigurationSection(lastLevel.toString())!!
                val firstItemConfig = firstLevelConfig.getConfigurationSection("Item")
                val firstItemMap = HashMap<String, Int>()
                if (firstItemConfig != null) {
                    if (firstItemConfig.getKeys(false).isNotEmpty()) {
                        for (key in firstItemConfig.getKeys(false)) {
                            firstItemMap[key as String] = firstItemConfig.getInt(key)
                        }
                    }
                }
                val firstPermissionUpData = PermissionUpData(
                    firstLevelConfig.getDouble("Points"),
                    firstLevelConfig.getDouble("Money"),
                    firstItemMap
                )
                levelMap[lastLevel] = firstPermissionUpData
                //构建剩余扩展
                while (level <= max){
                    if (level.toString() !in useConfig.getKeys(false)) {
                        val levelConfig = useConfig.getConfigurationSection(lastLevel.toString())!!
                        val itemConfig = levelConfig.getConfigurationSection("Item")
                        val itemMap = HashMap<String, Int>()
                        if (itemConfig != null) {
                            if (itemConfig.getKeys(false).isNotEmpty()) {
                                for (key in itemConfig.getKeys(false)) {
                                    itemMap[key as String] = itemConfig.getInt(key)
                                }
                            }
                        }
                        val permissionUpData = PermissionUpData(
                            levelConfig.getDouble("Points"),
                            levelConfig.getDouble("Money"),
                            itemMap
                        )
                        levelMap[level] = permissionUpData
                        lastLevel = level
                    } else {
                        levelMap[level] = levelMap[lastLevel]!!
                    }
                    level ++
                }
                val permissionGroupData = PermissionGroupData(
                    least,
                    max,
                    levelMap
                )
                groupMap[group] = permissionGroupData
            }
            val permissionData = PermissionData(
                permission,
                groupMap
            )
            permissionMap[permission] = permissionData
        }
        return permissionMap
    }
}