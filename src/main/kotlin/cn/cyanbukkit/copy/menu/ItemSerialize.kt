package cn.cyanbukkit.copy.menu

import org.bukkit.Material
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack


object ItemSerialize {

    /**
     * 序列化itemStack为String
     *
     * @param itemStack 物品
     * @Return String
     */
    fun itemStackSerialize(itemStack: ItemStack): String {
        val yml = YamlConfiguration()
        yml.set("is", itemStack)
        return yml.saveToString()
    }

    /**
     * 反序列化String为itemStack
     *
     * @param str 物品str
     * @return ItemStack
     */
    fun itemStackDeserialize(str: String): ItemStack {
        val yml = YamlConfiguration()
        val item: ItemStack? = try {
            yml.loadFromString(str)
            yml.getItemStack("is")
        } catch (ex: InvalidConfigurationException) {
            ItemStack(Material.AIR, 1)
        }
        return item!!
    }



    fun itemStackListDeserialize(objectList : Any) : List<ItemStack> {
        if (objectList is String) return listOf()
        // 对列表解析
        try {
            return objectList as List<ItemStack>
        } catch (e : Exception) {
            return listOf()
        }
    }




}