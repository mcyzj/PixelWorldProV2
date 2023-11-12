package com.mcyzj.pixelworldpro.api

import com.mcyzj.pixelworldpro.PixelWorldPro

abstract class Expansion protected constructor() {
    val logger = PixelWorldPro.instance.logger

    abstract fun onEnable()

    abstract fun onDisable()
}