package com.mcyzj.pixelworldpro.v2;

import com.mcyzj.lib.Metrics;
import com.mcyzj.lib.bukkit.BukkitPluginManager;
import com.mcyzj.lib.plugin.JiangPlugin;
import com.mcyzj.pixelworldpro.v2.core.PixelWorldPro;
import com.mcyzj.pixelworldpro.v2.core.util.Config;

public class Main extends JiangPlugin {

    public static Main instance = null;
    public static PixelWorldPro plugin = null;
    public static BukkitPluginManager manager = null;
    @Override
    public void enable() {
        instance = this;
        jiangPlugin = this;
        plugin = new PixelWorldPro();
        plugin.enable();
        manager = new BukkitPluginManager(this);
        manager.registerMenuModel();
    }

    @Override
    public void disable() {
        plugin.disable();
    }
}
