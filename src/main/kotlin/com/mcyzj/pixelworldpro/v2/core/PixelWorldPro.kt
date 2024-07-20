package com.mcyzj.pixelworldpro.v2.core

import com.mcyzj.lib.Metrics
import com.mcyzj.lib.bukkit.menu.MenuImpl
import com.mcyzj.lib.plugin.Logger
import com.mcyzj.lib.plugin.file.Path
import com.mcyzj.pixelworldpro.v2.Main
import com.mcyzj.pixelworldpro.v2.core.bungee.BungeeWorld
import com.mcyzj.pixelworldpro.v2.core.bungee.redis.Communicate
import com.mcyzj.pixelworldpro.v2.core.bungee.redis.DataProcessing
import com.mcyzj.pixelworldpro.v2.core.bungee.redis.RedisConfig
import com.mcyzj.pixelworldpro.v2.core.command.CommandCore
import com.mcyzj.pixelworldpro.v2.core.database.DataBase
import com.mcyzj.pixelworldpro.v2.core.database.DatabaseImpl
import com.mcyzj.pixelworldpro.v2.core.expansion.ExpansionManager
import com.mcyzj.pixelworldpro.v2.core.menu.WorldList
import com.mcyzj.pixelworldpro.v2.core.menu.WorldMemberList
import com.mcyzj.pixelworldpro.v2.core.papi.Papi
import com.mcyzj.pixelworldpro.v2.core.util.Config
import com.mcyzj.pixelworldpro.v2.core.util.Icon
import com.mcyzj.pixelworldpro.v2.core.util.Install
import com.mcyzj.pixelworldpro.v2.core.world.*
import com.mcyzj.pixelworldpro.v2.core.world.WorldCache.cleanWorldCache
import org.bukkit.Bukkit
import org.bukkit.WorldCreator
import redis.clients.jedis.JedisPool
import java.io.File
import java.sql.SQLException


@Suppress("unused")
class PixelWorldPro{
    companion object {
        lateinit var instance: PixelWorldPro
        lateinit var jedisPool: JedisPool
        lateinit var redisConfig: RedisConfig
        lateinit var redisThread: Thread
        var disable: Boolean = false
        var bungeeEnable: Boolean = false
    }
    val logger = Logger
    private val lang = Config.getLang()
    val log = Logger
    val config = Config.config
    fun enable() {
        if (Config.config.getBoolean("old")) {
            Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 初始化V1API兼容")
            com.dongzh1.pixelworldpro.old.PixelWorldPro()
        }
        disable = false
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
        try {
            DataBase.regDataDriver("local", DatabaseImpl(DataBase.getOrmlite()))
        } catch (e: SQLException) {
            log.info("如果你还没有完成PixelWorldProV2的安装，出现以下报错是正常的")
            throw e
        }
        //注册世界驱动
        WorldCache.regWorldDriver("local", LocalWorld())
        WorldCache.regWorldDriver("local_bungee", BungeeWorld())
        //注册扩展
        ExpansionManager.loadAllExpansion()
        ExpansionManager.enableAllExpansion()
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
            Main.instance.server.messenger.registerOutgoingPluginChannel(
                Main.instance, "BungeeCord")
            //注册监听
            Communicate.listener["local"] = DataProcessing
            //注册世界tickets计算
            WorldImpl.updateAllWorlds()
        }
        MenuImpl.registerMenuDriver("PixelWorldPro_WorldList", WorldList())
        MenuImpl.registerMenuDriver("PixelWorldPro_WorldMemberList", WorldMemberList())

        val metrics = Metrics(Main.instance, 20038)
        metrics.addCustomChart(Metrics.SimplePie("language") {instance.config.getString("lang")})
        metrics.addCustomChart(Metrics.SimplePie("test_version") {"Official"})

        //加载世界
        val worldList = Config.localWorld.getStringList("loadWorldList")
        for (worldName in worldList) {
            Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 加载世界${worldName}")
            var world = Bukkit.getWorld(worldName)
            if (world != null) {
                Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 世界${worldName}已加载")
                continue
            }
            world = Bukkit.createWorld(WorldCreator(worldName))
            if (world != null) {
                Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 加载世界${worldName}成功")
                continue
            }
            Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 加载世界${worldName}失败")
        }
    }

    private fun registerMenu() {
        val path = Path().getJarPath(this::class.java)
        val menuFolder = File("$path/PixelWorldProV2/menu")
        val fileList = menuFolder.listFiles()
        if (fileList != null) {
            for (file in fileList) {
                MenuImpl.registerMenuConfig(file, Main.instance)
            }
        }
    }

    fun disable() {
        logger.info(lang.getString("plugin.disable"))
        disable = true

        for (world in WorldImpl.loadWorld.values) {
            val worldData = world.worldData
            val localWorld = Bukkit.getWorld("PixelWorldPro/cache/world/${worldData.type}/${worldData.id}/world")
            localWorld?.let { Bukkit.unloadWorld(it, true) }
            PixelWorldProWorld(worldData, false).unload()
        }
    }
}