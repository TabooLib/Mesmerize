package it.alian.gun.mesmerize;

import it.alian.gun.mesmerize.lore.LoreInfo;
import it.alian.gun.mesmerize.lore.LoreParser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.Yaml;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.DecimalFormat;

public class MCommand implements CommandExecutor {

    private static final DecimalFormat format = new DecimalFormat("0.##");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Show stats
        if (args.length == 1 && args[0].equalsIgnoreCase("stats") && sender instanceof Player &&
                sender.hasPermission("mesmerize.showstats")) {
            MLocale.COMMAND_SHOW_STATS.player(sender, sender.getName());
            MTasks.execute(() -> {
                LoreInfo info = new LoreParser.ParseEntityTask((Player) sender).call();
                for (Field field : info.getClass().getDeclaredFields()) {
                    try {
                        field.setAccessible(true);
                        String value = format.format(field.get(info));
                        MConfig.Stats stats = (MConfig.Stats) MConfig.Prefixes.class.getDeclaredField(field.getName()).get(null);
                        sender.sendMessage(stats.getColor() + stats.getName() + ": " + value);
                    } catch (Exception ignored) {
                    }
                }
            });
            return true;
        }
        // Evaluate item
        if (args.length == 1 && args[0].equalsIgnoreCase("evaluate") && sender instanceof Player &&
                sender.hasPermission("mesmerize.evaluate")) {
            MTasks.execute(() -> {
                LoreInfo info = LoreParser.parseSingleItem(((Player) sender).getInventory().getItemInHand());
                double price = 0;
                for (Field field : info.getClass().getDeclaredFields()) {
                    try {
                        field.setAccessible(true);
                        double value = (double) field.get(info);
                        MConfig.Stats stats = (MConfig.Stats) MConfig.Prefixes.class.getDeclaredField(field.getName()).get(null);
                        price += stats.getValuePerPercentage() * value * 100D;
                    } catch (Throwable ignored) {
                    }
                }
                price *= ((Player) sender).getInventory().getItemInHand().getAmount();
                sender.sendMessage(String.format(MConfig.Message.onPriceEvaluate, price));
            });
            return true;
        }
        // Config save load
        if (args.length >= 2 && args[0].equalsIgnoreCase("config") && sender.hasPermission("mesmerize.config")) {
            MTasks.execute(() -> {
                if (args[1].equalsIgnoreCase("reload")) {
                    MConfig.load();
                    MConfig.save();
                    MLocale.GENERAL_CONFIG_LOAD.player(sender);
                    return;
                }
                if (args[1].equalsIgnoreCase("load")) {
                    MConfig.load();
                    MLocale.GENERAL_CONFIG_LOAD.player(sender);
                    return;
                }
                if (args[1].equalsIgnoreCase("save")) {
                    MConfig.save();
                    MLocale.GENERAL_CONFIG_SAVE.player(sender);
                    return;
                }
                if (args.length == 4 && args[1].equalsIgnoreCase("set")) {
                    String[] path = args[2].split("\\.");
                    Object rev = new MConfig();
                    Class clazz = MConfig.class;
                    try {
                        for (int i = 0; i < path.length - 1; i++) {
                            Field field = rev.getClass().getDeclaredField(path[i]);
                            field.setAccessible(true);
                            rev = field.get(rev);
                            clazz = rev.getClass();
                        }
                        Field field = clazz.getDeclaredField(path[path.length - 1]);
                        Object prev = field.get(rev);
                        if (field.getType().equals(int.class) || field.getType().equals(Integer.class)) {
                            field.set(rev, Integer.parseInt(args[3]));
                        } else if (field.getType().equals(long.class) || field.getType().equals(Long.class)) {
                            field.set(rev, Long.parseLong(args[3]));
                        } else if (field.getType().equals(double.class) || field.getType().equals(Double.class)) {
                            field.set(rev, Double.parseDouble(args[3]));
                        } else if (field.getType().equals(float.class) || field.getType().equals(Float.class)) {
                            field.set(rev, Float.parseFloat(args[3]));
                        } else if (field.getType().equals(boolean.class) || field.getType().equals(Boolean.class)) {
                            field.set(rev, Boolean.parseBoolean(args[3]));
                        } else
                            field.set(rev, args[3]);
                        MLocale.CONFIG_SET.player(sender, args[2], String.valueOf(prev), args[3]);
                    } catch (ReflectiveOperationException e) {
                        MLocale.WARN_CONFIG_SET.player(sender, args[2]);
                    }
                    return;
                }
                if (args.length == 3 && args[1].equalsIgnoreCase("list")) {
                    String[] path = args[2].split("\\.");
                    Object rev = new MConfig();
                    Class clazz = MConfig.class;
                    try {
                        for (String aPath : path) {
                            Field field = rev.getClass().getDeclaredField(aPath);
                            field.setAccessible(true);
                            rev = field.get(rev);
                            clazz = rev.getClass();
                        }
                        Field[] fields = clazz.getDeclaredFields();
                        MLocale.CONFIG_LIST.player(sender, args[2], String.valueOf(fields.length));
                        Yaml yaml = new Yaml();
                        for (Field field : fields) {
                            field.setAccessible(true);
                            if (!Modifier.isTransient(field.getModifiers())) {
                                if (field.getType().getName().contains(".") && field.getType().getName().contains("$"))
                                    MLocale.CONFIG_LIST_1.player(sender, field.getName());
                                else
                                    MLocale.CONFIG_LIST_2.player(sender, field.getName(),
                                            field.getType().getName().substring(field.getType().getName().lastIndexOf('.') + 1),
                                            yaml.dump(field.get(rev)).replace("\n", "").replace("\r", ""));
                            }
                        }
                    } catch (ReflectiveOperationException e) {
                        MLocale.WARN_CONFIG_SET.player(sender, args[2]);
                    }
                    return;
                }
                help(sender);
            });
            return true;
        }
        help(sender);
        return true;
    }

    private static void help(CommandSender sender) {
        MLocale.COMMAND_ERROR.player(sender);
        sender.sendMessage(MLocale.COMMAND_HELP_LIST.msg().split("\n"));
    }

    public static void init() {
        MCommand instance = new MCommand();
        Mesmerize.instance.getCommand("mes").setExecutor(instance);
        Mesmerize.instance.getCommand("mesmerize").setExecutor(instance);
    }

}
