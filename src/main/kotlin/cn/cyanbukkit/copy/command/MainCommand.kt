package cn.cyanbukkit.copy.command

import cn.cyanbukkit.copy.CyanShop
import cn.cyanbukkit.copy.menu.DeleteMenu
import cn.cyanbukkit.copy.menu.EditorMenu
import cn.cyanbukkit.copy.menu.ItemSerialize
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class MainCommand : Command(
    "CyanShop",
    "CyanShop",
    "/CyanShop",
    listOf("cs","AShop"),
) {

    private var noPerm = CyanShop.messageConfig.getString("无权限")!!
    private var noPlayer = CyanShop.messageConfig.getString("非玩家")!!

    override fun execute(p0: CommandSender, p1: String, p2: Array<out String>?): Boolean {
        if (p0 !is Player) {
            p0.sendMessage(noPlayer)
            return true
        }
        if (p2 == null || p2.isEmpty()) {
            CyanShop.messageConfig.getStringList("帮助").forEach {
                p0.sendMessage(it)
            }
            return true
        }
        if (p2.size == 1) {
            when (p2[0]) {
                "Reload" -> {
                    if (!p0.hasPermission("CyanShop.reload")) {
                        p0.sendMessage(noPerm.replace("<Prem>",
                            "CyanShop.reload"))
                        return true
                    }
                    val time = System.currentTimeMillis()
                    CyanShop.messageConfig = YamlConfiguration.loadConfiguration(CyanShop.instance.message)
                    CyanShop.npcDataConfig = YamlConfiguration.loadConfiguration(CyanShop.instance.npcData)
                    CyanShop.mainConfigConfig = YamlConfiguration.loadConfiguration(CyanShop.instance.mainConfig)
                    CyanShop.materialConfig = YamlConfiguration.loadConfiguration(CyanShop.instance.material)
                    p0.sendMessage("§a重载成功")
                    p0.sendMessage("§a耗时${System.currentTimeMillis() - time}ms")
                    return true
                }
                "saveTempItem" -> {
                    if (!p0.hasPermission("CyanShop.saveTempItem")) {
                        p0.sendMessage(noPerm.replace("<Prem>",
                            "CyanShop.saveTempItem"))
                        return true
                    }
                    val item = mutableListOf<ItemStack>()
                    p0.inventory.forEach {
                        if (it != null) {
                            item.add(it)
                        }
                    }
                    CyanShop.mainConfigConfig.set("TempItem", item)
                    CyanShop.mainConfigConfig.save(CyanShop.instance.mainConfig)
                    p0.sendMessage("§a保存成功")
                }
                "test" -> {
                    if (!p0.hasPermission("CyanShop.test")) {
                        p0.sendMessage(noPerm.replace("<Prem>",
                            "CyanShop.test"))
                        return true
                    }
                    CyanShop.mainConfigConfig.get("TempItem")?.let {
                        ItemSerialize.itemStackListDeserialize(it).forEach {
                            p0.inventory.addItem(it)
                        }
                    }
                    p0.sendMessage("§a执行测试")
                }
                "saveTempItemSimple" -> {
                    if (!p0.hasPermission("CyanShop.test")) {
                        p0.sendMessage(noPerm.replace("<Prem>",
                            "CyanShop.test"))
                        return true
                    }
                    val item = p0.inventory.itemInMainHand
                    if (!item.type.isAir) {
                        val itemMeta = item.itemMeta
                        if (itemMeta?.hasDisplayName() == false)  {
                            itemMeta.setDisplayName(
                                CyanShop.materialConfig.getString(item.type.name) ?: item.type.name
                            )
                        }
                        item.itemMeta = itemMeta
                        CyanShop.mainConfigConfig.set("TempItem", item)
                        CyanShop.mainConfigConfig.save(CyanShop.instance.mainConfig)
                        p0.sendMessage("§a保存成功")
                    } else {
                        p0.sendMessage("§c手上没有物品")
                    }
                    
                }
            }
        }
        if (p2.size == 2) {
            when (p2[0]) {
                "open" -> {
                    OpenMenu(p0,null, p2[1]).init()
                    return true
                }
                "edit" -> {
                    if (!p0.hasPermission("CyanShop.edit")) {
                        p0.sendMessage(noPerm.replace("<Prem>",
                            "CyanShop.edit"))
                        return true
                    }
                    EditorMenu.init(p0 as Player, p2[1])
                    return true
                }
                else -> {
                    CyanShop.messageConfig.getStringList("帮助").forEach {
                        p0.sendMessage(it)
                    }
                    return true
                }
            }
        }
        if (p2.size == 3) {
            when (p2[0]) {
                "open" -> {
                    if (!p0.hasPermission("CyanShop.open")) {
                        p0.sendMessage(noPerm.replace("<Prem>",
                            "CyanShop.open"))
                        return true
                    }
                    OpenMenu(p0, CyanShop.instance.server.getPlayer(p2[1]), p2[2]).init()
                    return true
                }
                "npc" -> {
                    if (!p0.hasPermission("CyanShop.npc")) {
                        p0.sendMessage(noPerm.replace("<Prem>",
                            "CyanShop.npc"))
                        return true
                    }
                    NPCBind(p0, p2[1], p2[2]).init()
                    return true
                }
                "new" -> {
                    if (!p0.hasPermission("CyanShop.new")) {
                        p0.sendMessage(noPerm.replace("<Prem>",
                            "CyanShop.new"))
                        return true
                    }
                    NewMenu(p0, p2[1], p2[2].toInt()).init()
                    return true
                }
                "remove" -> {
                    if (!p0.hasPermission("CyanShop.remove")) {
                        p0.sendMessage(noPerm.replace("<Prem>",
                            "CyanShop.remove"))
                        return true
                    }
                    DeleteMenu.init(p2[1], p0)
                    return true
                }
                "editPriceItem" -> {
                    if (!p0.hasPermission("CyanShop.editPriceItem")) {
                        p0.sendMessage(noPerm.replace("<Prem>",
                            "CyanShop.editPriceItem"))
                        return true
                    }
                    Editor(p0, p2[1], p2[2].toInt(), "price").init()
                    return true
                }
                "editAwardItem" -> {
                    if (!p0.hasPermission("CyanShop.editAwardItem")) {
                        p0.sendMessage(noPerm.replace("<Prem>",
                            "CyanShop.editAwardItem"))
                        return true
                    }
                    Editor(p0, p2[1], p2[2].toInt(), "award").init()
                    return true
                }
                else -> {
                    CyanShop.messageConfig.getStringList("帮助").forEach {
                        p0.sendMessage(it)
                    }
                    return true
                }
            }
        }

        return true
    }


    override fun tabComplete(sender: CommandSender, alias: String, args: Array<out String>?): MutableList<String> {
        if (!sender.hasPermission("CyanShop.new")) {
            return mutableListOf()
        }
        if (args == null || args.isEmpty() || args.size == 1) {
            return mutableListOf(
                "open",
                "edit",
                "npc",
                "new",
                "Reload",
                "editPriceItem",
                "editAwardItem",
                "saveTempItem",
                "remove",
            )
        }
        if (args.size == 2) {
            when (args[0]) {
                "open" -> {
                    return CyanShop.allMenu.keys.toMutableList()
                }
                "edit" -> {
                    return CyanShop.allMenu.keys.toMutableList()
                }
                "editAwardItem" -> {
                    return CyanShop.allMenu.keys.toMutableList()
                }
                "editPriceItem" -> {
                    return CyanShop.allMenu.keys.toMutableList()
                }
            }
        }
        if (args.size == 3) {
            when (args[0]) {
                "open" -> {
                    return CyanShop.instance.server.onlinePlayers.map { it.name }.toMutableList()
                }
                "npc" -> {
                    return CyanShop.allMenu.keys.toMutableList()
                }
                "new" -> {
                    return mutableListOf("1","2","3","4","5","6")
                }
            }
        }
        return mutableListOf()
    }
}