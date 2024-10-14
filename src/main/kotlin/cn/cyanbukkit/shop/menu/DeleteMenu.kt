package cn.cyanbukkit.shop.menu

import cn.cyanbukkit.shop.CyanShop
import cn.cyanbukkit.shop.CyanShop.menuFolder
import org.bukkit.entity.Player
import java.io.File

object DeleteMenu {


    fun init(menuName : String, p : Player) {
        val menu = File(menuFolder, "$menuName.yml")
        if (!menu.exists()) {
            p.sendMessage("§c不存在此菜单")
            return
        }
        menu.delete()
        p.sendMessage("§a删除成功")
    }

}