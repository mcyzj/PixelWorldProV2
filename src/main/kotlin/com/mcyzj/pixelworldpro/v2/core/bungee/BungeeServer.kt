package com.mcyzj.pixelworldpro.v2.core.bungee

import com.google.gson.JsonObject
import com.mcyzj.lib.plugin.Logger
import com.mcyzj.lib.plugin.file.BuiltInConfiguration
import com.mcyzj.lib.plugin.file.BuiltOutConfiguration
import com.mcyzj.pixelworldpro.v2.core.bungee.redis.Communicate
import com.mcyzj.pixelworldpro.v2.core.util.Config
import com.mcyzj.pixelworldpro.v2.core.world.WorldImpl
import org.bukkit.Bukkit
import org.json.simple.JSONObject
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object BungeeServer {
    private val bungeeConfig = Config.bungee
    private val publicBungeeConfig = getPublicBungeeConfig()

    private val inResponse = ArrayList<Int>()
    private val response = HashMap<Int, ResponseData>()

    /**
     * @author:  MC鱼子酱
     * @methodsName: get public bungee config
     * @description: 获取服务器共用的bungee配置
     * @return: BuiltOutConfiguration
     */
    fun getPublicBungeeConfig(): BuiltOutConfiguration {
        val config = BuiltOutConfiguration("./PixelWorldPro/bungee.yml")
        if (config.getInt("version") < 1) {
            val example = BuiltInConfiguration("example/bungee.yml")
            example.save(config.file)
        }
        return BuiltOutConfiguration("./PixelWorldPro/bungee.yml")
    }

    /**
     * @author:  MC鱼子酱
     * @methodsName: get local server
     * @description: 获取本地数据并自动保存至bungee文件
     * @return: BungeeData
     */
    fun getLocalServer(): BungeeData {
        //获取本地服务器数据
        val name = bungeeConfig.getString("name")!!
        val server = bungeeConfig.getString("server")!!
        val mode = bungeeConfig.getString("mode")!!
        var load = (mode != "lobby")
        val tickets = WorldImpl.serverTickets
        val maxTickets = bungeeConfig.getDouble("maxTickets")
        if ((load).and(maxTickets > 0.0)) {
            load = (tickets < maxTickets)
        }
        val worlds = WorldImpl.loadWorld.size
        val maxWorlds = bungeeConfig.getInt("maxWorld")
        if ((load).and(maxWorlds > 0)) {
            load = (worlds < maxWorlds)
        }
        val data = BungeeData(
            name, server, mode, tickets, maxTickets, worlds, maxWorlds, load
        )
        //保存至bungee文件
        val config = BuiltOutConfiguration("./PixelWorldPro/cache/bungee.yml")
        config.set("server.${data.server}.name", data.name)
        config.set("server.${data.server}.mode", data.mode)
        config.set("server.${data.server}.tickets", data.tickets)
        config.set("server.${data.server}.maxTickets", data.maxTickets)
        config.set("server.${data.server}.world", data.worlds)
        config.set("server.${data.server}.maxWorld", data.maxWorlds)
        config.set("server.${data.server}.load", data.load)
        config.saveToFile()
        //返回数据
        return data
    }

    /**
     * @author:  MC鱼子酱
     * @methodsName: get server
     * @description: 获取指定的服务器数据
     * @param: server String
     * @return: BungeeData?
     */
    fun getServerData(server: String): BungeeData? {
        val config = BuiltOutConfiguration("./PixelWorldPro/cache/bungee.yml").getConfigurationSection("server.$server")
            ?: return null
        val name = config.getString("name")!!
        val mode = config.getString("mode")!!
        val tickets = config.getDouble("tickets")
        val maxTickets = config.getDouble("maxTickets")
        val worlds = config.getInt("world")
        val maxWorlds = config.getInt("maxWorld")
        val load = config.getBoolean("load")
        return BungeeData(
            name, server, mode, tickets, maxTickets, worlds, maxWorlds, load
        )
    }

    /**
     * @author:  MC鱼子酱
     * @methodsName: get server
     * @description: 获取玩家可以加载的世界数据
     * @param: owner UUID
     * @return: BungeeData?
     */
    fun getServerData(owner: UUID): BungeeData? {
        val player = Bukkit.getPlayer(owner)
        var group = "default"
        if (player != null) {
            val groupConfig = publicBungeeConfig.getConfigurationSection("group")!!
            for (key in groupConfig.getKeys(false)) {
                if (key == group) {
                    continue
                }
                val permission = groupConfig.getString("$key.permission") ?: "pixelworldpro.vip"
                if (player.hasPermission(permission)) {
                    group = key
                    break
                }
            }
        }
        var time = 0
        while (true) {
            val serverList = publicBungeeConfig.getStringList("group.$group.server")
            var serverData: BungeeData? = null
            for (server in serverList) {
                val bungeeData = getServerData(server) ?: continue
                Logger.info("获取服务器数据：${bungeeData.server}", true)
                if (!bungeeData.load) {
                    continue
                }
                if (serverData != null) {
                    if (serverData.tickets < bungeeData.tickets) {
                        continue
                    }
                }
                serverData = bungeeData
            }
            Logger.info("成功获取的服务器数据：${serverData?.server}", true)
            if (serverData != null) {
                if (checkServer(serverData.server).get()) {
                    return serverData
                }
            }
            time++
            if (time > 10) {
                return null
            }

        }
    }

    /**
     * @author:  MC鱼子酱
     * @methodsName: check server
     * @description: 检测服务器是否存活
     * @param: owner UUID
     * @return: BungeeData?
     */
    fun checkServer(server: String): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        Thread {
            Logger.info("服务器查活：${server}", true)
            val id = getResponseId()
            val data = JSONObject()
            data["type"] = "ServerCheck"
            data["response"] = id
            Communicate.send(server, "local", data)
            val responseData = getServerResponse(id).get()
            Logger.info("服务器查活：${server} [${responseData.result}]", true)
            if (!responseData.result) {
                val config = BuiltOutConfiguration("./PixelWorldPro/cache/bungee.yml")
                config.set("server.$server", null)
                config.saveToFile()
            }
            future.complete(responseData.result)
        }.start()
        return future
    }

    /**
     * @author:  MC鱼子酱
     * @methodsName: get unused response id
     * @description: 获取空闲的返回编码
     * @throws Exception
     * @return: Int
     */
    fun getResponseId(): Int {
        var time = 0
        var checkId = Random().nextInt(900000000) + 100000000
        while (true) {
            if (checkId !in inResponse) {
                break
            }
            time ++
            checkId = Random().nextInt(900000000) + 100000000
            if (time > 60) {
                throw Exception("There are no available Response ID. If your server is not too popular, please consider if your luck is too bad")
            }
        }
        Logger.info("生成返回id：$checkId", true)
        return checkId
    }

    /**
     * @author:  MC鱼子酱
     * @methodsName: get server response
     * @description: 获取服务器的返回值
     * @param: checkId Int maxTime Int
     * @return: CompletableFuture<ResponseData>
     */
    fun getServerResponse(checkId: Int, maxTime: Int = 10, sleep: Int = 1000): CompletableFuture<ResponseData> {
        val future = CompletableFuture<ResponseData>()
        Thread {
            if (checkId == 0) {
                future.complete(ResponseData(false, JsonObject()))
                return@Thread
            }
            inResponse.add(checkId)
            var time = 0
            while (true) {
                Logger.info("查活返回id：$checkId [$time/$maxTime]", true)
                val response = response[checkId]
                if (response != null) {
                    Logger.info("返回id：$checkId 已返回值\n返回结果：${response.result}\n返回特殊值：${response.data}", true)
                    future.complete(response)
                    return@Thread
                }
                Logger.info("查活返回id：$checkId 没有回应，继续等待[$time/$maxTime]", true)
                Thread.sleep(sleep.toLong())
                time ++
                if (time > maxTime) {
                    Logger.info("返回id：$checkId 失效", true)
                    future.complete(ResponseData(false, JsonObject()))
                    inResponse.remove(checkId)
                    return@Thread
                }
            }
        }.start()
        return future
    }

    /**
     * @author:  MC鱼子酱
     * @methodsName: set server response
     * @description: 设置返回值
     * @param: data JsonObject
     */
    fun setServerResponse(data: JsonObject){
        try {
            val responseID = data["id"].asInt
            val result = data["result"].asBoolean
            val responseData = data["data"].asJsonObject
            Logger.info("接收到返回信息：$responseID\n返回结果：$result\n返回特殊值：$responseData", true)
            if (responseID in inResponse) {
                inResponse.remove(responseID)
                response[responseID] = ResponseData(
                    result,
                    responseData
                )
            } else {
                Logger.info("无法处理返回信息：$responseID [无效的返回ID]", true)
            }
        } catch (e: Exception) {
            Logger.warning("无法处理返回信息：$data [无效的数据]", true)
            e.printStackTrace()
        }
    }
}