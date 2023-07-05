package cn.cyanbukkit.copy.menu

import cn.cyanbukkit.copy.CyanShop
import net.citizensnpcs.api.gui.InventoryMenu
import net.minecraft.server.v1_15_R1.Items
import net.minecraft.server.v1_15_R1.Items.it
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.io.File
import java.util.function.Consumer


object EditorListMenu : Listener {
    private val editingAction: MutableMap<Player, Consumer<Inventory>> = mutableMapOf()

    fun init(p: Player, menuName: String, mode: String, id: Int) {
        val menu = File(CyanShop.instance.menuFolder, "$menuName.yml")
        if (!menu.exists()) {
            p.sendMessage("§c不存在此菜单")
            return
        }
        val menuConfig = YamlConfiguration.loadConfiguration(menu)
        if (!menuConfig.contains("Items.$id")) {
            p.sendMessage("§c此菜单没有物品")
            return
        }
        val aInv = Bukkit.createInventory(null, 54, "§a编辑菜单...$menuName 的$mode")
        p.openInventory(aInv)
        when (mode) {
            "award" -> {
                menuConfig.get("Items.$id.Award.Items")?.let { it1 ->
                    ItemSerialize.itemStackListDeserialize(it1).forEach {
                        aInv.addItem(it)
                    }
                }
                editingAction[p] = Consumer { inv ->
                    val now = System.currentTimeMillis()
                    Bukkit.getConsoleSender().sendMessage("§a${p} 编辑菜单....$menuName 的award物品列表")
                    val emptyList = mutableListOf<ItemStack>()
                    for (it in inv) {
                        if (it != null) {
                            val itemMeta = it.itemMeta
                            if (itemMeta?.hasDisplayName() == false) {
                                itemMeta.setDisplayName(
                                    CyanShop.materialConfig.getString(it.type.name) ?: it.type.name
                                )
                            }
                            it.itemMeta = itemMeta
                            emptyList.add(it)
                        }
                    }
                    menuConfig.set("Items.$id.Award.Items", emptyList)
                    menuConfig.save(menu)
                    p.sendMessage("§a修改成功...耗时${System.currentTimeMillis() - now}ms")
                }

            }

            "price" -> {
                menuConfig.get("Items.$id.Price.Items")?.let { it1 ->
                    ItemSerialize.itemStackListDeserialize(it1).forEach {
                        aInv.addItem(it)
                    }
                }
                editingAction[p] = Consumer { inv ->
                    val now = System.currentTimeMillis()
                    Bukkit.getConsoleSender().sendMessage("§a${p} 编辑菜单....$menuName 的Price物品列表")
                    val emptyList = mutableListOf<ItemStack>()
                    inv.forEach { it ->
                        if (it != null) {
                            if (it.type.isAir) return@forEach
                            val itemMeta = it.itemMeta
                            if (itemMeta?.hasDisplayName() == false) {
                                itemMeta.setDisplayName(
                                    CyanShop.materialConfig.getString(it.type.name) ?: it.type.name
                                )
                            }
                            it.itemMeta = itemMeta
                            emptyList.add(it)
                        }
                    }
                    menuConfig.set("Items.$id.Price.Items", emptyList)
                    menuConfig.save(menu)
                    p.sendMessage("§a修改成功...耗时${System.currentTimeMillis() - now}ms")
                }
            }


        }

        @EventHandler
        fun onMenuClick(e: InventoryCloseEvent) {
            if (editingAction.containsKey(e.player)) {
                editingAction[e.player]?.accept(e.inventory)
                editingAction.remove(e.player)
            }
        }

    }
}