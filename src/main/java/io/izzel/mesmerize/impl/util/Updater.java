package io.izzel.mesmerize.impl.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.izzel.mesmerize.impl.Mesmerize;
import io.izzel.mesmerize.impl.config.spec.ConfigSpec;
import io.izzel.taboolib.module.locale.TLocale;
import org.bukkit.util.NumberConversions;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Updater {

    private static final ScheduledExecutorService POOL = Executors.newScheduledThreadPool(1);
    private static final String API_URL = "https://api.github.com/repos/TabooLib/Mesmerize/releases";

    public static void start() {
        POOL.scheduleAtFixedRate(Updater::fetch, 0, 5, TimeUnit.HOURS);
    }

    private static void fetch() {
        if (!ConfigSpec.spec().general().updateCheck()) return;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(API_URL).openConnection();
            try (InputStream stream = connection.getInputStream()) {
                JsonArray array = new JsonParser().parse(new InputStreamReader(stream)).getAsJsonArray();
                JsonObject object = array.get(0).getAsJsonObject();
                String version = object.get("tag_name").getAsString();
                String current = Mesmerize.instance().getDescription().getVersion();
                if (isAfter(current, version)) {
                    String date = object.get("published_at").getAsString();
                    TLocale.sendToConsole("general.update", version, date);
                }
            }
        } catch (Throwable ignored) {
        }
    }

    private static boolean isAfter(String current, String newVersion) {
        String[] cur = current.split("\\.");
        String[] ver = newVersion.split("\\.");
        for (int i = 0; i < ver.length; i++) {
            int a = NumberConversions.toInt(ver[i]);
            int b = NumberConversions.toInt(cur[i]);
            if (a != b) {
                return a > b;
            }
        }
        return false;
    }
}
