package com.mcyzj.pixelworldpro.v2.core.util

import com.dongzh1.pixelworldpro.old.PixelWorldPro
import com.dongzh1.pixelworldpro.world.Dimension
import com.mcyzj.lib.bukkit.bridge.replacePlaceholder
import com.mcyzj.lib.plugin.Logger
import com.mcyzj.pixelworldpro.v2.core.database.DataBase
import com.mcyzj.pixelworldpro.v2.core.permission.PermissionImpl
import com.mcyzj.pixelworldpro.v2.core.world.PixelWorldProWorld
import com.mcyzj.pixelworldpro.v2.core.world.WorldDimensionData
import com.mcyzj.pixelworldpro.v2.core.world.dataclass.WorldCreateData
import org.bukkit.Bukkit
import java.io.File
import java.util.*
import kotlin.collections.HashMap

class UpData {
    fun fromV1() {
        val oldFile = File("./OldWorlds")
        oldFile.mkdirs()
        val oldDataList = PixelWorldPro.databaseApi.getWorldList(0, 1000000)
        for (oldDataUUID in oldDataList) {
            try {
                Logger.info("迁移数据${oldDataUUID}")
                val oldData = PixelWorldPro.databaseApi.getWorldData(oldDataUUID)
                if (oldData == null) {
                    Logger.info("迁移数据${oldDataUUID}失败：数据库空指针")
                    continue
                }
                val oldWorldFile = File(oldFile, oldData.worldName)
                if (!oldWorldFile.exists()) {
                    Logger.info("迁移数据${oldDataUUID}失败：没有文件")
                    continue
                }
                val playerMap = HashMap<UUID, String>()
                for (uuid in oldData.members) {
                    playerMap[uuid] = "member"
                }
                for (uuid in oldData.banPlayers) {
                    playerMap[uuid] = "blackList"
                }

                val dimension = HashMap<String, WorldDimensionData>()

                val oldWorldDimensionData =
                    com.dongzh1.pixelworldpro.world.Config.INSTANCE.getWorldDimensionData(oldData.worldName)
                for (createDimension in oldWorldDimensionData.createlist) {
                    val dimensionData = Dimension.INSTANCE.getDimensionData(createDimension) ?: continue

                    val environment = when (dimensionData.name) {
                        "nether" -> {
                            "Nether"
                        }

                        "the_end" -> {
                            "The_End"
                        }

                        else -> {
                            "Default"
                        }
                    }

                    val creator = if (dimensionData.creator == "auto") {
                        null
                    } else {
                        dimensionData.creator
                    }

                    dimension[dimensionData.name] = WorldDimensionData(
                        dimensionData.name,
                        dimensionData.name,
                        environment,
                        "Normal",
                        creator
                    )
                }

                val worldData = DataBase.getDataDriver("local").createWorldData(
                    WorldCreateData(
                        oldDataUUID,
                        (Config.world.getString("create.name")
                            ?: "%player_name%的世界").replacePlaceholder(Bukkit.getOfflinePlayer(oldDataUUID)),
                        PermissionImpl.getConfigWorldPermission(),
                        playerMap,
                        "local",
                        dimension
                    )
                )

                Logger.info("迁移数据${oldDataUUID}：完成数据库数据迁移")

                val newWorldFile = File("./PixelWorldPro/world/local/${worldData.id}/world")
                newWorldFile.mkdirs()
                oldWorldFile.copyRecursively(newWorldFile)

                val world = PixelWorldProWorld(worldData)
                world.setCompressMethod("None")

                world.setLevel(oldData.worldLevel.toInt())

                Logger.info("迁移数据${oldDataUUID}：完成世界数据迁移")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}