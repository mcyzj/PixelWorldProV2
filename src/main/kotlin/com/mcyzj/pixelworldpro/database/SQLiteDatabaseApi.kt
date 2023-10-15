package com.mcyzj.pixelworldpro.database

import com.xbaimiao.easylib.module.database.OrmliteSQLite

class SQLiteDatabaseApi : DatabaseImpl(OrmliteSQLite("database.db"))