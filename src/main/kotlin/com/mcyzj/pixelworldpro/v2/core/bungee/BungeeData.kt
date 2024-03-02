package com.mcyzj.pixelworldpro.v2.core.bungee

data class BungeeData(
    val name: String,
    val server: String,
    val mode: String,
    var tickets: Double,
    val maxTickets: Double,
    var worlds: Int,
    val maxWorlds: Int,
    val load: Boolean
)
