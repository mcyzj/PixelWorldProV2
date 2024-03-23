package com.mcyzj.pixelworldpro.v2.core.world

import com.mcyzj.pixelworldpro.v2.core.PixelWorldPro
import com.mcyzj.pixelworldpro.v2.core.api.PixelWorldProApi
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDamageEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerTeleportEvent
import java.lang.Thread.sleep

class WorldListener : Listener {
    private val log = PixelWorldPro.instance.log
    @EventHandler
    fun worldChange(e: PlayerChangedWorldEvent) {
        log.info("监听世界改变", true)
        if (e.player.isOp) {
            log.info("改变对象为op，监听结束", true)
            return
        }
        val world = PixelWorldProApi().getWorld(e.player.world.name)
        if (world == null){
            log.info("世界数据获取为空，监听结束", true)
            return
        }
        val worldData = world.worldData
        //获取世界权限
        val permission = worldData.permission
        if (e.player.uniqueId == worldData.owner){
            val permissionData = permission["owner"]!!
            when (permissionData["gameMode"]){
                "ADVENTURE" -> {
                    e.player.gameMode = GameMode.ADVENTURE
                    return
                }

                "SURVIVAL" -> {
                    e.player.gameMode = GameMode.SURVIVAL
                    return
                }

                "CREATIVE" -> {
                    e.player.gameMode = GameMode.CREATIVE
                    return
                }

                "SPECTATOR" -> {
                    e.player.gameMode = GameMode.SPECTATOR
                    return
                }
            }
        }
        if (worldData.player[e.player.uniqueId] != null){
            val permissionData = permission[worldData.player[e.player.uniqueId]]!!
            when (permissionData["gameMode"]){
                "ADVENTURE" -> {
                    e.player.gameMode = GameMode.ADVENTURE
                    return
                }

                "SURVIVAL" -> {
                    e.player.gameMode = GameMode.SURVIVAL
                    return
                }

                "CREATIVE" -> {
                    e.player.gameMode = GameMode.CREATIVE
                    return
                }

                "SPECTATOR" -> {
                    e.player.gameMode = GameMode.SPECTATOR
                    return
                }
            }
        }
        val permissionData = permission["visitor"]!!
        when (permissionData["gameMode"]){
            "ADVENTURE" -> {
                e.player.gameMode = GameMode.ADVENTURE
                return
            }

            "SURVIVAL" -> {
                e.player.gameMode = GameMode.SURVIVAL
                return
            }

            "CREATIVE" -> {
                e.player.gameMode = GameMode.CREATIVE
                return
            }

            "SPECTATOR" -> {
                e.player.gameMode = GameMode.SPECTATOR
                return
            }
        }
    }

    @EventHandler
    fun teleport(e: PlayerTeleportEvent) {
        if (e.from.world == e.to.world) {
            return
        }
        if (PixelWorldPro.instance.config.getBoolean("debug")){
            Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 监听传送事件")
        }
        if (e.player.isOp) {
            if (PixelWorldPro.instance.config.getBoolean("debug")){
                Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 改变对象为op，监听结束")
            }
            return
        }
        val world = PixelWorldProApi().getWorld(e.to.world.name)
        if (world == null){
            if (PixelWorldPro.instance.config.getBoolean("debug")){
                Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 世界数据获取为空，监听结束")
            }
            return
        }
        val worldData = world.worldData
        Thread {
            sleep(10 * 1000)
            WorldImpl.updateWorldPlayer(PixelWorldProWorld(worldData))
            val fromWorldData = PixelWorldProApi().getWorld(e.from.world.name)
            if (fromWorldData != null) {
                WorldImpl.updateWorldPlayer(fromWorldData)
            }
        }.start()
        //获取世界权限
        val permission = worldData.permission
        if (e.player.uniqueId == worldData.owner){
            val permissionData = permission["owner"]!!
            when (permissionData["fly"]){
                "true" -> {
                    e.player.allowFlight = true
                }
            }
            return
        }
        if (worldData.player[e.player.uniqueId] != null){
            val permissionData = permission[worldData.player[e.player.uniqueId]]!!
            when (permissionData["teleport"]){
                "false" -> {
                    e.isCancelled = true
                }
            }
            when (permissionData["fly"]){
                "true" -> {
                    e.player.allowFlight = true
                }
            }
            return
        }
        val permissionData = permission["visitor"]!!
        when (permissionData["fly"]){
            "true" -> {
                e.player.allowFlight = true
            }
        }
    }

