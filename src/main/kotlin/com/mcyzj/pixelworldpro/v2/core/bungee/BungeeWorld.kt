package com.mcyzj.pixelworldpro.v2.core.bungee

import com.mcyzj.lib.plugin.file.BuiltOutConfiguration
import com.mcyzj.pixelworldpro.v2.core.PixelWorldPro
import com.mcyzj.pixelworldpro.v2.core.util.Config
import com.mcyzj.pixelworldpro.v2.core.world.LocalWorld
import com.mcyzj.pixelworldpro.v2.core.world.PixelWorldProWorld
import com.xbaimiao.easylib.module.chat.BuiltInConfiguration
import org.bukkit.Bukkit
import org.json.simple.JSONObject
import java.io.File
import java.lang.Thread.sleep
import java.util.*
import java.util.concurrent.CompletableFuture

object BungeeWorld {
    private val bungeeConfig = Config.bungee
    private val publicBungeeConfig = getPublicBungeeConfig()
    private val log = PixelWorldPro.instance.log

    fun getBungeeData(): BungeeData {
        val name = bungeeConfig.getString("name")!!
        val server = bungeeConfig.getString("server")!!
        val mode = bungeeConfig.getString("mode")!!
        var load = (mode != "lobby")
        val tickets = LocalWorld.serverTickets
        val maxTickets = bungeeConfig.getDouble("maxTickets")
        if ((load).and(maxTickets > 0.0)) {
            load = (tickets < maxTickets)
        }
        val worlds = LocalWorld.loadWorld.size
        val maxWorlds = bungeeConfig.getInt("maxWorld")
        if ((load).and(maxWorlds > 0)) {
            load = (worlds < maxWorlds)
        }
        return BungeeData(
            name, server, mode, tickets, maxTickets, worlds, maxWorlds, load
        )
    }

    fun saveServerData() {
        val data = getBungeeData()
        val config = BuiltOutConfiguration("./PixelWorldPro/cache/bungee.yml")
        config.set("server.${data.server}.name", data.name)
        config.set("server.${data.server}.mode", data.mode)
        config.set("server.${data.server}.tickets", data.tickets)
        config.set("server.${data.server}.maxTickets", data.maxTickets)
        config.set("server.${data.server}.world", data.worlds)
        config.set("server.${data.server}.maxWorld", data.maxWorlds)
        config.set("server.${data.server}.load", data.load)
        config.saveToFile()
    }

    fun getServerData(server: String): BungeeData? {
        val config = BuiltOutConfiguration("./PixelWorldPro/cache/bungee.yml").getConfigurationSection("server.$server") ?: return null
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

    private fun getPublicBungeeConfig(): BuiltOutConfiguration {
        val config = BuiltOutConfiguration("./PixelWorldPro/bungee.yml")
        if (config.getInt("version") < 1) {
            val example = BuiltInConfiguration("example/bungee.yml")
            example.save(config.file)
        }
        return BuiltOutConfiguration("./PixelWorldPro/bungee.yml")
    }

    fun getServer(uuid: UUID) : BungeeData? {
        val player = Bukkit.getPlayer(uuid)
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
        val serverList = publicBungeeConfig.getStringList("group.$group.server")
        var serverData: BungeeData? = null
        log.info(serverList.toString(), true)
        for (server in serverList) {
            val bungeeData = getServerData(server) ?: continue
            log.info(bungeeData.toString(), true)
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
        log.info(serverData?.toString(), true)
        return serverData
    }

    fun checkServer(server: String): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        val config = BuiltOutConfiguration("./PixelWorldPro/cache/bungee.yml")
        config.set("server.$server", null)
        config.saveToFile()
        Thread {
            var id = Random().nextInt(900000000) + 100000000
            if (File("./PixelWorldPro/cache/bungee/server/$id.yml").exists()) {
                id = Random().nextInt(900000000) + 100000000
            }
            val data = JSONObject()
            data["type"] = "WorldLoad"
            data["id"] = id
            Communicate.send(null, server, data)
            var time = 0
            while (true) {
                val serverReturn = BuiltOutConfiguration("./PixelWorldPro/cache/bungee/server/$id.yml").getBoolean("return")
                if (serverReturn) {
                    File("./PixelWorldPro/cache/bungee/server/$id.yml").delete()
                    future.complete(true)
                }
                sleep(1000)
                time ++
                if (time > 10) {
                    File("./PixelWorldPro/cache/bungee/server/$id.yml").delete()
                    future.complete(false)
                    break
                }
            }
        }.start()
        return future
    }

    fun createWorld(owner: UUID, template: String?, seed: Long?) {
        var time = 0
        var server: BungeeData? = null
        while (time < 5) {
            server = getServer(owner)
            if (server == null) {
                time ++
                sleep(1000)
                continue
            }
            val future = checkServer(server.name)
            future.thenApply {
                if (it) {
                    time = 5
                } else {
                    server = null
                    time ++
                }
            }
        }
        if (server == null) {
            return
        }
        val data = JSONObject()
        data["type"] = "WorldCreate"
        data["owner"] = owner
        data["template"] = template
        data["seed"] = seed
        Communicate.send(null, server!!.server, data)
    }

    fun loadWorld(world: PixelWorldProWorld) {
        if (!world.isLoad()) {
            var time = 0
            var server: BungeeData? = null
            while (time < 5) {
                server = getServer(world.worldData.owner)
                if (server == null) {
                    time ++
                    sleep(1000)
                    continue
                }
                val future = checkServer(server.name)
                future.thenApply {
                    if (it) {
                        time = 5
                    } else {
                        server = null
                        time ++
                    }
                }
            }
            if (server == null) {
                return
            }
            val data = JSONObject()
            data["type"] = "WorldLoad"
            data["id"] = world.worldData.id
            Communicate.send(null, server!!.server, data)
        }
    }
}