package com.mcyzj.pixelworldpro.data.database

import com.xbaimiao.easylib.module.database.OrmliteSQLite

class SQLiteDatabaseApi : DatabaseImpl(OrmliteSQLite("database.db"))