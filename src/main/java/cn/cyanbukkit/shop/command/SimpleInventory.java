package cn.cyanbukkit.shop.command;

import cn.cyanbukkit.shop.cyanlib.inventory.ClickableItem;
import cn.cyanbukkit.shop.cyanlib.inventory.content.InventoryContents;
import cn.cyanbukkit.shop.cyanlib.inventory.content.InventoryProvider;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class SimpleInventory implements InventoryProvider {


    private final Random random = new Random();

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fillBorders(ClickableItem.empty(new ItemStack(Material.BLACK_STAINED_GLASS_PANE)));
        contents.set(1, 1, ClickableItem.of(new ItemStack(Material.CARROT),
                e -> player.sendMessage(ChatColor.GOLD + "You clicked on a potato.")));
        contents.set(1, 7, ClickableItem.of(new ItemStack(Material.BARRIER),
                e -> player.closeInventory()));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        int state = contents.property("state", 0);
        contents.setProperty("state", state + 1);

        if(state % 5 != 0)
            return;

        short durability = (short) random.nextInt(15);

        ItemStack glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1, durability);
        contents.fillBorders(ClickableItem.empty(glass));
    }
}
