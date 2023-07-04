package cn.cyanbukkit.copy.menu

import cn.cyanbukkit.copy.CyanShop
import org.bukkit.entity.Player
import java.io.File

object DeleteMenu {


    fun init(menuName : String, p : Player) {
        val menu = File(CyanShop.instance.menuFolder, "$menuName.yml")
        if (!menu.exists()) {
            p.sendMessage("§c不存在此菜单")
            return
        }
        menu.delete()
        p.sendMessage("§a删除成功")
    }

}