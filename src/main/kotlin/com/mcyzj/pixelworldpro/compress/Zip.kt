package com.mcyzj.pixelworldpro.compress

import com.mcyzj.pixelworldpro.PixelWorldPro
import com.mcyzj.pixelworldpro.file.Config
import java.io.*
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
import kotlin.collections.ArrayList


object Zip {
    private val logger = PixelWorldPro.instance.logger
    private val config = PixelWorldPro.instance.config
    private val lang = PixelWorldPro.instance.lang
    private val file = Config.file
    @JvmStatic
    fun toZip(from: String, to: String) {
        try {
            Folder.create(file.getString("World.Path")!!, to)
            val zipFIle = File(file.getString("World.Path"), "/$to/$to.zip")
            ZipOutputStream(FileOutputStream(zipFIle)).use { zipOutputStream ->
                // 压缩文件夹
                compressFolder(from, from, zipOutputStream)
                logger.info("${lang.getString("compress.info.success")} $from")
                //zipOutputStream.closeEntry()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun compressFolder(sourceFolder: String, folderName: String, zipOutputStream: ZipOutputStream) {
        val folder = File(file.getString("World.Server"), sourceFolder)
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
                    }catch (_:Exception){}
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
    fun unzip(zip: String, to: String) {
        //targetPath输出文件路径
        val targetFile = File(file.getString("World.Server"), to)
        // 如果目录不存在，则创建
        if (!targetFile.exists()) {
            targetFile.mkdirs()
        }
        //sourcePath压缩包文件路径
        try {
            ZipFile(File(file.getString("World.Path"), "/$zip/$zip.zip")).use { zipFile ->
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
                        val outFile = File(file.getString("World.Server"), name)
                        val pathList = name.split("/") as ArrayList
                        pathList.removeAt(name.split("/").size - 1)
                        val temp = File(file.getString("World.Server"), pathList.joinToString("/"))
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