package com.mcyzj.pixelworldpro.v2.core.bungee

import com.mcyzj.lib.plugin.file.BuiltOutConfiguration
import com.mcyzj.pixelworldpro.v2.core.PixelWorldPro
import com.mcyzj.pixelworldpro.v2.core.bungee.redis.Communicate
import com.mcyzj.pixelworldpro.v2.core.util.Config
import com.mcyzj.pixelworldpro.v2.core.world.WorldImpl
import com.mcyzj.pixelworldpro.v2.core.world.LocalWorld
import com.xbaimiao.easylib.module.chat.BuiltInConfiguration
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.json.simple.JSONObject
import java.io.File
import java.lang.Thread.sleep
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.collections.HashMap

object BungeeWorldImpl {
    private val bungeeConfig = Config.bungee

    fun getBungeeData(): BungeeData {
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

    fun createWorld(owner: UUID, template: String?, seed: Long?):CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        Thread {
            val server = BungeeWorld().getServer(owner)
            if (server == null) {
                future.complete(false)
                return@Thread
            }
            val response = BungeeWorld().getResponseId()
            val data = JSONObject()
            data["type"] = "WorldCreate"
            data["owner"] = owner
            data["template"] = template
            data["seed"] = seed
            data["response"] = response
            Communicate.send(null, server.server, "local", data)
            future.complete(BungeeWorld().getServerResponse(response, 120).get())
            return@Thread
        }.start()
        return future
    }
}