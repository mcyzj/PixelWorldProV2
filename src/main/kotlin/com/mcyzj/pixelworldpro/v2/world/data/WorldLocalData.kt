package com.mcyzj.pixelworldpro.v2.world.data

import com.mcyzj.lib.plugin.file.BuiltOutConfiguration
import com.mcyzj.pixelworldpro.v2.world.dataclass.WorldData

object WorldLocalData {
    fun getCompressBlock(worldData: WorldData): Boolean {
        val blockConfig = BuiltOutConfiguration("./PixelWorldPro/world/${worldData.id}/data/compress.yml")
        return blockConfig.getBoolean("block")
    }
    fun setCompressBlock(worldData: WorldData, compress: Boolean){
        val blockConfig = BuiltOutConfiguration("./PixelWorldPro/world/${worldData.id}/data/compress.yml")
        blockConfig.set("block", compress)
        blockConfig.saveToFile()
    }
    fun getCompressMethod(worldData: WorldData): String {
        val blockConfig = BuiltOutConfiguration("./PixelWorldPro/world/${worldData.id}/data/compress.yml")
        return blockConfig.getString("method") ?: "None"
    }
    fun setCompressMethod(worldData: WorldData, method: String){
        val blockConfig = BuiltOutConfiguration("./PixelWorldPro/world/${worldData.id}/data/compress.yml")
        blockConfig.set("method", method)
        blockConfig.saveToFile()
    }
}
