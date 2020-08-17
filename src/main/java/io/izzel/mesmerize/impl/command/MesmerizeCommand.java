package io.izzel.mesmerize.impl.command;

import com.google.common.collect.ImmutableList;
import io.izzel.mesmerize.api.DefaultStats;
import io.izzel.mesmerize.api.data.MultiValue;
import io.izzel.mesmerize.api.data.StatsSetValue;
import io.izzel.mesmerize.api.service.StatsService;
import io.izzel.mesmerize.api.visitor.StatsHolder;
import io.izzel.mesmerize.api.visitor.StatsVisitor;
import io.izzel.mesmerize.api.visitor.VisitMode;
import io.izzel.mesmerize.api.visitor.util.StatsSet;
import io.izzel.mesmerize.impl.Mesmerize;
import io.izzel.taboolib.module.command.base.Argument;
import io.izzel.taboolib.module.command.base.BaseCommand;
import io.izzel.taboolib.module.command.base.BaseMainCommand;
import io.izzel.taboolib.module.command.base.BaseSubCommand;
import io.izzel.taboolib.module.command.base.CommandType;
import io.izzel.taboolib.module.command.base.SubCommand;
import io.izzel.taboolib.module.locale.TLocale;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Optional;

@BaseCommand(name = "mesmerize")
public class MesmerizeCommand extends BaseMainCommand {

    @SubCommand(permission = "mesmerize.reload")
    public void reload(CommandSender sender, String... args) {
        Mesmerize.instance().reloadMesmerizeData();
        TLocale.sendTo(sender, "command.reload");
    }

    @SubCommand(permission = "mesmerize.attach", type = CommandType.PLAYER)
    public BaseSubCommand attach = new BaseSubCommand() {

        @Override
        public Argument[] getArguments() {
            return new Argument[]{new Argument("node", () -> ImmutableList.copyOf(Mesmerize.instance().getLocalRepository().keys()))};
        }

        @Override
        public void onCommand(CommandSender sender, Command command, String s, String[] args) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args.length == 0) {
                    TLocale.sendTo(player, "command.set_not_provide");
                    return;
                }
                Optional<StatsHolder> optional = StatsService.instance().getStatsManager().get(args[0]);
                if (optional.isPresent()) {
                    ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
                    if (itemInMainHand.getAmount() > 0 && itemInMainHand.getItemMeta() != null) {
                        ItemMeta itemMeta = itemInMainHand.getItemMeta();
                        StatsSet statsSet = new StatsSet();
                        StatsService.instance().newStatsHolder(itemMeta).accept(statsSet, VisitMode.DATA);
                        MultiValue<StatsHolder, StatsSetValue> multiValue = new MultiValue<>(true, StatsSetValue::new);
                        new StatsSetValue(args[0]).accept(multiValue, VisitMode.DATA);
                        multiValue.accept(statsSet.visitStats(DefaultStats.STATS_SET), VisitMode.DATA);
                        StatsVisitor writer = StatsService.instance().newStatsWriter(itemMeta);
                        statsSet.accept(writer, VisitMode.DATA);
                        itemInMainHand.setItemMeta(itemMeta);
                        player.getInventory().setItemInMainHand(itemInMainHand);
                        StatsService.instance().refreshCache(player, true);
                        TLocale.sendTo(player, "command.attach", args[0]);
                    } else {
                        TLocale.sendTo(player, "command.item_not_present");
                    }
                } else {
                    TLocale.sendTo(player, "command.set_not_present", args[0]);
                }
            }
        }
    };
}
