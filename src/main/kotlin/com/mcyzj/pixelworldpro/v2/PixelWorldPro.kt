package com.mcyzj.pixelworldpro.v2

import com.mcyzj.lib.plugin.Logger
import com.mcyzj.pixelworldpro.v2.database.DatabaseAPI
import com.mcyzj.pixelworldpro.v2.database.MysqlDatabaseAPI
import com.mcyzj.pixelworldpro.v2.database.SQLiteDatabaseAPI
import com.mcyzj.pixelworldpro.v2.util.Config
import com.mcyzj.pixelworldpro.v2.util.Icon
import com.mcyzj.pixelworldpro.v2.util.Install
import com.xbaimiao.easylib.EasyPlugin

class PixelWorldPro: EasyPlugin() {
    companion object {
        lateinit var databaseApi: DatabaseAPI
        lateinit var log: Logger
    }

    private val lang = Config.getLang()
    override fun enable() {
        log = Logger
        Icon.pixelWorldPro()
        Icon.v2()
        log.info(lang.getString("plugin.enable"))
        log.info("加载数据", true)
        if (config.getString("Database").equals("db", true)) {
            log.info("加载sqlite数据库", true)
            databaseApi = SQLiteDatabaseAPI()
        }
        if (config.getString("Database").equals("mysql", true)) {
            log.info("加载MySQL数据库", true)
            databaseApi = MysqlDatabaseAPI()
        }
        //检测是否安装
        if (Config.config.getBoolean("install")) {
            Install.start()
        }
    }

    override fun disable() {
        logger.info(lang.getString("plugin.disable"))
    }
}