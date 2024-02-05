package com.mcyzj.pixelworldpro.bungee

import com.mcyzj.pixelworldpro.PixelWorldPro
import com.mcyzj.pixelworldpro.data.dataclass.ServerData
import com.mcyzj.pixelworldpro.file.Config
import com.mcyzj.pixelworldpro.server.World
import com.xbaimiao.easylib.module.utils.submit
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException
import java.lang.Thread.sleep
import java.util.*


object Server {
    private val localServer = hashMapOf<String, String>()
    private var bungeeConfig = Config.bungee
    val playerTp = HashMap<UUID, String>()

    private var TICK_COUNT = 0
    private var TICKS = LongArray(600)
    private fun getTps(): Double {
        val ticks = 100
        if (TICK_COUNT < ticks) {
            return 20.0
        }
        val target: Int = (TICK_COUNT - 1 - ticks) % TICKS.size
        val elapsed: Long = java.lang.System.currentTimeMillis() - TICKS[target]
        return ticks / (elapsed / 1000.0)
    }

    fun bungeeTp(player: Player, server: String) {
        val bungeeTpShow = bungeeConfig.getBoolean("BungeeTpShow")
        if (bungeeTpShow) {
            var msg = bungeeConfig.getString("BungeeTpShowStr")!!
            msg = msg.replace("{server_showName}",server)
            msg = msg.replace("{server_realName}",server)
            player.sendMessage(msg)
        }
        val byteArray = ByteArrayOutputStream()
        val out = DataOutputStream(byteArray)
        try {
            out.writeUTF("Connect")
            out.writeUTF(server)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        player.sendPluginMessage(PixelWorldPro.instance, "BungeeCord", byteArray.toByteArray())
    }

    fun getLocalServer(): ServerData {
        if ((localServer["mode"] ?: bungeeConfig.getString("Mode")) == "load"){
            if (((bungeeConfig.getInt("MaxWorld")) <= World.localWorld.size).and(bungeeConfig.getInt("MaxWorld") > 0)){
                val type = localServer["type"]
                if (type == null){
                    val list = arrayListOf<String>()
                    list.add("noLoad")
                    list.add("worldMax")
                    localServer["type"] = list.joinToString("||")
                }else{
                    val list = type.split("||") as ArrayList<String>
                    if ("noLoad" !in list){
                        list.add("noLoad")
                    }
                    if ("worldMax" !in list){
                        list.add("worldMax")
                    }
                    localServer["type"] = list.joinToString("||")
                }
            }else{
                val type = localServer["type"]
                try {
                    if (type != null) {
                        val list = type.split("||") as ArrayList<String>
                        if (("noLoad" in list).and("leastTps" !in list)) {
                            list.remove("noLoad")
                        }
                        if ("worldMax" in list) {
                            list.remove("worldMax")
                        }
                        localServer["type"] = list.joinToString("||")
                    }
                }catch (_:Exception){}
            }
            if (((bungeeConfig.getDouble("LeastTps")) > getTps()).and(bungeeConfig.getInt("LeastTps") > 0)){
                val type = localServer["type"]
                if (type == null){
                    val list = arrayListOf<String>()
                    list.add("noLoad")
                    list.add("leastTps")
                    localServer["type"] = list.joinToString("||")
                }else{
                    val list = type.split("||") as ArrayList<String>
                    if ("noLoad" !in list){
                        list.add("noLoad")
                    }
                    if ("leastTps" !in list){
                        list.add("leastTps")
                    }
                    localServer["type"] = list.joinToString("||")
                }
            }else{
                val type = localServer["type"]
                try {
                    if (type != null) {
                        val list = type.split("||") as ArrayList<String>
                        if (("noLoad" in list).and("worldMax" !in list)) {
                            list.remove("noLoad")
                        }
                        if ("leastTps" in list) {
                            list.remove("leastTps")
                        }
                        localServer["type"] = list.joinToString("||")
                    }
                }catch (_:Exception){}
            }
        }
        return ServerData(
            localServer["showName"]?:bungeeConfig.getString("ShowName")!!,
            localServer["realName"]?:bungeeConfig.getString("RealName")!!,
            localServer["mode"]?:bungeeConfig.getString("Mode")!!,
            getTps(),
            localServer["type"]
        )
    }
    fun getCreateServer(): ServerData? {
        val serverMap = System.getAllServer()
        var createServer: ServerData? = null
        for (server in serverMap.values){
            if ((server.mode == "build").and("noLoad" !in (server.type?.split("||") ?: ArrayList()))){
                if (createServer == null){
                    createServer = server
                    continue
                }
                if (createServer.tps < server.tps){
                    createServer = server
                    continue
                }
            }
        }
        return createServer
    }
    fun getLoadServer(): ServerData? {
        val serverMap = System.getAllServer()
        var loadServer: ServerData? = null
        for (server in serverMap.values){
            if ((server.mode == "load").and(("noLoad" !in (server.type?.split("||") ?: ArrayList())).or(server.type == null))){
                if (loadServer == null){
                    loadServer = server
                    continue
                }
                if (loadServer.tps < server.tps){
                    loadServer = server
                    continue
                }
            }
        }
        return loadServer
    }
    fun tpPlayer(){
        Thread{
            while (true) {
                for (uuid in playerTp.keys){
                    val player = Bukkit.getPlayer(uuid) ?: continue
                    val worldName = "PixelWorldPro/${playerTp[player.uniqueId]}/world"
                    val world = Bukkit.getWorld(worldName.lowercase(Locale.getDefault())) ?: continue
                    submit {
                        player.teleport(world.spawnLocation)
                    }
                    playerTp.remove(player.uniqueId)
                }
                sleep(1000)
            }
        }.start()
    }
}