package com.mcyzj.pixelworldpro.v2.util

import com.xbaimiao.easylib.module.chat.BuiltInConfiguration

object Config {
    var config = BuiltInConfiguration("config.yml")
    var world = BuiltInConfiguration("world.yml")
    var permission = BuiltInConfiguration("permission.yml")

    fun getLang(): BuiltInConfiguration {
        val lang = config.get("lang") ?: "zh_cn"
        return BuiltInConfiguration("lang/${lang}.yml")
    }
}