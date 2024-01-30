package cn.cyanbukkit.copy

import cn.cyanbukkit.copy.command.MainCommand
import cn.cyanbukkit.copy.menu.*
import com.cryptomorin.xseries.XItemStack
import com.cryptomorin.xseries.XMaterial
import me.clip.placeholderapi.PlaceholderAPI
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.SimpleCommandMap
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.io.File


class CyanShop : JavaPlugin() {

    companion object {
        lateinit var instance: CyanShop
        lateinit var messageConfig : YamlConfiguration
        lateinit var mainConfigConfig : YamlConfiguration
        lateinit var materialConfig : YamlConfiguration
        var allMenu = mutableMapOf<String, MenuData>()
        var econ: Economy? = null


        fun org.bukkit.Material.toXMaterial(): XMaterial {
            return XMaterial.matchXMaterial(this) ?: XMaterial.AIR
        }

        fun XMaterial.toMaterial(): org.bukkit.Material {
            return this.parseMaterial() ?: org.bukkit.Material.AIR
        }

//        fun XItemStack.toItemStack(): ItemStack {
//            return
//        }

//        fun ItemStack.toXItemStack(): XItemStack {
//            return XItemStack.
//        }

        fun String.translateColor(): String {
            return this.replace("&", "§")
        }

    }


    val message = File(dataFolder, "Message.yml")
    val mainConfig = File(dataFolder, "config.yml")
    val material = File(dataFolder, "Material.yml")
    val menuFolder = File(dataFolder, "Shop")


    override fun onEnable() {
        instance = this
        server.consoleSender.sendMessage("§b[CyanBukkit™]§f加载CyanShop插件")
        if (!setupEconomy() ) {
            server.consoleSender.sendMessage("§b[CyanBukkit™]&c你没有启用Vault的插件eco功能")
            server.pluginManager.disablePlugin(this);
            return;
        }
        if (!dataFolder.exists()) {
            if (dataFolder.mkdir()) {
                server.consoleSender.sendMessage("§b[CyanBukkit™]§f创建插件文件夹")
            }
        }
        // 加载配置文件message.yml
        if (!message.exists()) {
            server.consoleSender.sendMessage("§b[CyanBukkit™]§f未检测到配置文件，正在创建配置文件")
            saveResource("Message.yml", false)
        }
        messageConfig = YamlConfiguration.loadConfiguration(message)
        if (messageConfig.getString("版本") != description.version) {
            server.consoleSender.sendMessage("§b[CyanBukkit™]§f检测到插件版本更新，正在更新配置文件")
            message.delete()
            saveResource("Message.yml", true)
            messageConfig = YamlConfiguration.loadConfiguration(message)
        }
        // 加载配置文件config.yml
        if (!mainConfig.exists()) {
            server.consoleSender.sendMessage("§b[CyanBukkit™]§f未检测到配置文件，正在创建配置文件")
            saveResource("config.yml", false)
        }
        mainConfigConfig = YamlConfiguration.loadConfiguration(mainConfig)
        // 加载配置文件Material.yml
        if (!material.exists()) {
            server.consoleSender.sendMessage("§b[CyanBukkit™]§f未检测到默认物品名字文件")
            saveResource("Material.yml", false)
        }
        materialConfig = YamlConfiguration.loadConfiguration(material)
        // 加载配置文件GlobalData.yml
        if (!menuFolder.exists()) {
            menuFolder.mkdir()
            server.consoleSender.sendMessage("§b[CyanBukkit™]§f未检测到菜单文件夹，正在创建菜单文件夹")
        }
        // 启动实时加载菜单到Inventery
        server.consoleSender.sendMessage("§b[CyanBukkit™]§f开始启动菜单热加载模块")
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, SyncLoadMenu, 0, 20)
        server.pluginManager.registerEvents(MenuListener, this)
        server.pluginManager.registerEvents(CreateMenu, this)
        server.pluginManager.registerEvents(EditorMenu, this)
        server.pluginManager.registerEvents(EditorListMenu, this)
        // 注册指令
        MainCommand().register()
        server.consoleSender.sendMessage("§b[CyanBukkit™]§fCyanShop插件加载完成")
        server.consoleSender.sendMessage("§b[CyanBukkit™]§f插件作者：瑞鸿网络科技工作室 CyanBukkit （我的世界青桶社区全网联名） ")
    }

    override fun onDisable() {
        // 关闭已经打开界面的用户
        for (player in Bukkit.getOnlinePlayers()) {
            player.closeInventory()
        }
    }
    fun getEconomy(): Economy? {
        return econ
    }
    private fun Command.register() {
        val pluginManagerClazz = instance.server.pluginManager.javaClass
        val field = pluginManagerClazz.getDeclaredField("commandMap")
        field.isAccessible = true
        val commandMap = field.get(instance.server.pluginManager) as SimpleCommandMap
        commandMap.register("CyanShop", this)
    }




    private fun setupEconomy(): Boolean {
        if (server.pluginManager.getPlugin("Vault") == null) {
            return false
        }
        val rsp = server.servicesManager.getRegistration(Economy::class.java) ?: return false
        econ = rsp.provider
        return econ != null
    }

}