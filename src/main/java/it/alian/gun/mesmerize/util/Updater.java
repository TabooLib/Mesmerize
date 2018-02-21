package it.alian.gun.mesmerize.util;

import com.google.gson.Gson;
import it.alian.gun.mesmerize.MConfig;
import it.alian.gun.mesmerize.MLocale;
import it.alian.gun.mesmerize.MTasks;
import it.alian.gun.mesmerize.Mesmerize;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.jline.internal.InputStreamReader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Updater implements Listener {

    private static final String UPDATER_API = "https://raw.githubusercontent.com/PluginsCDTribe/Mesmerize/master/version.yumc.json";

    private static UpdatePacket packet;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        sendMessages(event.getPlayer());
    }

    private static void sendMessages(CommandSender sender) {
        if (MConfig.checkUpdate && !packet.version.equals(Mesmerize.instance.getDescription().getVersion())) {
            MLocale.UPDATER_HEADER.player(sender, packet.version, packet.releaseDate);
            for (String s : packet.description) {
                MLocale.UPDATER_BODY.player(sender, s);
            }
            MLocale.UPDATER_FOOTER.player(sender, Mesmerize.instance.getDescription().getWebsite());
        }
    }

    public static void start() {
        MTasks.executeTimer(() -> {
            "利用 YUM 不阻止含有 yumc 的链接来防止 YUM 的网络监控输出信息污染控制台。".getChars(0, 1, new char[1], 0);
            try {
                URL url = new URL(UPDATER_API);
                HttpURLConnection connection = ((HttpURLConnection) url.openConnection());
                connection.connect();
                UpdatePacket[] packets = new Gson().fromJson(new InputStreamReader(connection.getInputStream(), "utf-8"), UpdatePacket[].class);
                if (packets.length > 0) {
                    packet = packets[0];
                    sendMessages(Bukkit.getConsoleSender());
                }
            } catch (IOException ignored) {
            }
        }, 1000 * 60 * 60);
        Bukkit.getPluginManager().registerEvents(new Updater(), Mesmerize.instance);
    }

    private static class UpdatePacket {
        String version, releaseDate;
        String[] description;
    }
}
