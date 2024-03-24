package com.mcyzj.lib.plugin;

import com.mcyzj.lib.bukkit.menu.MenuListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class JiangPlugin extends JavaPlugin {
    public static JiangPlugin jiangPlugin = null;
    @Override
    public void onEnable() {
        JiangLib.loadLibs();
        Bukkit.getPluginManager().registerEvents(new MenuListener(), this);
        enable();
    }

    public void enable() {}

    @Override
    public void onDisable() {
        disable();
    }

    public void disable() {}
}
