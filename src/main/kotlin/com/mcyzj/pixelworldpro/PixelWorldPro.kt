package com.mcyzj.pixelworldpro

import com.mcyzj.libs.JiangLib
import com.mcyzj.libs.Metrics
import com.mcyzj.pixelworldpro.api.interfaces.core.database.DatabaseAPI
import com.mcyzj.pixelworldpro.bungee.Server
import com.mcyzj.pixelworldpro.bungee.System.setServer
import com.mcyzj.pixelworldpro.bungee.database.SocketClient
import com.mcyzj.pixelworldpro.command.Register
import com.mcyzj.pixelworldpro.data.database.MysqlDatabaseAPI
import com.mcyzj.pixelworldpro.data.database.SQLiteDatabaseAPI
import com.mcyzj.pixelworldpro.expansion.ExpansionManager
import com.mcyzj.pixelworldpro.expansion.core.Core
import com.mcyzj.pixelworldpro.expansion.core.gui.listener.MenuListen
import com.mcyzj.pixelworldpro.file.Config
import com.mcyzj.pixelworldpro.listener.World
import com.mcyzj.pixelworldpro.server.Icon
import com.mcyzj.pixelworldpro.server.World.deleteLock
import com.mcyzj.pixelworldpro.server.windows.Eula
import com.mcyzj.pixelworldpro.world.Local
import com.xbaimiao.easylib.EasyPlugin
import com.xbaimiao.easylib.module.chat.BuiltInConfiguration
import com.xbaimiao.easylib.module.utils.submit
import org.bukkit.Bukkit
import java.io.File
import java.util.concurrent.CompletableFuture


class PixelWorldPro : EasyPlugin() {
    companion object {
        lateinit var databaseApi: DatabaseAPI
        lateinit var instance: PixelWorldPro
    }

    var eula = BuiltInConfiguration("Eula.yml")
    var config = BuiltInConfiguration("Config.yml")
    private var language = config.getString("lang")?:"zh_cn"
    var lang = BuiltInConfiguration("lang/${language}.yml")
    private var bungee = Config.bungee
    override fun onEnable() {
        instance = this
        //PixelWorldProV2遵循《用户协议-付费插件》
        //https://wiki.mcyzj.cn/#/zh-cn/agreement?id=%e4%bb%98%e8%b4%b9%e6%8f%92%e4%bb%b6
        //购买/反编译/使用 插件即表明您认可我们的协议
        //下载必要的lib库
        JiangLib.loadLibs()
        //进行bstats统计，根据付费协议，我们可以无条件使用bstats进行数据收集
        val pluginId = 20038
        val metrics = Metrics(this, pluginId)
        metrics.addCustomChart(Metrics.SimplePie("test_version") {
            "alpha"
        })
        metrics.addCustomChart(Metrics.SimplePie("language") {
            language
        })
        //开始插件加载
        Icon.pixelWorldPro()
        Icon.v2Alpha()
        val jdkVersion = System.getProperty("java.version") // jdk 版本
        logger.info("§aPixelWorldPro 开始加载于JDK${jdkVersion}上")
        //加载默认配置文件
        logger.info("§aPixelWorldPro ${lang.getString("config.load")}")
        saveOtherConfig()
        Config.update()
        //加载语言文件
        saveLang()
        //检查系统信息
        checkServer()
        //同意eula
        val future = eula()
        future.thenApply {
            if (!it){
                return@thenApply
            }
            //加载gui界面
            saveGui()
            //加载数据库
            submit(async = config.getBoolean("async.database.connect")) {
                if (config.getBoolean("debug")) {
                    Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 加载数据")
                }
                if (config.getString("Database").equals("db", true)) {
                    if (config.getBoolean("debug")) {
                        Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 加载sqlite数据库")
                    }
                    databaseApi = SQLiteDatabaseAPI()
                }
                if (config.getString("Database").equals("mysql", true)) {
                    if (config.getBoolean("debug")) {
                        Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 加载MySQL数据库")
                    }
                    databaseApi = MysqlDatabaseAPI()
                }
                if (bungee.getBoolean("Enable")) {
                    submit(async = true) {
                        //创建连接
                        SocketClient.createClient()
                    }
                    //写入服务器数据
                    setServer()
                    //启用传送
                    Server.tpPlayer()
                }
                submit {
                    //保存未正常备份的世界
                    Local.getUnzipWorld()
                    //删除未删除的缓存
                    deleteLock()
                    //注册监听
                    Bukkit.getPluginManager().registerEvents(World(), this@PixelWorldPro)
                    Bukkit.getPluginManager().registerEvents(MenuListen(), this@PixelWorldPro)
                    //注册备份线程
                    Local.regularBackup()
                    //加载核心扩展
                    Core.enable()
                    //加载外部扩展
                    ExpansionManager.loadAllExpansion()
                    //注册命令
                    Register().command.register()
                    //注册Papi
                    if (config.getString("mainPapi") != null) {
                        Papi.register()
                    }
                    //注册bungee信道
                    this@PixelWorldPro.server.messenger.registerOutgoingPluginChannel(this@PixelWorldPro, "BungeeCord")
                }
            }
        }
    }

