package cn.cyanbukkit.copy.menu

import cn.cyanbukkit.copy.CyanShop
import net.minecraft.server.v1_15_R1.Items.it
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
                if (item.type.isAir) continue
                val itemMeta = item.itemMeta
                if (itemMeta?.hasDisplayName() == false)  {
                    itemMeta.setDisplayName(
                        CyanShop.materialConfig.getString(item.type.name) ?: item.type.name
                    )
                }
                item.itemMeta = itemMeta
                val emptyList = mutableListOf<ItemStack>()
                menuConfig.set("Items.$index.ItemStack", item)
                menuConfig.set("Items.$index.Condition", emptyList)
                menuConfig.set("Items.$index.PlayerAmount", -1)
                menuConfig.set("Items.$index.GlobalAmount", -1)
                menuConfig.set("Items.$index.Price.PlayerCMD", emptyList)
                menuConfig.set("Items.$index.Price.ConsoleCMD", emptyList)
                menuConfig.set("Items.$index.Price.Items", emptyList)
                menuConfig.set("Items.$index.Price.Close", false)
                menuConfig.set("Items.$index.Price.Exp", 0)
                menuConfig.set("Items.$index.Price.Money", 0)
                menuConfig.set("Items.$index.Price.Point", 0)
                menuConfig.set("Items.$index.Price.RMB", 0)
                menuConfig.set("Items.$index.Award.PlayerCMD", emptyList)
                menuConfig.set("Items.$index.Award.ConsoleCMD", emptyList)
                menuConfig.set("Items.$index.Award.Items", emptyList)
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
            if (e.currentItem == null || e.currentItem?.type?.isAir ?: return) return
            if (e.clickedInventory == e.whoClicked.inventory) return
            val item = e.slot
            if (e.isLeftClick) {
                val p = e.whoClicked as Player
                val slot = e.slot
                e.isCancelled = true
                p.closeInventory()
                EditorListMenu.init(p, editingMenu[p]!!, "price", slot)
            }
            if (e.isLeftClick && e.isShiftClick) {
                val p = e.whoClicked as Player
                val slot = e.slot
                e.isCancelled = true
                editingAction[p]?.accept(e.inventory)
                editingAction.remove(p)
                editingMenu.remove(p)
                EditorListMenu.init(p, editingMenu[p]!!, "award", slot)
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