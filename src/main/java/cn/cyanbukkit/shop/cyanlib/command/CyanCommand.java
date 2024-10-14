package cn.cyanbukkit.shop.cyanlib.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static cn.cyanbukkit.shop.cyanlib.launcher.CyanPluginLauncher.cyanPlugin;

/**
 *  全新执行注册核心类
 */
public abstract class CyanCommand {
    /**
     * 主指令 没有参数的
     *
     * @param commandLabel 指令
     * @param args         参数        执行总指令的时候会返回数组 该方法会先跑然后在找sub 去跑sub
     */
    public abstract void mainExecute(CommandSender sender, String commandLabel, String[] args);

    /**
     * 预设帮助
     *
     * @return 返回帮助信息
     */
    public String help(CommandSender sender) {
        String helpMessage = "§8 - " + cyanPlugin.getName() + "§f - 帮助信息\n";
        StringBuilder stringBuilder = new StringBuilder(helpMessage);
        RegisterCommand registerMainCommand = this.getClass().getAnnotation(RegisterCommand.class);
        stringBuilder.append("§8|-/").append(registerMainCommand.name()).append(" help - 查看帮助\n");
        // 设计一个精美指令样式图
        Method[] methods = this.getClass().getDeclaredMethods();
        for (Method method : methods) {
            RegisterSubCommand registerSubCommand = method.getAnnotation(RegisterSubCommand.class);
            if (registerSubCommand == null) {
                continue;
            }
            stringBuilder.append("§8|-§f/").append(registerMainCommand.name()).append(" ").append(registerSubCommand.subName())
                    .append(" §b").append(registerSubCommand.howToUse()).append("\n");
        }
        return stringBuilder.toString();
    }

    /**
     * 注册子指令比如 /cyan give <player> <item> <count>
     * 你可以SubCommand 必须按照一下书写方法 可以创建好几个 方法参数必须保持一直
     *
     * @param args args返回的是  <player> <item> <count> 这个数组
     * @see RegisterSubCommand
     * 使用 @RegisterSubCommand 注解注册并且重写这个方法 其中args
     */
    public void subCommand(CommandSender sender, String commandLabel, String[] args) {
    }

    public java.util.List<String> tab(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        Method[] methods = this.getClass().getDeclaredMethods();
        for (Method method : methods) {
            RegisterSubCommand registerSubCommand = method.getAnnotation(RegisterSubCommand.class);
            if (registerSubCommand == null) {
                continue;
            }
            if (args.length == 1) {
                list.add(registerSubCommand.subName());
            }
        }
        return list;
    }


    /**
     * 注册成SpigotMC指令
     */
    public final void register() {
        // 查看谁继承了CyanCommand 并且获取
        RegisterCommand registerMainCommand = this.getClass().getAnnotation(RegisterCommand.class);
        if (registerMainCommand == null) {
            cyanPlugin.getLogger().warning("你也没注册指令啊? 你这个憨批"); // 我没骂你这是copilot骂的
            return;
        }
        // 注册主指令
        Command command = new Command(registerMainCommand.name()) {
            @Override
            public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                if (sender.hasPermission(registerMainCommand.permission())) {
                    mainExecute(sender, commandLabel, args);
                } else {
                    sender.sendMessage("§c你没有权限执行这个指令");
                }
                if (args.length >= 1) {
                    Method[] methods = CyanCommand.this.getClass().getDeclaredMethods();
                    for (Method method : methods) {
                        RegisterSubCommand registerSubCommand = method.getAnnotation(RegisterSubCommand.class);
                        if (registerSubCommand == null) {
                            continue;
                        }
                        if (args[0].equalsIgnoreCase(registerSubCommand.subName())) {
                            if (sender.hasPermission(registerSubCommand.permission()) ){
                                List<String> list = new ArrayList<>(Arrays.asList(args).subList(1, args.length));
                                method.setAccessible(true);
                                try {
                                    method.invoke(CyanCommand.this, sender, commandLabel, list.toArray(new String[0]));
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                                return true;
                            } else {
                                sender.sendMessage("§c你没有权限执行这个指令");
                                return true;
                            }
                        }
                    }

                    if (args[0].equalsIgnoreCase("help")) {
                        sender.sendMessage(help(sender));
                        return true;
                    }
                }
                // 执行子指令
                return true;
            }

            @Override
            public java.util.List<String> tabComplete(org.bukkit.command.CommandSender sender, String alias, String[] args) {
                return tab(sender, args);
            }
        };
        Class<?> pluginManagerClass = cyanPlugin.getServer().getPluginManager().getClass();
        try {
            Field field = pluginManagerClass.getDeclaredField("commandMap");
            field.setAccessible(true);
            SimpleCommandMap commandMap = (SimpleCommandMap) field.get(cyanPlugin.getServer().getPluginManager());
            commandMap.register(cyanPlugin.getName(), command);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
