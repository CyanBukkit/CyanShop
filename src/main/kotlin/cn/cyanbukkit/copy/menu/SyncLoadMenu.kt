package cn.cyanbukkit.copy.menu

import cn.cyanbukkit.copy.CyanShop
import net.minecraft.server.v1_15_R1.Items.it
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import java.io.File


object SyncLoadMenu : Runnable {

    private val MenuTimestamp = mutableMapOf<String, Long>()
    private var size: Int = 0

    override fun run() {
        // size 对不上就clear加载
        if (CyanShop.instance.menuFolder.listFiles()?.size != size) {
            CyanShop.allMenu.clear()
            CyanShop.instance.menuFolder.listFiles()?.forEach {
                if (!it.name.endsWith(".yml")) {
                    CyanShop.instance.server.consoleSender.sendMessage("§cshop目录下的${it.name}不是yml文件，请删除谢谢")
                    return
                }
                MenuTimestamp[it.name] = it.lastModified()
                save(it)
            }
            size = CyanShop.instance.menuFolder.listFiles()?.size ?: 0
            Bukkit.getConsoleSender().sendMessage("§a加载菜单完成")
            return
        }
        // 如果文件名为
        CyanShop.instance.menuFolder.listFiles()?.forEach {
            if (!it.name.endsWith(".yml")) {
                CyanShop.instance.server.consoleSender.sendMessage("§cshop目录下的${it.name}不是yml文件，请删除谢谢")
                return
            }

            if (MenuTimestamp[it.name] != it.lastModified()) {
                MenuTimestamp[it.name] = it.lastModified()
                save(it)
                Bukkit.getConsoleSender().sendMessage("§a加载菜单${it}完成")
            }
        }
    }


    private fun String.noYamlSuffix(): String {
        return this.replace(".yml", "")
    }


    private fun save(it: File) {
        val config = YamlConfiguration.loadConfiguration(it)
        val item = mutableMapOf<Int, Item>()
        val configurationSection = config.getConfigurationSection("Items")
        configurationSection?.getKeys(false)?.forEach { key ->
            item[key.toInt()] = Item(
                configurationSection.getItemStack("$key.ItemStack") ?: ItemStack(Material.AIR),
                configurationSection.getStringList("$key.Condition"),
                configurationSection.getInt("$key.PlayerAmount"),
                configurationSection.getInt("$key.GlobalAmount"),
                configurationSection.getStringList("$key.Price.PlayerCMD"),
                configurationSection.getStringList("$key.Price.ConsoleCMD"),
                ItemSerialize.itemStackListDeserialize(
                    configurationSection.get("$key.Price.Items") ?: ""
                ),
                configurationSection.getBoolean("$key.Price.Close"),
                configurationSection.getInt("$key.Price.Exp"),
                configurationSection.getInt("$key.Price.Money"),
                configurationSection.getInt("$key.Price.Point"),
                configurationSection.getInt("$key.Price.RMB"),
                configurationSection.getStringList("$key.Award.PlayerCMD"),
                configurationSection.getStringList("$key.Award.ConsoleCMD"),
                ItemSerialize.itemStackListDeserialize(
                    configurationSection.get("$key.Award.Items") ?: ""
                ),
                configurationSection.getBoolean("$key.Award.Close"),
                configurationSection.getInt("$key.Award.Exp"),
                configurationSection.getInt("$key.Award.Money"),
                configurationSection.getInt("$key.Award.Point"),
                configurationSection.getInt("$key.Award.RMB"),
            )
        }
        CyanShop.allMenu[it.name.noYamlSuffix()] = MenuData(
            config.getString("Title") ?: "",
            config.getInt("Size"),
            item
        )
    }

}