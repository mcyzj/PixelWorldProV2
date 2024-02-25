package com.mcyzj.pixelworldpro.v2.database

import com.mcyzj.lib.plugin.database.SQLite

class SQLiteDatabaseAPI : DatabaseImpl(SQLite("./PixelWorldPro/database.db"))