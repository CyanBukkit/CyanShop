package cn.cyanbukkit.copy.menu

data class MenuData(
    val title: String,
    val size: Int,
    // map of slot to item
    val items: MutableMap<Int, Item>,
)
