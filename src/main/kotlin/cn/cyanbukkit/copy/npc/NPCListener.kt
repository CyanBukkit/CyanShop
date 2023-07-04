package cn.cyanbukkit.copy.npc

import cn.cyanbukkit.copy.CyanShop
import cn.cyanbukkit.copy.command.OpenMenu
import net.citizensnpcs.api.event.NPCClickEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object NPCListener : Listener {


    @EventHandler
    fun onNPCClick(e: NPCClickEvent) {
        e.isCancelled = true
        val npc = e.npc
        val p = e.clicker
        val configSection = CyanShop.npcDataConfig.getConfigurationSection("NPC")
        if (configSection!!.contains(npc.id.toString())) {
            val npcData = configSection.getString(npc.id.toString())
            OpenMenu(p, null, npcData!!)
        }
    }

}