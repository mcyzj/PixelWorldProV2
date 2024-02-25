package com.mcyzj.pixelworldpro.v2.world.compress

import com.mcyzj.pixelworldpro.v2.PixelWorldPro
import com.mcyzj.pixelworldpro.v2.util.Config
import com.mcyzj.pixelworldpro.v2.world.dataclass.WorldData
import java.io.*
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream


object Zip {
    private val logger = PixelWorldPro.log
    private val lang = Config.getLang()
    @JvmStatic
    fun toZip(worldData: WorldData) {
        try {
            //拉取并清除旧世界数据
            val worldFile = File(File("./PixelWorldPro/world", worldData.id.toString()), "world")
            if (worldFile.exists()){
                worldFile.deleteRecursively()
            }
            worldFile.mkdirs()
            val zipFIle = File("${worldFile.path}/world.zip")
            ZipOutputStream(FileOutputStream(zipFIle)).use { zipOutputStream ->
                // 压缩文件夹
                compressFolder(worldData.id.toString(), worldData.id.toString(), zipOutputStream)
                logger.info("${lang.getString("compress.info.success")} ${worldData.id}")
                //zipOutputStream.closeEntry()
            }
            File("./PixelWorldPro/cache/world", worldData.id.toString()).deleteRecursively()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun compressFolder(sourceFolder: String, folderName: String, zipOutputStream: ZipOutputStream) {
        val folder = File("./PixelWorldPro/cache/world", sourceFolder)
        val files = folder.listFiles()
        if (files != null) {
            for (file in files) {
                if (file.isDirectory) {
                    // 压缩子文件夹
                    compressFolder(folderName + "/" + file.name, folderName + "/" + file.name, zipOutputStream)
                } else {
                    try {
                        // 压缩文件
                        addToZipFile(folderName + "/" + file.name, file.absolutePath, zipOutputStream)
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun addToZipFile(fileName: String, fileAbsolutePath: String, zipOutputStream: ZipOutputStream) {
        // 创建ZipEntry对象并设置文件名
        val entry = ZipEntry(fileName)
        zipOutputStream.putNextEntry(entry)
        FileInputStream(fileAbsolutePath).use { fileInputStream ->
            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (fileInputStream.read(buffer).also { bytesRead = it } != -1) {
                zipOutputStream.write(buffer, 0, bytesRead)
            }
        }
    }

    @JvmStatic
    fun unZip(worldData: WorldData) {
        //targetPath输出文件路径
        val targetFile = File("./PixelWorldPro/cache/world", worldData.id.toString())
        // 如果目录不存在，则创建
        if (!targetFile.exists()) {
            targetFile.mkdirs()
        }
        //sourcePath压缩包文件路径
        try {
            ZipFile(File("./PixelWorldPro/world/${worldData.id}/world", "world.zip")).use { zipFile ->
                val enumeration: Enumeration<*> = zipFile.entries()
                while (enumeration.hasMoreElements()) {
                    //依次获取压缩包内的文件实体对象
                    val entry = enumeration.nextElement() as ZipEntry
                    val name = entry.name
                    if (entry.isDirectory) {
                        continue
                    }
                    BufferedInputStream(zipFile.getInputStream(entry)).use { inputStream ->
                        // 需要判断文件所在的目录是否存在，处理压缩包里面有文件夹的情况
                        val outFile = File("./PixelWorldPro/cache/world", name)
                        val pathList = name.split("/") as ArrayList
                        pathList.removeAt(name.split("/").size - 1)
                        val temp = File("./PixelWorldPro/cache/world", pathList.joinToString("/"))
                        if (!temp.exists()) {
                            temp.mkdirs()
                        }
                        BufferedOutputStream(FileOutputStream(outFile)).use { outputStream ->
                            var len: Int
                            val buffer = ByteArray(1024)
                            while (inputStream.read(buffer).also { len = it } > 0) {
                                outputStream.write(buffer, 0, len)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}