package cn.cyanbukkit.copy.menu

import cn.cyanbukkit.copy.CyanShop
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.io.File
import java.util.function.Consumer

/**
 * @Description 利用consume 传inventory
 */
object CreateMenu : Listener {

    private val editingAction: MutableMap<Player, Consumer<Inventory>> = mutableMapOf()


    fun init(menuName: String, size: Int,p : Player) {
        if (CyanShop.instance.menuFolder.listFiles()!!.any { it.name == "$menuName.yml" }) {
            p.sendMessage("§c已存在此菜单")
            return
        }
        val inv= Bukkit.createInventory(null, size * 9, menuName)
        editingAction[p] = Consumer { consumerInv ->
            val now = System.currentTimeMillis()
            Bukkit.getConsoleSender().sendMessage("§a创建菜单....$menuName")
            val menu = File(CyanShop.instance.menuFolder, "$menuName.yml")
            menu.createNewFile()
            val menuConfig = YamlConfiguration.loadConfiguration(menu)
            menuConfig.set("Size", size)
            menuConfig.set("Title", menuName)
            for (index in 0 until consumerInv.size - 1) {
                val item = consumerInv.getItem(index) ?: continue
//                if (item.type.isAir) continue
                val itemMeta = item.itemMeta
                if (itemMeta?.hasDisplayName() == false) {
                    itemMeta.setDisplayName(CyanShop.materialConfig.getString(item.type.name) ?: item.type.name)
                }
                item.itemMeta = itemMeta
                val emptyStringList = mutableListOf<String>()
                val emptyItemStackList = mutableListOf<ItemStack>()
                menuConfig.set("Items.$index.ItemStack", item)
                menuConfig.set("Items.$index.Condition", emptyStringList)
                menuConfig.set("Items.$index.PlayerAmount", -1)
                menuConfig.set("Items.$index.GlobalAmount", -1)
                menuConfig.set("Items.$index.Price.PlayerCMD", emptyStringList)
                menuConfig.set("Items.$index.Price.ConsoleCMD", emptyStringList)
                menuConfig.set("Items.$index.Price.Items", emptyItemStackList)
                menuConfig.set("Items.$index.Price.Close", false)
                menuConfig.set("Items.$index.Price.Exp", 0)
                menuConfig.set("Items.$index.Price.Money", 0)
                menuConfig.set("Items.$index.Price.Point", 0)
                menuConfig.set("Items.$index.Price.RMB", 0)
                menuConfig.set("Items.$index.Award.PlayerCMD", emptyStringList)
                menuConfig.set("Items.$index.Award.ConsoleCMD", emptyStringList)
                menuConfig.set("Items.$index.Award.Items", emptyItemStackList)
                menuConfig.set("Items.$index.Award.Close", false)
                menuConfig.set("Items.$index.Award.Exp", 0)
                menuConfig.set("Items.$index.Award.Money", 0)
                menuConfig.set("Items.$index.Award.Point", 0)
                menuConfig.set("Items.$index.Award.RMB", 0)
            }
            menuConfig.save(menu)
            p.sendMessage("§a创建成功 耗时${System.currentTimeMillis() - now}ms")
        }
        p.openInventory(inv)
    }
    @EventHandler
    fun onInventoryClick(e: InventoryCloseEvent) {
        if (editingAction.containsKey(e.player)) {
            editingAction[e.player]?.accept(e.inventory)
            editingAction.remove(e.player)
        }
    }
}