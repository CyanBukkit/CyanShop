package cn.cyanbukkit.shop.command

import net.citizensnpcs.api.CitizensAPI
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class NPCBind(
    val sender: CommandSender,
    val npcId: String,
    val menuName: String,
) {


    fun init() {
        if (sender !is Player) {
            sender.sendMessage("§c你必须是一个玩家")
            return
        }
        // 获取citizens 有没有这个npc
        val npc = CitizensAPI.getNPCRegistry().getById(npcId.toInt())
        if (npc == null) {
            sender.sendMessage("§c没有找到这个NPC")
            return
        }
        ///npc cmd add cyanshop open menuName -o
        sender.performCommand("npc sel $npcId")
        sender.performCommand("npc cmd add cyanshop open $menuName -o")
        sender.sendMessage("§a绑定成功 你可以用npc cmd -l查看")
    }


}