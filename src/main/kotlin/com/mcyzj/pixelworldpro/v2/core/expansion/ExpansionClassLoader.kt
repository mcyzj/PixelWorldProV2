package com.mcyzj.pixelworldpro.v2.core.expansion

import org.bukkit.plugin.InvalidDescriptionException
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.util.*

@Suppress("unused")
class ExpansionClassLoader(
    expansionManager: ExpansionManager,
    data: ExpansionData,
    jarFile: File,
    parent: ClassLoader?
) : URLClassLoader(
    arrayOf<URL>(jarFile.toURI().toURL()), parent
) {
    private val classes: MutableMap<String, Class<*>?> = HashMap()


    @JvmField
    val expansion: Expansion
    private val loader: ExpansionManager = expansionManager

    init {
        val javaClass: Class<*>
        try {
            val mainClass = data.main
            javaClass = Class.forName(mainClass, true, this)
            if (mainClass.startsWith("com.mcyzj.pixelworldpro.v2.core")) {
                throw java.lang.Exception("The main class of a PixelWorldPro extension cannot be 'com.mcyzj.pixelworldpro.v2.core'")
            }
        } catch (e: Exception) {
            throw InvalidDescriptionException("Could not load class file ${jarFile.name}")
        }
        val expansionClass: Class<out Expansion> = try {
            javaClass.asSubclass(Expansion::class.java)
        } catch (e: ClassCastException) {
            throw java.lang.Exception("PixelWorldPro main class does not have an extension")
        }
        expansion = expansionClass.getDeclaredConstructor().newInstance()
        expansion.onEnable()
    }

    public override fun findClass(name: String): Class<*>? {
        return findClass(name, true)
    }


    fun findClass(name: String, checkGlobal: Boolean): Class<*>? {
        if (name.startsWith("com.mcyzj.pixelworldpro.v2.core")) {
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
                } catch (_: NoClassDefFoundError) {
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
