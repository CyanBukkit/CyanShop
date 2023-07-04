package cn.cyanbukkit.copy.command

import cn.cyanbukkit.copy.CyanShop
import cn.cyanbukkit.copy.menu.MenuListener
import net.minecraft.server.v1_15_R1.Items.it
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player

class NPCBind(
    val sender: CommandSender,
    val npcId: String,
    val menuName: String,
) {


    fun init() {
        CyanShop.npcDataConfig.set("NPC.${npcId}.menu", menuName)
        CyanShop.npcDataConfig.save(CyanShop.instance.npcData)
        CyanShop.npcDataConfig = YamlConfiguration.loadConfiguration(CyanShop.instance.npcData)
        sender.sendMessage("§a绑定成功")
    }


}