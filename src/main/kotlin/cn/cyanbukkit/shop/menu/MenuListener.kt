package cn.cyanbukkit.shop.menu

import cn.cyanbukkit.shop.CyanShop
import cn.cyanbukkit.shop.CyanShop.translateColor
import cn.cyanbukkit.shop.cyanlib.launcher.CyanPluginLauncher.cyanPlugin
import me.clip.placeholderapi.PlaceholderAPI
import org.black_ixx.playerpoints.PlayerPoints
import org.black_ixx.playerpoints.PlayerPointsAPI
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryInteractEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import java.util.function.Consumer


object MenuListener : Listener {

    private val openingMenuMap = mutableMapOf<Player, OpeningMenu>()

    private val refreshConsumers = mutableMapOf<Player, Consumer<ConsumerData>>()

    fun menuToInventory(player: Player, menu: MenuData) {
        refreshConsumers[player] = Consumer { data ->
            data.inventory.clear()
            for (it in data.menuData.items) {
                val item = it.value.itemStack.clone()
//                if (item.type.isAir) continue
                val im = item.itemMeta
                im?.setDisplayName(PlaceholderAPI.setPlaceholders(player, im.displayName))
                val lore = if (im?.hasLore() == true) {
                    PlaceholderAPI.setPlaceholders(player, im.lore!!.tran() )
                } else {
                    mutableListOf()
                }
                lore.add(player.papi("§5条件:"))
                it.value.condition.forEach {
                    try {
                        lore.add(player.papi(it.check()))
                    } catch (e: Exception) {
                        lore.add(player.papi("§c${it} §4§l[条件错误]"))
                    }
                }
                lore.add(player.papi("§5花费:"))
                if (player.exp >= it.value.priceExp) {
                    lore.add(player.papi("§7➥ §a§l√ §a经验: §e${it.value.priceExp}"))
                } else {
                    lore.add(player.papi("§7➥ §c§l✘ §c经验: §e${it.value.priceExp}"))
                }
                if (CyanShop.econ!!.getBalance(player) >= it.value.priceMoney) {
                    lore.add(player.papi("§7➥ §a§l√ §a金币: §e${it.value.priceMoney}"))
                } else {
                    lore.add(player.papi("§7➥ §c§l✘ §c金币: §e${it.value.priceMoney}"))
                }
                if(PlayerPointsAPI(PlayerPoints.getInstance()).look(player.uniqueId) >= it.value.pricePoint) {
                    lore.add(player.papi("§7➥ §a§l√ §a点券: §e${it.value.pricePoint}"))
                } else {
                    lore.add(player.papi("§7➥ §c§l✘ §c点券: §e${it.value.pricePoint}"))
                }
                // 解决不能识别RGB文字和 同一个物品显示很多行的问题
                val allThings = mutableMapOf<ItemStack, Int>()
                for (thing in it.value.priceItems) {
                    if (allThings.containsKey(thing)) {
                        allThings[thing] = allThings[thing]!! + thing.amount
                    } else {
                        allThings[thing] = thing.amount
                    }
                }
                val playerAllItems = mutableMapOf<ItemStack, Int>()
                for (thing in player.inventory) {
                    if (thing != null) {
                        if (playerAllItems.containsKey(thing)) {
                            playerAllItems[thing] = playerAllItems[thing]!! + thing.amount
                        } else {
                            playerAllItems[thing] = thing.amount
                        }
                    }
                }


                for (entry in allThings.entries) {
                    val lI = entry.key
                    val lIValue = entry.value

                    val matchingItem = playerAllItems.keys.find { pItem ->
                        lI.type == pItem.type && lI.itemMeta?.displayName == ( if (pItem.itemMeta?.hasDisplayName() == true) {
                            // 因为在物品放在分别器上物品名字会因为替换后不识别 所以导致不一致 现在改成它对应的物品
                            pItem.itemMeta?.displayName
                        } else {
                            CyanShop.materialConfig.getString(pItem.type.name) ?: pItem.type.name
                        } )
                    }

                    if (matchingItem != null) {
                        val pItemValue = playerAllItems[matchingItem] ?: 0
                        if (lIValue <= pItemValue) {
                            val str = player.papi("§7➥ §a§l√ §a${lI.itemMeta?.displayName} §e${lIValue}")
                            lore.add(str)
                        } else {
                            val str = player.papi("§7➥ §c§l✘ §c${lI.itemMeta?.displayName} §e${lIValue} (数量少了你只有$pItemValue)")
                            lore.add(str)
                        }
                    } else {
                        val str = player.papi("§7➥ §c§l✘ §c${lI.itemMeta?.displayName} §e${lIValue} (没有这个物品)")
                        lore.add(str)
                    }
                }


                im?.lore = lore
                item.itemMeta = im
                data.inventory.setItem(it.key, item)
            }
        }

        val newInventory = Bukkit.createInventory(null, menu.size * 9, menu.title.translateColor())
        val cData = ConsumerData(menu, newInventory)
        val taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(cyanPlugin, Runnable {
            refreshConsumers[player]?.accept(cData)
        }, 0, 60).taskId
        player.openMenu(newInventory)
        openingMenuMap[player] = OpeningMenu(menu, taskId, newInventory, cData)
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
        val x = this.split("||").map { it.trim() }
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

    private fun Player.papi(s: String): String {
        return PlaceholderAPI.setPlaceholders(this, s)
    }

    @EventHandler
    fun onInto(e : InventoryInteractEvent) {
        if (e.whoClicked !is Player) return
        val p = e.whoClicked as Player
        if (!openingMenuMap.containsKey(p)) return
        e.isCancelled = true
    }

    private fun Player.openMenu(menu: Inventory) {
        val p = this
        // 创建异步操作
        object : BukkitRunnable() {
            override fun run() {
                //  这里直接使用调度器同步打开这个gui即可
                Bukkit.getScheduler().runTask(cyanPlugin, Runnable {
                    p.openInventory(menu)
                })
            }
        }.runTaskAsynchronously(cyanPlugin)
    }


    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        if (e.whoClicked !is Player) return
        val p = e.whoClicked as Player
        if (!openingMenuMap.containsKey(p)) return
        val menu = openingMenuMap[p]!!
        // 点击判定执行指令
        if (e.currentItem == null) return
        e.isCancelled = true
        if (e.clickedInventory != e.inventory) return
        if (menu.menuData.items.containsKey(e.slot)) {
            p.price(menu.menuData.items[e.slot]!!)
        }
    }