    fun reloadAll(){
        //开始插件加载
        val jdkVersion = System.getProperty("java.version") // jdk 版本
        logger.info("§aPixelWorldPro 开始重载于JDK${jdkVersion}上")
        //加载默认配置文件
        logger.info("§aPixelWorldPro ${lang.getString("config.load")}")
        saveOtherConfig()
        //加载语言文件
        saveLang()
        //检查系统信息
        checkServer()
        //同意eula
        val future = eula()
        future.thenApply {
            if (!it) {
                return@thenApply
            }
            //加载gui界面
            saveGui()
            //加载数据库
            submit(async = config.getBoolean("async.database.connect")) {
                if (config.getBoolean("debug")) {
                    Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 加载数据")
                }
                if (config.getString("Database").equals("db", true)) {
                    if (config.getBoolean("debug")) {
                        Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 加载sqlite数据库")
                    }
                    databaseApi = SQLiteDatabaseAPI()
                }
                if (config.getString("Database").equals("mysql", true)) {
                    if (config.getBoolean("debug")) {
                        Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 加载MySQL数据库")
                    }
                    databaseApi = MysqlDatabaseAPI()
                }
            }
        }
    }

    override fun onDisable() {
        Local.unloadAllWorld()
        for (key in ExpansionManager.loadExpansion.keys){
            if (config.getBoolean("debug")) {
                Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 卸载扩展 $key")
            }
            ExpansionManager.loadExpansion[key]!!.onDisable()
        }
    }

    private fun eula(): CompletableFuture<Boolean> {
        if (eula.getBoolean("eula")){
            val future = CompletableFuture<Boolean>()
            future.complete(true)
            return future
        }
        logger.info(lang.getString("eula"))
        logger.info("https://wiki.mcyzj.cn/#/zh-cn/agreement?id=%e4%bb%98%e8%b4%b9%e6%8f%92%e4%bb%b6")
        return Eula.open()
    }
    private fun checkServer(){
        val osArch = System.getProperty("os.arch") // 架构名称
        if (!osArch.equals("amd64")){
            Icon.warning()
            logger.warning("§aPixelWorldPro ${lang.getString("system.unKnowArch")} osArch")
        }
    }

    private fun saveLang() {
        logger.info("§aPixelWorldPro ${lang.getString("lang.load")}")
        val language = listOf(
            //"en",
            "zh_cn"
        )
        for (lang in language) {
            if (!File(dataFolder, "lang/$lang.yml").exists()) {
                saveResource("lang/$lang.yml", false)
            }else{
                //CommentConfig.updateLang(lang)
            }
        }
        reloadLang()
    }
    private fun reloadLang() {
        lang = BuiltInConfiguration("lang/${language}.yml")
    }

    private fun saveOtherConfig() {
        //遍历插件需要释放的yml配置文件,并保存在生成的插件文件夹中
        //主配置文件
        if (!File(dataFolder, "Config.yml").exists()) {
            saveResource("Config.yml", false)
        }
        //存储配置文件
        if (!File(dataFolder, "File.yml").exists()) {
            saveResource("File.yml", false)
        }
    }
    private fun saveGui() {
        logger.info("§aPixelWorldPro ${lang.getString("gui.load")}")
        //遍历插件resources中gui文件夹下所有的.yml文件,并保存在生成的插件文件夹中
        if (!File(dataFolder, "gui/WorldCreate.yml").exists()) {
            saveResource("gui/WorldCreate.yml", false)
        }
        if (!File(dataFolder, "gui/WorldList.yml").exists()) {
            saveResource("gui/WorldList.yml", false)
        }
    }
}