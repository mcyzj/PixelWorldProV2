package com.mcyzj.lib.plugin

import com.mcyzj.lib.plugin.file.BuiltInConfiguration
import org.bukkit.Bukkit

object Logger {
    private val config = BuiltInConfiguration("config.yml")
    fun info(msg: String?, debug: Boolean? = false){
        if (debug == true) {
            if (config.getBoolean("debug")){
                Bukkit.getLogger().info(msg)
            }
        } else {
            Bukkit.getLogger().info(msg)
        }
    }
    fun warning(msg: String?, debug: Boolean?=false){
        if (debug == true) {
            if (config.getBoolean("debug")){
                Bukkit.getLogger().warning(msg)
            }
        } else {
            Bukkit.getLogger().warning(msg)
        }
    }
}