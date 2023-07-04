package cn.cyanbukkit.copy

import cn.cyanbukkit.copy.command.MainCommand
import cn.cyanbukkit.copy.menu.*
import cn.cyanbukkit.copy.npc.NPCListener
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.SimpleCommandMap
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File


class CyanShop : JavaPlugin() {

    companion object {
        lateinit var instance: CyanShop
        lateinit var messageConfig : YamlConfiguration
        lateinit var mainConfigConfig : YamlConfiguration
        lateinit var materialConfig : YamlConfiguration
        lateinit var npcDataConfig : YamlConfiguration
        var allMenu = mutableMapOf<String, MenuData>()
        var econ: Economy? = null
    }


    val message = File(dataFolder, "Message.yml")
    val mainConfig = File(dataFolder, "config.yml")
    val material = File(dataFolder, "Material.yml")
    val npcData = File(dataFolder, "GlobalData.yml")
    val menuFolder = File(dataFolder, "Shop")


    override fun onEnable() {
        instance = this
        if (!setupEconomy() ) {
            logger.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (!dataFolder.exists()) {
            dataFolder.mkdir()
        }
        // 加载配置文件message.yml
        if (!message.exists()) {
            saveResource("Message.yml", false)
        }
        messageConfig = YamlConfiguration.loadConfiguration(message)
        // 加载配置文件config.yml
        if (!mainConfig.exists()) {
            saveResource("config.yml", false)
        }
        mainConfigConfig = YamlConfiguration.loadConfiguration(mainConfig)
        // 加载配置文件Material.yml
        if (!material.exists()) {
            saveResource("Material.yml", false)
        }
        materialConfig = YamlConfiguration.loadConfiguration(material)
        // 加载配置文件GlobalData.yml
        if (!npcData.exists()) {
            saveResource("GlobalData.yml", false)
        }
        npcDataConfig = YamlConfiguration.loadConfiguration(npcData)
        if (!menuFolder.exists()) {
            menuFolder.mkdir()
        }
        // 启动实时加载菜单到Inventery
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, SyncLoadMenu, 0, 20)
        server.pluginManager.registerEvents(MenuListener, this)
        server.pluginManager.registerEvents(NPCListener, this)
        server.pluginManager.registerEvents(CreateMenu, this)
        server.pluginManager.registerEvents(EditorMenu, this)
        server.pluginManager.registerEvents(EditorListMenu, this)
        // 注册指令
        MainCommand().register()
    }

    override fun onDisable() {
        logger.info("CyanShop disabled")
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