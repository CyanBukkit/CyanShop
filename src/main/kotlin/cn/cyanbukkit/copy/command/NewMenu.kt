package cn.cyanbukkit.copy.command

import cn.cyanbukkit.copy.menu.CreateMenu
import cn.cyanbukkit.copy.menu.CreateMenu.init
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class NewMenu(
    val sender: CommandSender,
    val menuName: String,
    val size: Int,
) {

    fun init() {
        if (sender !is Player) {
            sender.sendMessage("§c控制台无法使用此命令")
            return
        }
        CreateMenu.init(menuName,size,sender as Player)
    }

}