@file:Suppress("SpellCheckingInspection")

package cn.cyanbukkit.shop.command

import cn.cyanbukkit.shop.CyanShop
import cn.cyanbukkit.shop.CyanShop.mainConfig
import cn.cyanbukkit.shop.CyanShop.material
import cn.cyanbukkit.shop.CyanShop.message
import cn.cyanbukkit.shop.CyanShop.translateColor
import cn.cyanbukkit.shop.cyanlib.launcher.CyanPluginLauncher.cyanPlugin
import cn.cyanbukkit.shop.menu.DeleteMenu
import cn.cyanbukkit.shop.menu.EditorMenu
import cn.cyanbukkit.shop.menu.ItemSerialize
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

    override fun execute(p0: CommandSender, p1: String, p2: Array<out String>): Boolean {
        if (p0 !is Player) {
            p0.sendMessage(noPlayer)
            return true
        }
        if (p2.isEmpty()) {
            CyanShop.messageConfig.getStringList("帮助").forEach {
                p0.sendMessage(it)
            }
            return true
        }
        if (p2.size == 1) {
            when (p2[0]) {
                "reload" -> {
                    if (!p0.hasPermission("CyanShop.reload")) {
                        p0.sendMessage(noPerm.replace("<Prem>",
                            "CyanShop.reload"))
                        return true
                    }
                    val time = System.currentTimeMillis()
                    CyanShop.messageConfig = YamlConfiguration.loadConfiguration(message)
                    CyanShop.mainConfigConfig = YamlConfiguration.loadConfiguration(mainConfig)
                    CyanShop.materialConfig = YamlConfiguration.loadConfiguration(material)
                    p0.sendMessage("§a重载成功")
                    p0.sendMessage("§a耗时${System.currentTimeMillis() - time}ms")
                    return true
                }
                "list" -> {
                    if (!p0.hasPermission("CyanShop.list")) {
                        p0.sendMessage(noPerm.replace("<Prem>",
                            "CyanShop.list"))
                        return true
                    }
                    p0.sendMessage("§a当前已有的商店:")
                    CyanShop.allMenu.forEach {
                        // 弄个textcomponent
                        val text = net.md_5.bungee.api.chat.TextComponent(it.key)
                        text.clickEvent = net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/CyanShop open ${it.key}")
                        p0.spigot().sendMessage(text)
                    }
                    return true
                }
                "savetempitem" -> {
                    if (!p0.hasPermission("CyanShop.savetempitem")) {
                        p0.sendMessage(noPerm.replace("<Prem>",
                            "CyanShop.savetempitem"))
                        return true
                    }
                    val item = mutableListOf<ItemStack>()
                    p0.inventory.forEach {
                        if (it != null) {
                            item.add(it)
                        }
                    }
                    CyanShop.mainConfigConfig.set("TempItem", item)
                    CyanShop.mainConfigConfig.save(mainConfig)
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
                "savetempitemsimple" -> {
                    if (!p0.hasPermission("CyanShop.savetempitemsimple")) {
                        p0.sendMessage(noPerm.replace("<Prem>",
                            "CyanShop.savetempitemsimple"))
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
                        CyanShop.mainConfigConfig.save(mainConfig)
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
                "setdisplayname" -> {
                    if (!p0.hasPermission("CyanShop.setdisplayname")) {
                        p0.sendMessage(noPerm.replace("<Prem>",
                            "CyanShop.setdisplayname"))
                        return true
                    }
                    val item = p0.inventory.itemInMainHand
                    val itemMeta = item.itemMeta
                    itemMeta?.setDisplayName(p2[1].translateColor())
                    item.itemMeta = itemMeta
                    p0.sendMessage("§a设置成功")
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
                    OpenMenu(p0, cyanPlugin.server.getPlayer(p2[1]), p2[2]).init()
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
                "editpriceitem" -> {
                    if (!p0.hasPermission("CyanShop.editPriceItem")) {
                        p0.sendMessage(noPerm.replace("<Prem>",
                            "CyanShop.editPriceItem"))
                        return true
                    }
                    Editor(p0, p2[1], p2[2].toInt(), "price").init()
                    return true
                }
                "setlore" -> {
                    // /cs setlore <index> <lore> 如果输入的行数超出手里物品的lore行数则自动设置在新行
                    if (!p0.hasPermission("CyanShop.setLore")) {
                        p0.sendMessage(noPerm.replace("<Prem>",
                            "CyanShop.setLore"))
                        return true
                    }
                    // 如果<index>输入的不是数字 就sendmessage
                    if (!p2[1].isInt()) {
                        p0.sendMessage("§c请输入数字")
                        return true
                    }
                    val item = p0.inventory.itemInMainHand
                    val itemMeta = item.itemMeta
                    val lore = itemMeta?.lore
                    if (lore == null) {
                        itemMeta?.lore = mutableListOf(p2[1])
                    } else {
                        if (p2[1].toInt() > lore.size) {
                            lore.add(p2[2].translateColor())
                        } else {
                            lore[p2[1].toInt()] = p2[2].translateColor()
                        }
                        itemMeta.lore = lore
                    }
                    item.itemMeta = itemMeta
                    p0.sendMessage("§a设置成功")
                }
                "editawarditem" -> {
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


    override fun tabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
        if (!sender.hasPermission("CyanShop.new")) {
            return mutableListOf()
        }
        if (args.isEmpty() || args.size == 1) {
            return mutableListOf(
                "open",
                "edit",
                "npc",
                "new",
                "reload",
                "editpriceitem",
                "editawarditem",
                "savetempitem",
                "remove",
                "setlore",
                "setdisplayname",
                "list",
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
                    return cyanPlugin.server.onlinePlayers.map { it.name }.toMutableList()
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

private fun String.isInt(): Boolean {
    return try {
        this.toInt()
        true
    } catch (e: Exception) {
        false
    }
}
