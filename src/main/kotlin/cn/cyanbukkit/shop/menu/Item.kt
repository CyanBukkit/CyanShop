package cn.cyanbukkit.shop.menu

import org.bukkit.inventory.ItemStack

data class Item(
    val itemStack: ItemStack,
    val condition: List<String>,
    val playerAmount : Int,
    val globalAmount : Int,
    val pricePlayerCMD : List<String>,
    val priceGlobalCMD : List<String>,
    val priceItems : List<ItemStack>,
    val priceClose : Boolean,
    val priceExp : Int,
    val priceMoney : Int,
    val pricePoint : Int,
    val priceRMB : Int,
    val awardPlayerCMD : List<String>,
    val awardGlobalCMD : List<String>,
    val awardItems : List<ItemStack>,
    val awardClose : Boolean,
    val awardExp : Int,
    val awardMoney : Int,
    val awardPoint : Int,
    val awardRMB : Int,
)
