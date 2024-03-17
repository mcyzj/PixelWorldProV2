package com.mcyzj.lib.plugin;

import org.bukkit.plugin.java.JavaPlugin;

public class JiangPlugin extends JavaPlugin {
    public static JiangPlugin jiangPlugin = null;
    @Override
    public void onEnable() {
        JiangLib.loadLibs();
        enable();
    }

    public void enable() {}

    @Override
    public void onDisable() {
        disable();
    }

    public void disable() {}
}