    @EventHandler
    fun rightClickBlock(e: PlayerInteractEvent) {
        if (PixelWorldPro.instance.config.getBoolean("debug")){
            Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 监听右键点击事件")
        }
        if (e.player.isOp) {
            if (PixelWorldPro.instance.config.getBoolean("debug")){
                Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 改变对象为op，监听结束")
            }
            return
        }
        val world = PixelWorldProApi().getWorld(e.player.world.name)
        if (world == null){
            if (PixelWorldPro.instance.config.getBoolean("debug")){
                Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 世界数据获取为空，监听结束")
            }
            return
        }
        val worldData = world.worldData
        //获取世界权限
        val permission = worldData.permission
        Bukkit.getConsoleSender().sendMessage(permission.toString())
        if (e.player.uniqueId == worldData.owner){
            val permissionData = permission["owner"]!!
            when (permissionData["blockRightClick"]){
                "false" -> {
                    e.isCancelled = true
                }
            }
            return
        }
        if (worldData.player[e.player.uniqueId] != null){
            val permissionData = permission[worldData.player[e.player.uniqueId]]!!
            when (permissionData["blockRightClick"]){
                "false" -> {
                    e.isCancelled = true
                }
            }
            return
        }
        val permissionData = permission["visitor"]!!
        when (permissionData["blockRightClick"]){
            "false" -> {
                e.isCancelled = true
            }
        }
        return
    }

    @EventHandler
    fun blockDamage(e: BlockDamageEvent) {
        if (PixelWorldPro.instance.config.getBoolean("debug")){
            Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 监听方块破坏事件")
        }
        if (e.player.isOp) {
            if (PixelWorldPro.instance.config.getBoolean("debug")){
                Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 改变对象为op，监听结束")
            }
            return
        }
        val world = PixelWorldProApi().getWorld(e.player.world.name)
        if (world == null){
            if (PixelWorldPro.instance.config.getBoolean("debug")){
                Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 世界数据获取为空，监听结束")
            }
            return
        }
        val worldData = world.worldData
        //获取世界权限
        val permission = worldData.permission
        if (e.player.uniqueId == worldData.owner){
            val permissionData = permission["owner"]!!
            when (permissionData["blockDamage"]){
                "false" -> {
                    e.isCancelled = true
                }
            }
            return
        }
        if (worldData.player[e.player.uniqueId] != null){
            val permissionData = permission[worldData.player[e.player.uniqueId]]!!
            when (permissionData["blockDamage"]){
                "false" -> {
                    e.isCancelled = true
                }
            }
            return
        }
        val permissionData = permission["visitor"]!!
        when (permissionData["blockDamage"]){
            "false" -> {
                e.isCancelled = true
            }
        }
        return
    }

    @EventHandler
    fun rightClickEntity(e: PlayerInteractEntityEvent) {
        if (PixelWorldPro.instance.config.getBoolean("debug")){
            Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 监听右键点击事件")
        }
        if (e.player.isOp) {
            if (PixelWorldPro.instance.config.getBoolean("debug")){
                Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 改变对象为op，监听结束")
            }
            return
        }
        val world = PixelWorldProApi().getWorld(e.player.world.name)
        if (world == null){
            if (PixelWorldPro.instance.config.getBoolean("debug")){
                Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 世界数据获取为空，监听结束")
            }
            return
        }
        val worldData = world.worldData
        //获取世界权限
        val permission = worldData.permission
        if (e.player.uniqueId == worldData.owner){
            val permissionData = permission["owner"]!!
            when (permissionData["rightClickEntity"]){
                "false" -> {
                    e.isCancelled = true
                }
            }
            return
        }
        if (worldData.player[e.player.uniqueId] != null){
            val permissionData = permission[worldData.player[e.player.uniqueId]]!!
            when (permissionData["rightClickEntity"]){
                "false" -> {
                    e.isCancelled = true
                }
            }
            return
        }
        val permissionData = permission["visitor"]!!
        when (permissionData["rightClickEntity"]){
            "false" -> {
                e.isCancelled = true
            }
        }
        return
    }

