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
        if (!setupEconomy() ) {
            logger.severe(String.format("[%s] - Disabled due to no Vault dependency found!", description.name));
            server.pluginManager.disablePlugin(this);
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
        if (messageConfig.getString("版本") != description.version) {
            logger.warning("检测到插件版本更新，正在更新配置文件")
            message.delete()
            saveResource("Message.yml", true)
            messageConfig = YamlConfiguration.loadConfiguration(message)
        }
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
        if (!menuFolder.exists()) {
            menuFolder.mkdir()
        }
        // 启动实时加载菜单到Inventery
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, SyncLoadMenu, 0, 20)
        server.pluginManager.registerEvents(MenuListener, this)
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