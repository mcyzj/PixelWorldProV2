package com.mcyzj.lib.plugin.database

import com.j256.ormlite.jdbc.JdbcConnectionSource
import com.j256.ormlite.logger.Level
import com.j256.ormlite.support.ConnectionSource
import com.xbaimiao.easylib.EasyPlugin
import com.xbaimiao.easylib.module.database.AbstractOrmliteDatabase
import java.io.File

class SQLite(private val name: String) : AbstractOrmliteDatabase() {

    override val connectionSource: ConnectionSource by lazy {
        val url = "jdbc:sqlite:$name"
        JdbcConnectionSource(url)
    }

    init {
        com.j256.ormlite.logger.Logger.setGlobalLogLevel(Level.WARNING)
    }

}