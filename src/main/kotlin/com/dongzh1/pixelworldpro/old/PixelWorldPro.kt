package com.dongzh1.pixelworldpro.old

import com.dongzh1.pixelworldpro.api.DatabaseApi
import com.dongzh1.pixelworldpro.database.MysqlDatabaseApi
import com.dongzh1.pixelworldpro.database.SQLiteDatabaseApi
import com.dongzh1.pixelworldpro.expansion.Expansion
import com.mcyzj.lib.plugin.file.BuiltInConfiguration
import org.bukkit.Bukkit
import redis.clients.jedis.JedisPool

class PixelWorldPro {
    companion object {

        lateinit var databaseApi: DatabaseApi
        lateinit var expansion: Expansion
        lateinit var jedisPool: JedisPool
        const val channel = "PixelWorldPro"
    }

    private var config = BuiltInConfiguration("old/config.yml")

    init {
        val instance = this
        if (config.getString("Database").equals("db", true)) {
            if (config.getBoolean("debug")) {
                Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 加载sqlite数据库")
            }
            databaseApi = SQLiteDatabaseApi()
        }
        if (config.getString("Database").equals("mysql", true)) {
            if (config.getBoolean("debug")) {
                Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 加载MySQL数据库")
            }
            databaseApi = MysqlDatabaseApi()
        }
    }
}