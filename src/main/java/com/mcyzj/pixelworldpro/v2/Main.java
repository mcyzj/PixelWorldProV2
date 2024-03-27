package com.mcyzj.pixelworldpro.v2;

import com.mcyzj.lib.plugin.JiangPlugin;
import com.mcyzj.pixelworldpro.v2.core.PixelWorldPro;

public class Main extends JiangPlugin {

    public static Main instance = null;
    public static PixelWorldPro plugin = null;
    @Override
    public void enable() {
        instance = this;
        jiangPlugin = this;
        plugin = new PixelWorldPro();
        plugin.enable();
    }

    @Override
    public void disable() {
        plugin.disable();
    }
}
