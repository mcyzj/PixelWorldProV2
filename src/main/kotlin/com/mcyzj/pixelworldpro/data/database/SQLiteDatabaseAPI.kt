package com.mcyzj.pixelworldpro.data.database

import com.xbaimiao.easylib.module.database.OrmliteSQLite

class SQLiteDatabaseAPI : DatabaseImpl(OrmliteSQLite("database.db"))