    @EventHandler
    fun createDamage(e: EntityDamageByEntityEvent) {
        if (PixelWorldPro.instance.config.getBoolean("debug")) {
            Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 监听右键点击事件")
        }
        if ((!e.damager.isEmpty).and(e.entity.isEmpty)) {
            if (e.damager.isOp) {
                if (PixelWorldPro.instance.config.getBoolean("debug")) {
                    Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 改变对象为op，监听结束")
                }
                return
            }
            val world = PixelWorldProApi().getWorld(e.damager.world.name)
            if (world == null){
                if (PixelWorldPro.instance.config.getBoolean("debug")){
                    Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 世界数据获取为空，监听结束")
                }
                return
            }
            val worldData = world.worldData
            //获取世界权限
            val permission = worldData.permission
            if (e.damager.uniqueId == worldData.owner) {
                val permissionData = permission["owner"]!!
                when (permissionData["attackEntity"]) {
                    "false" -> {
                        e.isCancelled = true
                    }
                }
                return
            }
            if (worldData.player[e.damager.uniqueId] != null) {
                val permissionData = permission[worldData.player[e.damager.uniqueId]]!!
                when (permissionData["attackEntity"]) {
                    "false" -> {
                        e.isCancelled = true
                    }
                }
                return
            }
            val permissionData = permission["visitor"]!!
            when (permissionData["attackEntity"]) {
                "false" -> {
                    e.isCancelled = true
                }
            }
            return
        }
        if ((!e.entity.isEmpty).and(e.damager.isEmpty)) {
            if (e.entity.isOp) {
                if (PixelWorldPro.instance.config.getBoolean("debug")) {
                    Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 改变对象为op，监听结束")
                }
                return
            }
            val world = PixelWorldProApi().getWorld(e.entity.world.name)
            if (world == null){
                if (PixelWorldPro.instance.config.getBoolean("debug")){
                    Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 世界数据获取为空，监听结束")
                }
                return
            }
            val worldData = world.worldData
            //获取世界权限
            val permission = worldData.permission
            if (e.entity.uniqueId == worldData.owner) {
                val permissionData = permission["owner"]!!
                when (permissionData["attackEntity"]) {
                    "false" -> {
                        e.isCancelled = true
                    }
                }
                return
            }
            if (worldData.player[e.entity.uniqueId] != null) {
                val permissionData = permission[worldData.player[e.entity.uniqueId]]!!
                when (permissionData["attackEntity"]) {
                    "false" -> {
                        e.isCancelled = true
                    }
                }
                return
            }
            val permissionData = permission["visitor"]!!
            when (permissionData["attackEntity"]) {
                "false" -> {
                    e.isCancelled = true
                }
            }
            return
        }

        if ((!e.damager.isEmpty).and(!e.entity.isEmpty)) {
            if (e.damager.isOp) {
                if (PixelWorldPro.instance.config.getBoolean("debug")) {
                    Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 改变对象为op，监听结束")
                }
                return
            }
            val world = PixelWorldProApi().getWorld(e.damager.world.name)
            if (world == null){
                if (PixelWorldPro.instance.config.getBoolean("debug")){
                    Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 世界数据获取为空，监听结束")
                }
                return
            }
            val worldData = world.worldData
            //获取世界权限
            val permission = worldData.permission
            var damage = "true"
            if (e.damager.uniqueId == worldData.owner) {
                val permissionData = permission["owner"]!!
                when (permissionData["attackPlayer"]) {
                    "false" -> {
                        damage = "false"
                    }
                }
            }
            if (worldData.player[e.damager.uniqueId] != null) {
                val permissionData = permission[worldData.player[e.damager.uniqueId]]!!
                when (permissionData["attackPlayer"]) {
                    "false" -> {
                        damage = "false"
                    }
                }
            } else {
                val permissionData = permission["visitor"]!!
                when (permissionData["attackPlayer"]) {
                    "false" -> {
                        damage = "false"
                    }
                }
            }

            val permission2 = worldData.permission
            var entity = "true"
            if (e.entity.uniqueId == worldData.owner) {
                val permissionData2 = permission2["owner"]!!
                when (permissionData2["attackPlayer"]) {
                    "false" -> {
                        entity = "false"
                    }
                }
            }
            if (worldData.player[e.entity.uniqueId] != null) {
                val permissionData2 = permission2[worldData.player[e.entity.uniqueId]]!!
                when (permissionData2["attackPlayer"]) {
                    "false" -> {
                        entity = "false"
                    }
                }
            } else {
                val permissionData2 = permission2["visitor"]!!
                when (permissionData2["attackPlayer"]) {
                    "false" -> {
                        entity = "false"
                    }
                }
            }
            if (!((damage == "true").and(entity == "true"))){
                e.isCancelled = true
                return
            }
        }
    }
}