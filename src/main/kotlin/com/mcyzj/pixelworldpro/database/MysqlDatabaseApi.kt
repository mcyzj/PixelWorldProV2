package com.mcyzj.pixelworldpro.database

import com.mcyzj.pixelworldpro.PixelWorldPro
import com.xbaimiao.easylib.module.database.OrmliteMysql

class MysqlDatabaseApi : DatabaseImpl(OrmliteMysql(
    PixelWorldPro.instance.config.getConfigurationSection("Mysql")!!,
    PixelWorldPro.instance.config.getBoolean("Mysql.HikariCP")
)
)