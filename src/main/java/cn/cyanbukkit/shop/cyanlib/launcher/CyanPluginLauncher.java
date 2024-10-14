package cn.cyanbukkit.shop.cyanlib.launcher;

import cn.cyanbukkit.shop.CyanShop;
import cn.cyanbukkit.shop.cyanlib.loader.KotlinBootstrap;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 嵌套框架
 */

public class CyanPluginLauncher extends JavaPlugin {

    public static CyanPluginLauncher cyanPlugin;

    public CyanPluginLauncher() {
        cyanPlugin = this;
        KotlinBootstrap.init();
    }



    @Override
    public void onEnable() {
        cyanPlugin.onEnable();
    }

    @Override
    public void onDisable() {
        cyanPlugin.onDisable();
    }



}