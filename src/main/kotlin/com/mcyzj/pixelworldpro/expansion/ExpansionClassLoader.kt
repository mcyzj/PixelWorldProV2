package com.mcyzj.pixelworldpro.expansion

import com.mcyzj.pixelworldpro.api.Expansion
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.InvalidDescriptionException
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.util.*

class ExpansionClassLoader : URLClassLoader {
    private val classes: MutableMap<String, Class<*>?> = HashMap()


    @JvmField
    val expansion: Expansion
    private val loader: ExpansionManager


    constructor(expansion: Expansion, loader: ExpansionManager, jarFile: File) : super(arrayOf<URL>(jarFile.toURI().toURL())) {
        this.expansion = expansion
        this.loader = loader
    }

    constructor(expansionManager: ExpansionManager, data: YamlConfiguration, jarFile: File, parent: ClassLoader?, name: String) : super(
            arrayOf<URL>(jarFile.toURI().toURL()), parent
    ) {
        loader = expansionManager
        val javaClass: Class<*>
        try {
            val mainClass =
                    data.getString("main") ?: throw java.lang.Exception("PixelWorldPro 扩展没有设置一个主类！")
            javaClass = Class.forName(mainClass, true, this)
            if (mainClass.startsWith("com.mcyzj.pixelworldpro.")) {
                throw java.lang.Exception("PixelWorldPro 扩展的主类不能是 'com.mcyzj.pixelworldpro'")
            }
        } catch (e: Exception) {
            throw InvalidDescriptionException("PixelWorldPro 无法加载 '" + jarFile.name + "' 在文件夹中 '" + jarFile.parent + "' - " + e.message)
        }
        val expansionClass: Class<out Expansion> = try {
            javaClass.asSubclass(Expansion::class.java)
        } catch (e: ClassCastException) {
            throw java.lang.Exception("PixelWorldPro 主类没有扩展")
        }
        expansion = expansionClass.getDeclaredConstructor().newInstance()
        Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 启动本地扩展扩展${jarFile.name}")
        expansion.onEnable()
    }

    public override fun findClass(name: String): Class<*>? {
        return findClass(name, true)
    }


    fun findClass(name: String, checkGlobal: Boolean): Class<*>? {
        if (name.startsWith("world.bentobox.bentobox")) {
            return null
        }
        var result = classes[name]
        if (result == null) {
            if (checkGlobal) {
                result = loader.getClassByName(name) as Class<*>?
            }
            if (result == null) {
                try {
                    result = super.findClass(name)
                } catch (e: ClassNotFoundException) {
                    // Do nothing.
                } catch (e: NoClassDefFoundError) {
                }
                if (result != null) {
                    loader.setClass(name, result)
                }
            }
            classes[name] = result
        }
        return result
    }

    fun getClasses(): Set<String> {
        return classes.keys
    }
}
