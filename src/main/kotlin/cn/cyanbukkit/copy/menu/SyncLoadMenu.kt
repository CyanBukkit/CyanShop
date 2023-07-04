package cn.cyanbukkit.copy.menu

import cn.cyanbukkit.copy.CyanShop
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack

object SyncLoadMenu : Runnable {
    override fun run() {
        CyanShop.allMenu.clear()
        CyanShop.instance.menuFolder.listFiles()?.forEach {
            if (!it.name.endsWith(".yml")) {
                CyanShop.instance.server.consoleSender.sendMessage("§c${it.name}不是yml文件，请删除谢谢")
                return
            }
            val config = YamlConfiguration.loadConfiguration(it)
            val item = mutableMapOf<Int, Item>()
            val configurationSection = config.getConfigurationSection("Items")
            configurationSection?.getKeys(false)?.forEach { key ->
                item[key.toInt()] = Item(
                    configurationSection.getItemStack("$key.ItemStack")?: ItemStack(Material.AIR),
                    configurationSection.getStringList("$key.Condition"),
                    configurationSection.getInt("$key.PlayerAmount"),
                    configurationSection.getInt("$key.GlobalAmount"),
                    configurationSection.getStringList("$key.Price.PlayerCMD"),
                    configurationSection.getStringList("$key.Price.ConsoleCMD"),
                    ItemSerialize.itemStackListDeserialize(
                        configurationSection.get("$key.Price.Items")?: ""
                    ),
                    configurationSection.getBoolean("$key.Price.Close"),
                    configurationSection.getInt("$key.Price.Exp"),
                    configurationSection.getInt("$key.Price.Money"),
                    configurationSection.getInt("$key.Price.Point"),
                    configurationSection.getInt("$key.Price.RMB"),
                    configurationSection.getStringList("$key.Award.PlayerCMD"),
                    configurationSection.getStringList("$key.Award.ConsoleCMD"),
                    ItemSerialize.itemStackListDeserialize(
                        configurationSection.get("$key.Award.Items")?: ""
                    ),
                    configurationSection.getBoolean("$key.Award.Close"),
                    configurationSection.getInt("$key.Award.Exp"),
                    configurationSection.getInt("$key.Award.Money"),
                    configurationSection.getInt("$key.Award.Point"),
                    configurationSection.getInt("$key.Award.RMB"),
                )
            }
            CyanShop.allMenu[it.name.noYamlSuffix()] = MenuData(
                config.getString("Title")?:"",
                config.getInt("Size"),
                item)
        }
    }


    private fun String.noYamlSuffix() : String{
        return this.replace(".yml", "")
    }

}