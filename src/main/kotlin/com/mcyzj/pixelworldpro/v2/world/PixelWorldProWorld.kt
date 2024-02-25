package com.mcyzj.pixelworldpro.v2.world

import com.mcyzj.pixelworldpro.v2.PixelWorldPro
import com.mcyzj.pixelworldpro.v2.util.Config
import com.mcyzj.pixelworldpro.v2.world.compress.None
import com.mcyzj.pixelworldpro.v2.world.compress.Zip
import com.mcyzj.pixelworldpro.v2.world.data.WorldLocalData
import com.mcyzj.pixelworldpro.v2.world.dataclass.WorldData


/**
 * PixelWorldPro世界
 * @return: PixelWorldProWorld
 */
@Suppress("unused")
class PixelWorldProWorld(id: Int) {
    /**
     * 拉取世界数据
     */
    val worldData: WorldData

    /**
     * 获取世界压缩锁
     */
    val compressBlock = isCompress()

    /**
     * 获取世界压缩方式
     */
    val compressMethod = getCompressMethod()

    /**
     * 压缩世界
     */
    val compress = compress()

    /**
     * 解压缩世界
     */
    val decompression = decompression()

    private val worldConfig = Config.world

    init {
        worldData = PixelWorldPro.databaseApi.getWorldData(id)!!
    }

    fun getWorldData(): WorldData {
        return worldData
    }
    private fun isCompress(): Boolean {
        return WorldLocalData.getCompressBlock(worldData)
    }
    private fun setCompress(value: Boolean) {
        WorldLocalData.setCompressBlock(worldData, value)
    }
    private fun getCompressMethod(): String {
        return WorldLocalData.getCompressMethod(worldData)
    }

    private fun setCompressMethod(value: String) {
        WorldLocalData.setCompressMethod(worldData, value)
    }
    private fun compress() {
        //开启压缩
        if (compressBlock) {
            when (worldConfig.getString("compress.method")) {
                "None" -> {
                    None.toZip(worldData)
                    setCompressMethod("None")
                }

                "Zip" -> {
                    Zip.toZip(worldData)
                    setCompressMethod("Zip")
                }

                else -> {
                    None.toZip(worldData)
                    setCompressMethod("None")
                }
            }
        }
        //解开世界压缩锁
        setCompress(false)
    }
    private fun decompression() {
        if (!compressBlock) {
            when (compressMethod){
                "None" -> {
                    None.unZip(worldData)
                }

                "Zip" -> {
                    Zip.unZip(worldData)
                }

                else -> {
                    None.unZip(worldData)
                }
            }
            //锁定世界压缩锁
            setCompress(true)
        }
    }
    private fun isLoad() {

    }
}