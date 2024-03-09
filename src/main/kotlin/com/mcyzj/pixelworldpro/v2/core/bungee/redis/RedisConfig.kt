package com.mcyzj.pixelworldpro.v2.core.bungee.redis

import org.bukkit.configuration.Configuration
import redis.clients.jedis.JedisPoolConfig

class RedisConfig(val host: String?, val port: Int) : JedisPoolConfig() {

    var password: String? = null
        private set
    var channel: String? = "PixelWorldProV2"
        private set

    constructor(configuration: Configuration) : this(
        configuration.getString("redis.host"),
        configuration.getInt("redis.port"),
    ) {
        val channel = configuration.getString("channel") ?: "PixelWorldProV2"
        val password = configuration.getString("redis.password")
        if (!password.isNullOrEmpty()) {
            this.password = password
        }
    }
}
