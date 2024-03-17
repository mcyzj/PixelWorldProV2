package com.mcyzj.pixelworldpro.v2.core;

import com.mcyzj.lib.plugin.JiangLib;
import com.mcyzj.lib.plugin.JiangPlugin;

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
