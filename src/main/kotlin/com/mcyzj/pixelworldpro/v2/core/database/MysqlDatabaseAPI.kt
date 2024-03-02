package com.mcyzj.pixelworldpro.v2.core.database

import com.mcyzj.pixelworldpro.v2.core.util.Config
import com.xbaimiao.easylib.module.database.OrmliteMysql

class MysqlDatabaseAPI : DatabaseImpl(
    OrmliteMysql(
        Config.config.getConfigurationSection("mysql")!!,
        Config.config.getBoolean("mysql.HikariCP")
    )
)