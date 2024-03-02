package com.mcyzj.pixelworldpro.v2.core

import com.mcyzj.lib.plugin.JiangLib
import com.mcyzj.lib.plugin.Logger
import com.mcyzj.pixelworldpro.v2.core.command.CommandCore
import com.mcyzj.pixelworldpro.v2.core.database.DatabaseAPI
import com.mcyzj.pixelworldpro.v2.core.database.MysqlDatabaseAPI
import com.mcyzj.pixelworldpro.v2.core.database.SQLiteDatabaseAPI
import com.mcyzj.pixelworldpro.v2.core.expansion.ExpansionManager
import com.mcyzj.pixelworldpro.v2.core.util.Config
import com.mcyzj.pixelworldpro.v2.core.util.Icon
import com.mcyzj.pixelworldpro.v2.core.util.Install
import com.mcyzj.pixelworldpro.v2.core.world.WorldCache.cleanWorldCache
import com.xbaimiao.easylib.EasyPlugin

@Suppress("unused")
class PixelWorldPro: EasyPlugin() {
    companion object {
        lateinit var databaseApi: DatabaseAPI
        lateinit var instance: PixelWorldPro
    }

    private val lang = Config.getLang()
    val log = Logger
    override fun enable() {
        JiangLib.loadLibs()
        instance = this
        Icon.pixelWorldPro()
        Icon.v2()
        log.info(lang.getString("plugin.enable"))
        log.info("加载数据", true)
        if (config.getString("database").equals("db", true)) {
            log.info("加载sqlite数据库", true)
            databaseApi = SQLiteDatabaseAPI()
        }
        if (config.getString("database").equals("mysql", true)) {
            log.info("加载MySQL数据库", true)
            databaseApi = MysqlDatabaseAPI()
        }
        //检测是否安装
        if (Config.config.getBoolean("install")) {
            Install.start()
        }
        //注册扩展
        ExpansionManager.loadAllExpansion()

        //注册指令
        CommandCore().commandRoot.register()
        //启动回收线程
        cleanWorldCache()
    }

    override fun disable() {
        logger.info(lang.getString("plugin.disable"))
    }
}