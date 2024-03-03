package com.mcyzj.pixelworldpro.v2.core

import com.mcyzj.lib.plugin.JiangLib
import com.mcyzj.lib.plugin.Logger
import com.mcyzj.pixelworldpro.v2.core.bungee.Communicate
import com.mcyzj.pixelworldpro.v2.core.bungee.DataProcessing
import com.mcyzj.pixelworldpro.v2.core.bungee.RedisConfig
import com.mcyzj.pixelworldpro.v2.core.command.CommandCore
import com.mcyzj.pixelworldpro.v2.core.database.DatabaseAPI
import com.mcyzj.pixelworldpro.v2.core.database.MysqlDatabaseAPI
import com.mcyzj.pixelworldpro.v2.core.database.SQLiteDatabaseAPI
import com.mcyzj.pixelworldpro.v2.core.expansion.ExpansionManager
import com.mcyzj.pixelworldpro.v2.core.util.Config
import com.mcyzj.pixelworldpro.v2.core.util.Icon
import com.mcyzj.pixelworldpro.v2.core.util.Install
import com.mcyzj.pixelworldpro.v2.core.world.LocalWorld
import com.mcyzj.pixelworldpro.v2.core.world.WorldCache.cleanWorldCache
import com.mcyzj.pixelworldpro.v2.core.world.WorldListener
import com.xbaimiao.easylib.EasyPlugin
import org.bukkit.Bukkit
import redis.clients.jedis.JedisPool


@Suppress("unused")
class PixelWorldPro: EasyPlugin() {
    companion object {
        lateinit var databaseApi: DatabaseAPI
        lateinit var instance: PixelWorldPro
        lateinit var jedisPool: JedisPool
        lateinit var redisConfig: RedisConfig
        lateinit var redisThread: Thread
    }

    private val lang = Config.getLang()
    val log = Logger
    override fun enable() {
        JiangLib.loadLibs()
        instance = this
        Icon.pixelWorldPro()
        Icon.v2()
        log.info(lang.getString("plugin.enable"))
        //检测是否安装
        if (Config.config.getBoolean("install")) {
            Install.start()
        }
        log.info("加载数据", true)
        if (config.getString("database").equals("db", true)) {
            log.info("加载sqlite数据库", true)
            databaseApi = SQLiteDatabaseAPI()
        }
        if (config.getString("database").equals("mysql", true)) {
            log.info("加载MySQL数据库", true)
            databaseApi = MysqlDatabaseAPI()
        }
        //注册扩展
        ExpansionManager.loadAllExpansion()

        //注册指令
        CommandCore().commandRoot.register()
        //启动回收线程
        cleanWorldCache()
        //注册监听
        Bukkit.getPluginManager().registerEvents(WorldListener(), this)
        if (Config.bungee.getBoolean("enable")) {
            //链接redis
            redisConfig = RedisConfig(Config.bungee)
            jedisPool = if (redisConfig.password != null) {
                JedisPool(redisConfig, redisConfig.host, redisConfig.port, 1000, redisConfig.password)
            } else {
                JedisPool(redisConfig, redisConfig.host, redisConfig.port)
            }
            redisThread = Thread {
                jedisPool.resource.use { jedis ->
                    jedis.subscribe(Communicate, redisConfig.channel)
                }
            }
            redisThread.start()
            //注册信道
            this.server.messenger.registerOutgoingPluginChannel(this, "BungeeCord")
            //注册监听
            Communicate.listener["local"] = DataProcessing
            //注册世界tickets计算
            LocalWorld.updateAllWorlds()
        }
    }

    override fun disable() {
        logger.info(lang.getString("plugin.disable"))
        redisThread.stop()
    }
}