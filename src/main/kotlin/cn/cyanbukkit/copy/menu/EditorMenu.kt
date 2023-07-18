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


object EditorMenu : Listener {
    private val editingAction: MutableMap<Player, Consumer<Inventory>> = mutableMapOf()
    private val editingMenu: MutableMap<Player, String> = mutableMapOf()

    fun init(p: Player, menuName: String) {
        val menu = File(CyanShop.instance.menuFolder, "$menuName.yml")
        if (!menu.exists()) {
            p.sendMessage("§c不存在此菜单")
            return
        }
        editingMenu[p] = menuName
        edit(p, CyanShop.allMenu[menuName]!!)
        editingAction[p] = Consumer { inv ->
            val now = System.currentTimeMillis()
            Bukkit.getConsoleSender().sendMessage("§a编辑菜单....$menuName")
            val menuConfig = YamlConfiguration.loadConfiguration(menu)
            menuConfig.set("Items", null)
            for (index in 0 until inv.size) {
                val item = inv.getItem(index) ?: continue
//                if (item.type.isAir) continue
                val itemMeta = item.itemMeta
                if (itemMeta?.hasDisplayName() == false)  {
                    itemMeta.setDisplayName(
                        CyanShop.materialConfig.getString(item.type.name) ?: item.type.name
                    )
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
            p.sendMessage("§a修改成功...耗时${System.currentTimeMillis() - now}ms")
        }
    }


    fun edit(p: Player, menu: MenuData) {
        val newInventory = Bukkit.createInventory(null, menu.size * 9, menu.title)
        menu.items.forEach {
            newInventory.setItem(it.key, it.value.itemStack)
        }
        p.openInventory(newInventory)
    }

    @EventHandler
    fun onLeft(e: InventoryClickEvent) {
        if (editingAction.containsKey(e.whoClicked as Player)) {
            if (e.currentItem == null ) return
            if (e.clickedInventory == e.whoClicked.inventory) return
            val slot = e.slot
            if (e.isLeftClick && e.isShiftClick) {
                e.isCancelled = true
                val p = e.whoClicked as Player
                editingAction[p]?.accept(e.inventory)
                EditorListMenu.init(p, editingMenu[p]!!, "award", slot)
                editingAction.remove(p)
                editingMenu.remove(p)
                return
            }
            if (e.isLeftClick) {
                e.isCancelled = true
                val p = e.whoClicked as Player
                editingAction[p]?.accept(e.inventory)
                EditorListMenu.init(p, editingMenu[p]!!, "price", slot)
                editingAction.remove(p)
                editingMenu.remove(p)
            }
        }
    }



    @EventHandler
    fun onMenuClick(e: InventoryCloseEvent) {
        if (editingAction.containsKey(e.player)) {
            editingAction[e.player]?.accept(e.inventory)
            editingAction.remove(e.player)
            editingMenu.remove(e.player)
        }
    }

}