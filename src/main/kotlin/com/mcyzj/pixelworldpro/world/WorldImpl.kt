package com.mcyzj.pixelworldpro.world

import com.mcyzj.pixelworldpro.PixelWorldPro
import com.mcyzj.pixelworldpro.api.interfaces.WorldAPI
import java.text.SimpleDateFormat
import java.util.*

abstract class WorldImpl : WorldAPI {
    private val logger = PixelWorldPro.instance.logger
    override fun createWorld(owner: UUID, template: String): Boolean {
        logger.info("§aPixelWorldPro 使用线程：${Thread.currentThread().name} 进行世界创建操作")
        //获取time时间
        val time = System.currentTimeMillis()
        val date = Date(time)
        //把time时间格式化
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")
        //把time时间格式化为字符串
        val timeString = formatter.format(date)
        //获取路径下对应的world文件夹
        val worldName = "${owner}_$timeString"

    }

    override fun loadWorld(id: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun loadWorld(owner: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override fun unloadWorld(id: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun unloadWorld(owner: UUID): Boolean {
        TODO("Not yet implemented")
    }
}