    /**
     * 点击判定 包括兑换后的事件都在这
     */
    private fun Player.price(item: Item) {
        val p = this
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
        val allThings = mutableMapOf<ItemStack, Int>()
        for (thing in item.priceItems) {
            if (allThings.containsKey(thing)) {
                allThings[thing] = allThings[thing]!! + thing.amount
            } else {
                allThings[thing] = thing.amount
            }
        }
        // 玩家拥有的所有物品
        val playerAllItems = mutableMapOf<ItemStack, Int>()
        for (thing in p.inventory) {
            if (thing != null) {
                if (playerAllItems.containsKey(thing)) {
                    playerAllItems[thing] = playerAllItems[thing]!! + thing.amount
                } else {
                    playerAllItems[thing] = thing.amount
                }
            }
        }
        for (entry in allThings.entries) {
            val lI = entry.key
            val lIValue = entry.value

            val matchingItem = playerAllItems.keys.find { pItem ->
                lI.type == pItem.type && lI.itemMeta?.displayName == ( if (pItem.itemMeta?.hasDisplayName() == true) {
                    // 因为在物品放在分别器上物品名字会因为替换后不识别 所以导致不一致 现在改成它对应的物品
                    pItem.itemMeta?.displayName
                } else {
                    CyanShop.materialConfig.getString(pItem.type.name) ?: pItem.type.name
                } )
            }
            val pItemValue = playerAllItems[matchingItem] ?: 0
            if (matchingItem == null) {
                p.sendMessage("§c§l你没有带够这些物品")
                return
            }
            if (lIValue > pItemValue) {
                p.sendMessage("§c§l你的物品不足")
                return
            }
        }
        allThings.forEach { (t, u) ->
            // 根据allThings的物品进行在玩家p背包中的物品数量减少 不要用while
            var u1 = u
            for (thing in p.inventory) {
                if (thing != null  && t.type == thing.type
                    && t.itemMeta?.displayName == ( if (thing.itemMeta?.hasDisplayName() == true) {
                        thing.itemMeta?.displayName
                    } else {// 因为在物品放在分别器上物品名字会因为替换后不识别 所以导致不一致 现在改成它对应的物品
                        CyanShop.materialConfig.getString(thing.type.name) ?: thing.type.name
                    } )) {
                    p.sendMessage("§a正在移除...${u} 个 ${t.itemMeta?.displayName} ")
                    if (thing.amount >= u1) {
                        thing.amount -= u1
                        // 如果物品数量减到零，移除该物品
                        if (thing.amount == 0) {
                            p.inventory.remove(thing) // 从玩家背包中移除物品
                        }
                        break
                    } else {
                        u1 -= thing.amount
                        thing.amount = 0
                        p.inventory.remove(thing) // 从玩家背包中移除物品
                    }
                }
            }
        }


        p.updateInventory() // 更新玩家背包显示
        CyanShop.econ!!.withdrawPlayer(p, item.priceMoney.toDouble())
        PlayerPointsAPI(PlayerPoints.getInstance()).take(p.uniqueId, item.pricePoint)
        // exp take
        p.level -= item.priceExp
        // Award
        item.awardItems.forEach {
            p.inventory.addItem(it)
        }
        item.awardPlayerCMD.forEach {
            Bukkit.dispatchCommand(p, p.papi(it))
        }
        item.awardGlobalCMD.forEach {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), p.papi(it))
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
        } else {
            refreshConsumers[player]?.accept(openingMenuMap[p]!!.data)
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
        refreshConsumers.remove(p)
    }


    data class OpeningMenu(
        val menuData: MenuData,
        val task: Int,
        val inventory: Inventory,
        val data : ConsumerData
    )

    data class ConsumerData (
        val menuData: MenuData,
        val inventory: Inventory// 用于更新背包
    )

}