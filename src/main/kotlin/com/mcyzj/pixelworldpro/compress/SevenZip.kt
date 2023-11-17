package com.mcyzj.pixelworldpro.compress

import com.mcyzj.pixelworldpro.PixelWorldPro
import com.mcyzj.pixelworldpro.file.Config
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry
import org.apache.commons.compress.archivers.sevenz.SevenZFile
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile
import org.apache.commons.lang.StringUtils
import java.io.*


object SevenZip {
    private val logger = PixelWorldPro.instance.logger
    private val config = PixelWorldPro.instance.config
    private val lang = PixelWorldPro.instance.lang
    private val fileConfig = Config.file

    fun toZip(from: String, to: String) {
        try {
            Folder.create(fileConfig.getString("World.Path")!!, to)
            Folder.delete(to, "7z")
            val fromFile = File(fileConfig.getString("World.Server"), from)
            val zipFIle = File(fileConfig.getString("World.Path"), "/$to/$to.7z")
            SevenZOutputFile(zipFIle).use { out ->
                compression7z(
                    out,
                    fromFile,
                    null
                )
                logger.info("${lang.getString("compress.info.success")} $from")
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    @Throws(IOException::class)
    fun compression7z(out: SevenZOutputFile, input: File, name: String?) {
        //7z实体
        var entry: SevenZArchiveEntry?
        val fileName = if (StringUtils.isNotBlank(name)) name + File.separator else ""
        //判断是否是目录
        if (input.isDirectory) {
            val flist = input.listFiles()
            if (flist != null) {
                if (flist.isEmpty()) {
                    entry = out.createArchiveEntry(input, fileName)
                    out.putArchiveEntry(entry)
                } else {
                    for (i in flist.indices) {
                        compression7z(out, flist[i], fileName + flist[i].name)
                    }
                }
            }
            //如果是文件写入
        } else {
            try {
                FileInputStream(input).use { fos ->
                    BufferedInputStream(fos).use { bufferedInputStream ->
                        entry = out.createArchiveEntry(input, name)
                        out.putArchiveEntry(entry)
                        var len: Int
                        val buf = ByteArray(4096)
                        while (bufferedInputStream.read(buf).also { len = it } != -1) {
                            out.write(buf, 0, len)
                        }
                        out.closeArchiveEntry()
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    fun unZip(from: String, to: String) {
        try {
            val toFile = File(fileConfig.getString("World.Server"), to)
            val zipFIle = File(fileConfig.getString("World.Path"), "/$from/$from.7z")
            SevenZFile(zipFIle).use { sevenZFile ->
                val iterator: Iterator<SevenZArchiveEntry> =
                    sevenZFile.entries.iterator()
                while (iterator.hasNext()) {
                    val nextEntry = iterator.next()
                    val name = nextEntry.name
                    val outPath = toFile.path + File.separator + name
                    if (outPath.lastIndexOf("\\") != -1) {
                        val outPathFile = File(outPath)
                        if (!outPathFile.parentFile.exists()) {
                            outPathFile.parentFile.mkdirs()
                        }
                        if (!outPathFile.exists()) {
                            outPathFile.createNewFile()
                        }
                    }
                    FileOutputStream(outPath).use { outputStream ->
                        sevenZFile.getInputStream(nextEntry).use { inputStream ->
                            val bytes = ByteArray(1024)
                            var size: Int
                            while (inputStream.read(bytes).also { size = it } != -1) {
                                outputStream.write(bytes, 0, size)
                            }
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}


