package cn.cyanbukkit.shop.command;

import cn.cyanbukkit.shop.cyanlib.command.CyanCommand;
import cn.cyanbukkit.shop.cyanlib.command.RegisterCommand;
import cn.cyanbukkit.shop.cyanlib.command.RegisterSubCommand;
import cn.cyanbukkit.shop.cyanlib.inventory.SmartInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RegisterCommand(name = "CyanBukkit", permission = "CyanBukkit.dev")
public class MyCommand extends CyanCommand {


    @Override
    public void mainExecute(CommandSender sender, String commandLabel, String[] args) {

    }


    @RegisterSubCommand(subName = "test")
    public void subCommand(CommandSender sender, String commandLabel, String[] args) {
        sender.sendMessage("§8这是一个子指令");
    }


    @RegisterSubCommand(subName = "opengui")
    public void gui(CommandSender sender, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            SmartInventory INVENTORY = SmartInventory.builder()
                    .id("myInventory")
                    .provider(new SimpleInventory())
                    .size(3, 9)
                    .title(ChatColor.BLUE + "My Awesome Inventory!")
                    .build();
        }
    }



}
