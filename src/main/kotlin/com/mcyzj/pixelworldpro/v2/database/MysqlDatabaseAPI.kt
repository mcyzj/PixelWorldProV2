package com.mcyzj.pixelworldpro.v2.database

import com.mcyzj.pixelworldpro.v2.util.Config
import com.xbaimiao.easylib.module.database.OrmliteMysql

class MysqlDatabaseAPI : DatabaseImpl(
    OrmliteMysql(
        Config.config.getConfigurationSection("mysql")!!,
        Config.config.getBoolean("mysql.HikariCP")
    )
)