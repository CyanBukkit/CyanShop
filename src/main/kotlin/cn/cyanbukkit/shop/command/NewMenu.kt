package cn.cyanbukkit.shop.command

import cn.cyanbukkit.shop.menu.CreateMenu
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