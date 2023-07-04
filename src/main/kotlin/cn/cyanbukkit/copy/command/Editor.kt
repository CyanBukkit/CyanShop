package cn.cyanbukkit.copy.command

import cn.cyanbukkit.copy.CyanShop
import cn.cyanbukkit.copy.menu.EditorListMenu
import org.bukkit.entity.Player

class Editor(
    val sender: Player,
    val menuName: String,
    val itemID: Int,
    val mode: String,
) {

    fun init() {
        if (!CyanShop.allMenu.containsKey(menuName)) {
            sender.sendMessage("§c菜单不存在")
            return
        }
        EditorListMenu.init(sender, menuName,  mode, itemID)
    }


}