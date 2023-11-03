package com.mcyzj.pixelworldpro.config

import com.xbaimiao.easylib.module.chat.BuiltInConfiguration

object Config {
    var config = BuiltInConfiguration("Config.yml")
    var file = BuiltInConfiguration("File.yml")
    var world = BuiltInConfiguration("World.yml")
    var permission = BuiltInConfiguration("Permission.yml")
    fun reload(){
        config = BuiltInConfiguration("Config.yml")
        file = BuiltInConfiguration("File.yml")
        world = BuiltInConfiguration("World.yml")
        permission = BuiltInConfiguration("Permission.yml")
    }
}