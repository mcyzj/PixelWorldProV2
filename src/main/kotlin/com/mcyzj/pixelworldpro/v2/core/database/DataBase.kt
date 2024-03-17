package com.mcyzj.pixelworldpro.v2.core.database

import com.mcyzj.lib.plugin.database.SQLite
import com.mcyzj.pixelworldpro.v2.core.PixelWorldPro
import com.mcyzj.pixelworldpro.v2.core.util.Config
import com.xbaimiao.easylib.module.database.Ormlite
import com.xbaimiao.easylib.module.database.OrmliteMysql

object DataBase {
    private val databaseMap = HashMap<String, DatabaseAPI>()

    fun regDataDriver(name: String, driver: DatabaseAPI) {
        PixelWorldPro.instance.log.info("加载数据", true)
        databaseMap[name] = driver
    }

    fun getDataDriver(name: String): DatabaseAPI {
        return databaseMap[name]!!
    }

    fun getOrmlite(): Ormlite {
        return if (Config.config.getString("database").equals("mysql", true)) {
            PixelWorldPro.instance.log.info("加载MySQL数据库", true)
            OrmliteMysql(
                Config.config.getConfigurationSection("mysql")!!,
                Config.config.getBoolean("mysql.HikariCP")
            )
        } else {
            PixelWorldPro.instance.log.info("加载sqlite数据库", true)
            SQLite("./PixelWorldPro/database.db")
        }
    }
}