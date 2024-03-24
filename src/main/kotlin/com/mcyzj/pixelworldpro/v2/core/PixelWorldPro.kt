package com.mcyzj.pixelworldpro.v2.core

import com.mcyzj.lib.bukkit.menu.MenuImpl
import com.mcyzj.lib.plugin.Logger
import com.mcyzj.lib.plugin.file.Path
import com.mcyzj.pixelworldpro.v2.core.bungee.BungeeWorld
import com.mcyzj.pixelworldpro.v2.core.bungee.redis.Communicate
import com.mcyzj.pixelworldpro.v2.core.bungee.redis.DataProcessing
import com.mcyzj.pixelworldpro.v2.core.bungee.redis.RedisConfig
import com.mcyzj.pixelworldpro.v2.core.command.CommandCore
import com.mcyzj.pixelworldpro.v2.core.database.DataBase
import com.mcyzj.pixelworldpro.v2.core.database.DatabaseImpl
import com.mcyzj.pixelworldpro.v2.core.expansion.ExpansionManager
import com.mcyzj.pixelworldpro.v2.core.papi.Papi
import com.mcyzj.pixelworldpro.v2.core.util.Config
import com.mcyzj.pixelworldpro.v2.core.util.Icon
import com.mcyzj.pixelworldpro.v2.core.util.Install
import com.mcyzj.pixelworldpro.v2.core.world.LocalWorld
import com.mcyzj.pixelworldpro.v2.core.world.WorldCache
import com.mcyzj.pixelworldpro.v2.core.world.WorldImpl
import com.mcyzj.pixelworldpro.v2.core.world.WorldCache.cleanWorldCache
import com.mcyzj.pixelworldpro.v2.core.world.WorldListener
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import redis.clients.jedis.JedisPool
import java.io.File


@Suppress("unused")
class PixelWorldPro{
    companion object {
        lateinit var instance: PixelWorldPro
        lateinit var jedisPool: JedisPool
        lateinit var redisConfig: RedisConfig
        lateinit var redisThread: Thread
        var bungeeEnable: Boolean = false
    }
    val logger = Logger
    private val lang = Config.getLang()
    val log = Logger
    val config = Config.config
    fun enable() {
        //JiangLib.loadLibs()
        instance = this
        Icon.pixelWorldPro()
        Icon.v2()
        log.info(lang.getString("plugin.enable"))
        //检测是否安装
        if (Config.config.getBoolean("install")) {
            Install.start()
        }
        //注册数据驱动
        DataBase.regDataDriver("local", DatabaseImpl())
        //注册世界驱动
        WorldCache.regWorldDriver("local", LocalWorld())
        WorldCache.regWorldDriver("local_bungee", BungeeWorld())
        //注册扩展
        ExpansionManager.loadAllExpansion()
        //注册指令
        CommandCore().commandRoot.register()
        //启动回收线程
        cleanWorldCache()
        //注册监听
        Bukkit.getPluginManager().registerEvents(WorldListener(), Main.instance)
        //注册Papi
        Papi.register()
        //注册菜单
        registerMenu()
        //Bungee处理
        bungeeEnable = Config.bungee.getBoolean("enable")
        if (bungeeEnable) {
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
            Main.instance.server.messenger.registerOutgoingPluginChannel(Main.instance, "BungeeCord")
            //注册监听
            Communicate.listener["local"] = DataProcessing
            //注册世界tickets计算
            WorldImpl.updateAllWorlds()
        }
    }

    fun registerMenu() {
        val path = Path().getJarPath(this::class.java)
        val menuFolder = File("$path/PixelWorldProV2/menu")
        val fileList = menuFolder.listFiles()
        if (fileList != null) {
            for (file in fileList) {
                val menuConfig = YamlConfiguration.loadConfiguration(file)
                MenuImpl.registerMenuConfig(menuConfig, Main.instance)
            }
        }
    }

    fun disable() {
        logger.info(lang.getString("plugin.disable"))
        redisThread.stop()
    }
}