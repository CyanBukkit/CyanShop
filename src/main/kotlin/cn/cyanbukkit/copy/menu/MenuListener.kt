package cn.cyanbukkit.copy.menu

import cn.cyanbukkit.copy.CyanShop
import cn.cyanbukkit.copy.menu.MenuListener.Papi
import cn.cyanbukkit.copy.menu.MenuListener.check
import cn.cyanbukkit.copy.menu.MenuListener.isTrue
import cn.cyanbukkit.copy.menu.MenuListener.tran
import me.clip.placeholderapi.PlaceholderAPI
import net.minecraft.server.v1_15_R1.Items.im
import net.minecraft.server.v1_15_R1.Items.it
import org.black_ixx.playerpoints.PlayerPoints
import org.black_ixx.playerpoints.PlayerPointsAPI
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.scheduler.BukkitRunnable


object MenuListener : Listener {

    val openingMenuMap = mutableMapOf<Player, openingMenu>()


    fun changeMenu(p: Player, menu: MenuData) {
        val newInventory = Bukkit.createInventory(null, menu.size * 9, menu.title)
        val taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(CyanShop.instance, Runnable {
            newInventory.clear()
            for (it in menu.items) {
                val item = it.value.itemStack
                if (item.type.isAir) continue
                val im = item.itemMeta
                im?.setDisplayName(PlaceholderAPI.setPlaceholders(p, im.displayName))
                val lore = if (im?.hasLore() == true) {
                    PlaceholderAPI.setPlaceholders(p, im.lore!!.tran() )
                } else {
                    mutableListOf()
                }
                lore.add(p.Papi("§5条件:"))
                it.value.condition.forEach {
                    try {
                        lore.add(p.Papi(it.check()))
                    } catch (e: Exception) {
                        lore.add(p.Papi("§c${it} §4§l[条件错误]"))
                    }
                }
                lore.add(p.Papi("§5花费:"))
                if (p.exp >= it.value.priceExp) {
                    lore.add(p.Papi("§7➥ §a§l√ §a经验: §e${it.value.priceExp}"))
                } else {
                    lore.add(p.Papi("§7➥ §c§l✘ §c经验: §e${it.value.priceExp}"))
                }
                if (CyanShop.econ!!.getBalance(p) >= it.value.priceMoney) {
                    lore.add(p.Papi("§7➥ §a§l√ §a金币: §e${it.value.priceMoney}"))
                } else {
                    lore.add(p.Papi("§7➥ §c§l✘ §c金币: §e${it.value.priceMoney}"))
                }
                if(PlayerPointsAPI(PlayerPoints.getInstance()).look(p.uniqueId) >= it.value.pricePoint) {
                    lore.add(p.Papi("§7➥ §a§l√ §a点券: §e${it.value.pricePoint}"))
                } else {
                    lore.add(p.Papi("§7➥ §c§l✘ §c点券: §e${it.value.pricePoint}"))
                }

                it.value.priceItems.forEach { thing ->
                    try {
                        if (p.inventory.contains(thing.type) && p.inventory.getItem(p.inventory.first(thing.type))!!.amount >= thing.amount) {
                            lore.add(p.Papi("§7➥ §a§l√ §a${thing.itemMeta!!.displayName} x${thing.amount}"))
                        } else {
                            lore.add(p.Papi("§7➥ §c§l✘ §c${thing.itemMeta!!.displayName} x${thing.amount}"))
                        }
                    } catch (e: Exception) {
                        lore.add(p.Papi("§7➥ §c§l✘ §c${thing.itemMeta!!.displayName} x${thing.amount}"))
                    }
                }
                im?.lore = lore
                item.itemMeta = im
                newInventory.setItem(it.key, item)
            }
        }, 0, 1000).taskId
        p.openMenu(newInventory)
        openingMenuMap[p] = openingMenu(menu, taskId)
    }

    /**
     * %objective_scorep_Halloween%||>=||128||§a合成 §e128个 §d南瓜灯 §7(当 %objective_scorep_Halloween%
     *       前)
     */
    private fun String.check(): String {
        if (this == "") return ""
        val x = this.split("||")
        when (x[1]) {
            ">=" -> {
                return if (x[0].toInt() >= x[2].toInt()) {
                    "§a§l√" + x[3]
                } else {
                    "§c§l✘" + x[3]
                }
            }
            "==" -> {
                return if (x[0] == x[2]) {
                    "§a§l√" + x[3]
                } else {
                    "§c§l✘" + x[3]
                }
            }
            "!=" -> {
                return if (x[0] != x[2]) {
                    "§a§l√" + x[3]
                } else {
                    "§c§l✘" + x[3]
                }
            }
            "<=" -> {
                return if (x[0].toInt() <= x[2].toInt()) {
                    "§a§l√" + x[3]
                } else {
                    "§c§l✘" + x[3]
                }
            }
            ">" -> {
                return if (x[0].toInt() > x[2].toInt()) {
                    "§a§l√" + x[3]
                } else {
                    "§c§l✘" + x[3]
                }
            }
            "<" -> {
                return if (x[0].toInt() < x[2].toInt()) {
                    "§a§l√" + x[3]
                } else {
                    "§c§l✘" + x[3]
                }
            }
            else -> {
                return "§a§l未知判定格式" + x[3]
            }
        }
    }

