package it.alian.gun.mesmerize.compat.hook;

import it.alian.gun.mesmerize.MConfig;
import it.alian.gun.mesmerize.lore.LoreInfo;
import it.alian.gun.mesmerize.lore.LoreParser;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.text.DecimalFormat;

public class MesmerizePlaceholder extends EZPlaceholderHook {

    private static final DecimalFormat format = new DecimalFormat(MConfig.Misc.customDecimalFormat);

    public MesmerizePlaceholder(Plugin plugin, String identifier) {
        super(plugin, identifier);
    }

    @Override
    public String onPlaceholderRequest(Player player, String s) {
        if (s.startsWith("stats_")) {
            String key = s.substring("stats_".length());
            LoreInfo info = LoreParser.getByEntityId(player.getEntityId());
            try {
                Field field = info.getClass().getDeclaredField(key);
                field.setAccessible(true);
                return format.format(field.get(info));
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

}
