package com.mcyzj.pixelworldpro

import com.mcyzj.libs.JiangLib
import com.mcyzj.libs.Metrics
import com.mcyzj.pixelworldpro.api.interfaces.DatabaseApi
import com.mcyzj.pixelworldpro.command.Register
import com.mcyzj.pixelworldpro.database.MysqlDatabaseApi
import com.mcyzj.pixelworldpro.database.SQLiteDatabaseApi
import com.mcyzj.pixelworldpro.listener.World
import com.mcyzj.pixelworldpro.world.Local
import com.xbaimiao.easylib.EasyPlugin
import com.xbaimiao.easylib.module.chat.BuiltInConfiguration
import com.xbaimiao.easylib.module.utils.submit
import org.bukkit.Bukkit
import redis.clients.jedis.JedisPool
import java.io.File

@Suppress("unused")
class PixelWorldPro : EasyPlugin(){
    companion object {
        lateinit var databaseApi: DatabaseApi
        lateinit var instance: PixelWorldPro
        lateinit var jedisPool: JedisPool
    }

    private var eula = BuiltInConfiguration("Eula.yml")
    var config = BuiltInConfiguration("Config.yml")
    private var language = config.getString("lang")?:"zh_cn"
    var lang = BuiltInConfiguration("lang/${language}.yml")
    var pwpDebug = config.getBoolean("debug")
    override fun enable() {
        //PixelWorldProV2遵循《用户协议-付费插件》
        //https://wiki.mcyzj.cn/#/zh-cn/agreement?id=%e4%bb%98%e8%b4%b9%e6%8f%92%e4%bb%b6
        //购买/反编译/使用 插件即表明您认可我们的协议
        //下载必要的lib库
        JiangLib.loadLibs()
        //进行bstats统计，根据付费协议，我们可以无条件使用bstats进行数据收集
        val pluginId = 20038
        val metrics = Metrics(this, pluginId)
        metrics.addCustomChart(Metrics.SimplePie("test_version") {
            "beta"
        })
        metrics.addCustomChart(Metrics.SimplePie("language") {
            language
        })
        //开始插件加载
        val jdkVersion = System.getProperty("java.version") // jdk 版本
        logger.info("§aPixelWorldPro 开始加载于JDK${jdkVersion}上")
        instance = this
        //加载默认配置文件
        logger.info("§aPixelWorldPro ${lang.getString("config.load")}")
        saveOtherConfig()
        //加载语言文件
        saveLang()
        //检查系统信息
        checkServer()
        //同意eula
        if (!eula.getBoolean("eula")){
            if (eula()){
                eula.set("eula", true)
                eula.saveToFile()
            }else{
                return
            }
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
                databaseApi = SQLiteDatabaseApi()
            }
            if (config.getString("Database").equals("mysql", true)) {
                if (config.getBoolean("debug")) {
                    Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 加载MySQL数据库")
                }
                databaseApi = MysqlDatabaseApi()
            }
            submit {
                //注册命令
                Register().command.register()
                //注册监听
                Bukkit.getPluginManager().registerEvents(World(), this@PixelWorldPro)
            }
        }
    }

    fun reloadAll(){
        //开始插件加载
        val jdkVersion = System.getProperty("java.version") // jdk 版本
        logger.info("§aPixelWorldPro 开始重载于JDK${jdkVersion}上")
        instance = this
        //加载默认配置文件
        logger.info("§aPixelWorldPro ${lang.getString("config.load")}")
        saveOtherConfig()
        //加载语言文件
        saveLang()
        //检查系统信息
        checkServer()
        //同意eula
        if (!eula.getBoolean("eula")){
            if (eula()){
                eula.set("eula", true)
                eula.saveToFile()
            }else{
                return
            }
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
                databaseApi = SQLiteDatabaseApi()
            }
            if (config.getString("Database").equals("mysql", true)) {
                if (config.getBoolean("debug")) {
                    Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 加载MySQL数据库")
                }
                databaseApi = MysqlDatabaseApi()
            }
        }
    }

    override fun onDisable() {
        Local.unloadAllWorld()
    }

    private fun eula(): Boolean{
        logger.info(lang.getString("eula"))
        logger.info("https://wiki.mcyzj.cn/#/zh-cn/agreement?id=%e4%bb%98%e8%b4%b9%e6%8f%92%e4%bb%b6")
        //val stringInput = readln()
        //return stringInput == "true"
        return false
    }
    private fun checkServer(){
        val osArch = System.getProperty("os.arch") // 架构名称
        if (!osArch.equals("amd64")){
            logger.warning("§aPixelWorldPro ${lang.getString("system.unKnowArch")} osArch")
        }
    }

    private fun saveLang() {
        logger.info("§aPixelWorldPro ${lang.getString("lang.load")}")
        val langs = listOf(
            //"en",
            "zh_cn"
        )
        for (lang in langs) {
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
        if (!File(dataFolder, "gui/WorldEdit.yml").exists()) {
            saveResource("gui/WorldEdit.yml", false)
        }
        if (!File(dataFolder, "gui/WorldList.yml").exists()) {
            saveResource("gui/WorldList.yml", false)
        }
        if (!File(dataFolder, "gui/custom/customGui.yml").exists()) {
            saveResource("gui/custom/customGui.yml", false)
        }
        if (!File(dataFolder, "gui/MembersEdit.yml").exists()) {
            saveResource("gui/MembersEdit.yml", false)
        }
        if (!File(dataFolder, "gui/BanEdit.yml").exists()) {
            saveResource("gui/BanEdit.yml", false)
        }
        if (!File(dataFolder, "gui/WorldRestart.yml").exists()) {
            saveResource("gui/WorldRestart.yml", false)
        }
    }
}