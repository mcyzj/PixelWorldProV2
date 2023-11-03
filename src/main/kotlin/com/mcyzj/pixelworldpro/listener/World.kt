package com.mcyzj.pixelworldpro.listener

import com.mcyzj.pixelworldpro.PixelWorldPro
import com.mcyzj.pixelworldpro.world.Local.getWorldNameUUID
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

class World : Listener {
    @EventHandler
    fun worldChange(e: PlayerChangedWorldEvent) {
        if (PixelWorldPro.instance.config.getBoolean("debug")){
            Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 监听世界改变")
        }
        if (e.player.isOp) {
            if (PixelWorldPro.instance.config.getBoolean("debug")){
                Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 改变对象为op，监听结束")
            }
            return
        }
        val worldName = getWorldNameUUID(e.player.world.name)
        if (worldName == null){
            if (PixelWorldPro.instance.config.getBoolean("debug")){
                Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 世界数据获取为空，监听结束")
            }
            return
        }
        val worldData = PixelWorldPro.databaseApi.getWorldData(worldName)
        if (worldData == null){
            if (PixelWorldPro.instance.config.getBoolean("debug")){
                Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 世界数据获取为空，监听结束")
            }
            return
        }
        //获取世界权限
        val permission = worldData.permission
        if (e.player.uniqueId == worldData.owner){
            val permissionData = permission["Owner"]!!
            when (permissionData["GameMode"]){
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
            when (permissionData["GameMode"]){
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
        val permissionData = permission["Visitor"]!!
        when (permissionData["GameMode"]){
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
        if (PixelWorldPro.instance.config.getBoolean("debug")){
            Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 监听传送事件")
        }
        if (e.player.isOp) {
            if (PixelWorldPro.instance.config.getBoolean("debug")){
                Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 改变对象为op，监听结束")
            }
            return
        }
        val worldName = getWorldNameUUID(e.player.world.name)
        if (worldName == null){
            if (PixelWorldPro.instance.config.getBoolean("debug")){
                Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 世界数据获取为空，监听结束")
            }
            return
        }
        val worldData = PixelWorldPro.databaseApi.getWorldData(worldName)
        if (worldData == null){
            if (PixelWorldPro.instance.config.getBoolean("debug")){
                Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 世界数据获取为空，监听结束")
            }
            return
        }
        //获取世界权限
        val permission = worldData.permission
        if (e.player.uniqueId == worldData.owner){
            val permissionData = permission["Owner"]!!
            when (permissionData["Fly"]){
                "True" -> {
                    e.player.allowFlight = true
                }
            }
            return
        }
        if (worldData.player[e.player.uniqueId] != null){
            val permissionData = permission[worldData.player[e.player.uniqueId]]!!
            when (permissionData["Teleport"]){
                "False" -> {
                    e.isCancelled = true
                }
            }
            when (permissionData["Fly"]){
                "True" -> {
                    e.player.allowFlight = true
                }
            }
            return
        }
        val permissionData = permission["Visitor"]!!
        when (permissionData["Fly"]){
            "True" -> {
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
        val worldName = getWorldNameUUID(e.player.world.name)
        if (worldName == null){
            if (PixelWorldPro.instance.config.getBoolean("debug")){
                Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 世界数据获取为空，监听结束")
            }
            return
        }
        val worldData = PixelWorldPro.databaseApi.getWorldData(worldName)
        if (worldData == null){
            if (PixelWorldPro.instance.config.getBoolean("debug")){
                Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 世界数据获取为空，监听结束")
            }
            return
        }
        //获取世界权限
        val permission = worldData.permission
        if (e.player.uniqueId == worldData.owner){
            val permissionData = permission["Owner"]!!
            when (permissionData["BlockRightClick"]){
                "False" -> {
                    e.isCancelled = true
                }
            }
            return
        }
        if (worldData.player[e.player.uniqueId] != null){
            val permissionData = permission[worldData.player[e.player.uniqueId]]!!
            when (permissionData["BlockRightClick"]){
                "False" -> {
                    e.isCancelled = true
                }
            }
            return
        }
        val permissionData = permission["Visitor"]!!
        when (permissionData["BlockRightClick"]){
            "False" -> {
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
        val worldName = getWorldNameUUID(e.player.world.name)
        if (worldName == null){
            if (PixelWorldPro.instance.config.getBoolean("debug")){
                Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 世界数据获取为空，监听结束")
            }
            return
        }
        val worldData = PixelWorldPro.databaseApi.getWorldData(worldName)
        if (worldData == null){
            if (PixelWorldPro.instance.config.getBoolean("debug")){
                Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 世界数据获取为空，监听结束")
            }
            return
        }
        //获取世界权限
        val permission = worldData.permission
        if (e.player.uniqueId == worldData.owner){
            val permissionData = permission["Owner"]!!
            when (permissionData["BlockDamage"]){
                "False" -> {
                    e.isCancelled = true
                }
            }
            return
        }
        if (worldData.player[e.player.uniqueId] != null){
            val permissionData = permission[worldData.player[e.player.uniqueId]]!!
            when (permissionData["BlockDamage"]){
                "False" -> {
                    e.isCancelled = true
                }
            }
            return
        }
        val permissionData = permission["Visitor"]!!
        when (permissionData["BlockDamage"]){
            "False" -> {
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
        val worldName = getWorldNameUUID(e.player.world.name)
        if (worldName == null){
            if (PixelWorldPro.instance.config.getBoolean("debug")){
                Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 世界数据获取为空，监听结束")
            }
            return
        }
        val worldData = PixelWorldPro.databaseApi.getWorldData(worldName)
        if (worldData == null){
            if (PixelWorldPro.instance.config.getBoolean("debug")){
                Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 世界数据获取为空，监听结束")
            }
            return
        }
        //获取世界权限
        val permission = worldData.permission
        if (e.player.uniqueId == worldData.owner){
            val permissionData = permission["Owner"]!!
            when (permissionData["RightClickEntity"]){
                "False" -> {
                    e.isCancelled = true
                }
            }
            return
        }
        if (worldData.player[e.player.uniqueId] != null){
            val permissionData = permission[worldData.player[e.player.uniqueId]]!!
            when (permissionData["RightClickEntity"]){
                "False" -> {
                    e.isCancelled = true
                }
            }
            return
        }
        val permissionData = permission["Visitor"]!!
        when (permissionData["RightClickEntity"]){
            "False" -> {
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
            val worldName = getWorldNameUUID(e.damager.world.name)
            if (worldName == null) {
                if (PixelWorldPro.instance.config.getBoolean("debug")) {
                    Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 世界数据获取为空，监听结束")
                }
                return
            }
            val worldData = PixelWorldPro.databaseApi.getWorldData(worldName)
            if (worldData == null) {
                if (PixelWorldPro.instance.config.getBoolean("debug")) {
                    Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 世界数据获取为空，监听结束")
                }
                return
            }
            //获取世界权限
            val permission = worldData.permission
            if (e.damager.uniqueId == worldData.owner) {
                val permissionData = permission["Owner"]!!
                when (permissionData["AttackEntity"]) {
                    "False" -> {
                        e.isCancelled = true
                    }
                }
                return
            }
            if (worldData.player[e.damager.uniqueId] != null) {
                val permissionData = permission[worldData.player[e.damager.uniqueId]]!!
                when (permissionData["AttackEntity"]) {
                    "False" -> {
                        e.isCancelled = true
                    }
                }
                return
            }
            val permissionData = permission["Visitor"]!!
            when (permissionData["AttackEntity"]) {
                "False" -> {
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
            val worldName = getWorldNameUUID(e.entity.world.name)
            if (worldName == null) {
                if (PixelWorldPro.instance.config.getBoolean("debug")) {
                    Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 世界数据获取为空，监听结束")
                }
                return
            }
            val worldData = PixelWorldPro.databaseApi.getWorldData(worldName)
            if (worldData == null) {
                if (PixelWorldPro.instance.config.getBoolean("debug")) {
                    Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 世界数据获取为空，监听结束")
                }
                return
            }
            //获取世界权限
            val permission = worldData.permission
            if (e.entity.uniqueId == worldData.owner) {
                val permissionData = permission["Owner"]!!
                when (permissionData["AttackEntity"]) {
                    "False" -> {
                        e.isCancelled = true
                    }
                }
                return
            }
            if (worldData.player[e.entity.uniqueId] != null) {
                val permissionData = permission[worldData.player[e.entity.uniqueId]]!!
                when (permissionData["AttackEntity"]) {
                    "False" -> {
                        e.isCancelled = true
                    }
                }
                return
            }
            val permissionData = permission["Visitor"]!!
            when (permissionData["AttackEntity"]) {
                "False" -> {
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
            val worldName = getWorldNameUUID(e.damager.world.name)
            if (worldName == null) {
                if (PixelWorldPro.instance.config.getBoolean("debug")) {
                    Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 世界数据获取为空，监听结束")
                }
                return
            }
            val worldData = PixelWorldPro.databaseApi.getWorldData(worldName)
            if (worldData == null) {
                if (PixelWorldPro.instance.config.getBoolean("debug")) {
                    Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 世界数据获取为空，监听结束")
                }
                return
            }
            //获取世界权限
            val permission = worldData.permission
            var damage = "True"
            if (e.damager.uniqueId == worldData.owner) {
                val permissionData = permission["Owner"]!!
                when (permissionData["AttackPlayer"]) {
                    "False" -> {
                        damage = "False"
                    }
                }
            }
            if (worldData.player[e.damager.uniqueId] != null) {
                val permissionData = permission[worldData.player[e.damager.uniqueId]]!!
                when (permissionData["AttackPlayer"]) {
                    "False" -> {
                        damage = "False"
                    }
                }
            } else {
                val permissionData = permission["Visitor"]!!
                when (permissionData["AttackPlayer"]) {
                    "False" -> {
                        damage = "False"
                    }
                }
            }

            val permission2 = worldData.permission
            var entity = "True"
            if (e.entity.uniqueId == worldData.owner) {
                val permissionData2 = permission2["Owner"]!!
                when (permissionData2["AttackPlayer"]) {
                    "False" -> {
                        entity = "False"
                    }
                }
            }
            if (worldData.player[e.entity.uniqueId] != null) {
                val permissionData2 = permission2[worldData.player[e.entity.uniqueId]]!!
                when (permissionData2["AttackPlayer"]) {
                    "False" -> {
                        entity = "False"
                    }
                }
            } else {
                val permissionData2 = permission2["Visitor"]!!
                when (permissionData2["AttackPlayer"]) {
                    "False" -> {
                        entity = "False"
                    }
                }
            }
            if (!((damage == "True").and(entity == "True"))){
                e.isCancelled = true
                return
            }
        }
    }
}