    private fun String.isTrue(): Boolean {
        if (this == "") return false
        val x = this.split("||")
        when (x[1]) {
            ">=" -> {
                return x[0].toInt() >= x[2].toInt()
            }
            "==" -> {
                return x[0] == x[2]
            }
            "!=" -> {
                return x[0] != x[2]
            }
            "<=" -> {
                return x[0].toInt() <= x[2].toInt()
            }
            ">" -> {
                return x[0].toInt() > x[2].toInt()
            }
            "<" -> {
                return x[0].toInt() < x[2].toInt()
            }
        }
        return false
    }


    private fun List<String>.tran(): List<String> {
        val list = mutableListOf<String>()
        this.forEach {
            list.add(it.replace("§", "§"))
        }
        return list
    }

    private fun Player.Papi(s: String): String {
        return PlaceholderAPI.setPlaceholders(this, s)
    }


    private fun Player.openMenu(menu: Inventory) {
        val p = this
        // 创建异步操作
        object : BukkitRunnable() {
            override fun run() {
                //  这里直接使用调度器同步打开这个gui即可
                Bukkit.getScheduler().runTask(CyanShop.instance, Runnable {
                    p.openInventory(menu)
                })
            }
        }.runTaskAsynchronously(CyanShop.instance)
    }


    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        if (e.whoClicked !is Player) return
        val p = e.whoClicked as Player
        if (!openingMenuMap.containsKey(p)) return
        val menu = openingMenuMap[p]!!
        // 点击判定执行指令
        if (e.currentItem == null) return
        if (!menu.menuData.items.containsKey(e.slot)) return
        e.isCancelled = true
        val item = menu.menuData.items[e.slot]!!
        p.price(item)
    }

    /**
     * 点击判定 包括兑换后的事件都在这
     */
    private fun Player.price(item: Item) {
        val p = this
        //
        // exp
        item.condition.forEach {
            if (it == "") return@forEach
            try {
                if (!it.isTrue()) {
                    p.sendMessage("§c§l你的${it.split("||")[3]}不满足")
                    return
                }
            } catch (e: Exception) {
                p.sendMessage("§c§l条件不满足")
                return
            }
        }
        if (p.level < item.priceExp) {
            p.sendMessage("§c§l你的经验不足")
            return
        }
        if (CyanShop.econ!!.getBalance(p) < item.priceMoney) {
            p.sendMessage("§c§l你的金币不足")
            return
        }
        if (PlayerPointsAPI(PlayerPoints.getInstance()).look(p.uniqueId) < item.pricePoint) {
            p.sendMessage("§c§l你的点券不足")
            return
        }
        item.priceItems.forEach {
            if (!p.inventory.contains(it.type)) {
                p.sendMessage("§c§l你的物品不足")
                return
            }
            if (p.inventory.getItem(p.inventory.first(it.type))!!.amount < it.amount) {
                p.sendMessage("§c§l你的物品不足")
                return
            }
        }
        CyanShop.econ!!.withdrawPlayer(p, item.priceMoney.toDouble())
        PlayerPointsAPI(PlayerPoints.getInstance()).take(p.uniqueId, item.pricePoint)
        item.priceItems.forEach {
            p.inventory.removeItem(ItemStack(it.type, it.amount))
        }
        // exp take
        p.level = p.level - item.priceExp
        // Award
        item.awardItems.forEach {
            p.inventory.addItem(it)
        }
        item.awardPlayerCMD.forEach {
            Bukkit.dispatchCommand(p, p.Papi(it))
        }
        item.awardGlobalCMD.forEach {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), p.Papi(it))
        }
        if (item.awardExp != 0) {
            p.giveExp(item.awardExp)
        }
        if (item.awardMoney != 0) {
            CyanShop.econ!!.depositPlayer(p, item.awardMoney.toDouble())
        }
        if (item.awardPoint != 0) {
            PlayerPointsAPI(PlayerPoints.getInstance()).give(p.uniqueId, item.awardPoint)
        }
        if(item.awardClose){
            p.closeInventory()
        }
    }

    // 关闭后结束线程
    @EventHandler
    fun onClose(e: InventoryCloseEvent) {
        if (e.player !is Player) return
        val p = e.player as Player
        if (!openingMenuMap.containsKey(p)) return
        val menu = openingMenuMap[p]!!
        if (menu.task != -1) {
            Bukkit.getScheduler().cancelTask(menu.task)
        }
        openingMenuMap.remove(p)
    }


    data class openingMenu(
        val menuData: MenuData,
        val task: Int,
    )



}