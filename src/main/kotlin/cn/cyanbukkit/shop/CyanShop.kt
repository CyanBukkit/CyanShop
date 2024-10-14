package cn.cyanbukkit.shop

import cn.cyanbukkit.shop.command.MainCommand
import cn.cyanbukkit.shop.cyanlib.launcher.CyanPluginLauncher.cyanPlugin
import cn.cyanbukkit.shop.menu.*
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.SimpleCommandMap
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File


object CyanShop {

    lateinit var messageConfig: YamlConfiguration
    lateinit var mainConfigConfig: YamlConfiguration
    lateinit var materialConfig: YamlConfiguration
    var allMenu = mutableMapOf<String, MenuData>()
    var econ: Economy? = null


    fun String.translateColor(): String {
        return this.replace("&", "§")
    }


    val message = File(cyanPlugin.dataFolder, "Message.yml")
    val mainConfig = File(cyanPlugin.dataFolder, "config.yml")
    val material = File(cyanPlugin.dataFolder, "Material.yml")
    val menuFolder = File(cyanPlugin.dataFolder, "Shop")


    fun onEnable() {
        cyanPlugin.server.consoleSender.sendMessage("§b[CyanBukkit™]§f加载CyanShop插件")
        if (!setupEconomy() ) {
            cyanPlugin.server.consoleSender.sendMessage("§b[CyanBukkit™]&c你没有启用Vault的插件eco功能")
            cyanPlugin.server.pluginManager.disablePlugin(cyanPlugin);
            return;
        }
        if (!cyanPlugin.dataFolder.exists()) {
            if (cyanPlugin.dataFolder.mkdir()) {
                cyanPlugin.server.consoleSender.sendMessage("§b[CyanBukkit™]§f创建插件文件夹")
            }
        }
        // 加载配置文件message.yml
        if (!message.exists()) {
            cyanPlugin.server.consoleSender.sendMessage("§b[CyanBukkit™]§f未检测到配置文件，正在创建配置文件")
            cyanPlugin.saveResource("Message.yml", false)
        }
        messageConfig = YamlConfiguration.loadConfiguration(message)
        if (messageConfig.getString("版本") != cyanPlugin.description.version) {
            cyanPlugin.server.consoleSender.sendMessage("§b[CyanBukkit™]§f检测到插件版本更新，正在更新配置文件")
            message.delete()
            cyanPlugin.saveResource("Message.yml", true)
            messageConfig = YamlConfiguration.loadConfiguration(message)
        }
        // 加载配置文件config.yml
        if (!mainConfig.exists()) {
            cyanPlugin.server.consoleSender.sendMessage("§b[CyanBukkit™]§f未检测到配置文件，正在创建配置文件")
            cyanPlugin.saveResource("config.yml", false)
        }
        mainConfigConfig = YamlConfiguration.loadConfiguration(mainConfig)
        // 加载配置文件Material.yml
        if (!material.exists()) {
            cyanPlugin.server.consoleSender.sendMessage("§b[CyanBukkit™]§f未检测到默认物品名字文件")
            cyanPlugin.saveResource("Material.yml", false)
        }
        materialConfig = YamlConfiguration.loadConfiguration(material)
        // 加载配置文件GlobalData.yml
        if (!menuFolder.exists()) {
            menuFolder.mkdir()
            cyanPlugin.server.consoleSender.sendMessage("§b[CyanBukkit™]§f未检测到菜单文件夹，正在创建菜单文件夹")
        }
        // 启动实时加载菜单到Inventery
        cyanPlugin.server.consoleSender.sendMessage("§b[CyanBukkit™]§f开始启动菜单热加载模块")
        Bukkit.getScheduler().runTaskTimerAsynchronously(cyanPlugin, SyncLoadMenu, 0, 20)
        cyanPlugin.server.pluginManager.registerEvents(MenuListener, cyanPlugin)
        cyanPlugin.server.pluginManager.registerEvents(CreateMenu, cyanPlugin)
        cyanPlugin.server.pluginManager.registerEvents(EditorMenu, cyanPlugin)
        cyanPlugin.server.pluginManager.registerEvents(EditorListMenu, cyanPlugin)
        // 注册指令
        MainCommand().register()
        cyanPlugin.server.consoleSender.sendMessage("§b[CyanBukkit™]§fCyanShop插件加载完成")
        cyanPlugin.server.consoleSender.sendMessage("§b[CyanBukkit™]§f插件作者：瑞鸿网络科技工作室 CyanBukkit （我的世界青桶社区全网联名） ")
    }

    fun onDisable() {
        // 关闭已经打开界面的用户
        for (player in Bukkit.getOnlinePlayers()) {
            player.closeInventory()
        }
    }
    fun getEconomy(): Economy? {
        return econ
    }
    private fun Command.register() {
        val pluginManagerClazz = cyanPlugin.server.pluginManager.javaClass
        val field = pluginManagerClazz.getDeclaredField("commandMap")
        field.isAccessible = true
        val commandMap = field.get(cyanPlugin.server.pluginManager) as SimpleCommandMap
        commandMap.register("CyanShop", this)
    }




    private fun setupEconomy(): Boolean {
        if (cyanPlugin.server.pluginManager.getPlugin("Vault") == null) {
            return false
        }
        val rsp = cyanPlugin.server.servicesManager.getRegistration(Economy::class.java) ?: return false
        econ = rsp.provider
        return econ != null
    }

}