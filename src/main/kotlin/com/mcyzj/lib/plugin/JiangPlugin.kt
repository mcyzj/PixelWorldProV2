package com.mcyzj.lib.plugin

import org.bukkit.plugin.java.JavaPlugin

abstract class JiangPlugin: JavaPlugin() {
    open fun load() {}

    open fun enable() {}

    open fun disable() {}

    override fun onLoad() {
        load()
    }

    override fun onEnable() {
        JiangLib.loadLibs()
        enable()
    }

    override fun onDisable() {
        disable()
    }
}