package io.izzel.mesmerize.impl.config;

import io.izzel.mesmerize.api.visitor.StatsHolder;
import io.izzel.mesmerize.api.visitor.VisitMode;
import io.izzel.mesmerize.api.visitor.util.StatsSet;
import io.izzel.mesmerize.impl.Mesmerize;
import io.izzel.taboolib.module.inject.TInject;
import io.izzel.taboolib.module.locale.TLocale;
import io.izzel.taboolib.module.locale.logger.TLogger;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LocalRepository {

    @TInject
    private static TLogger LOGGER;

    private final Map<String, StatsHolder> map = new HashMap<>();

    public StatsHolder get(String s) {
        return map.get(s);
    }

    public Collection<String> keys() {
        return map.keySet();
    }

    public void loadAndValidate() {
        this.map.clear();
        Path path = Mesmerize.instance().getDataFolder().toPath().resolve("data");
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            Iterator<Path> iterator = Files.walk(path, 32)
                .filter(it -> !Files.isDirectory(it))
                .filter(it -> it.getFileName().toString().endsWith(".yml")).iterator();
            while (iterator.hasNext()) {
                Path next = iterator.next();
                try {
                    String node = path.relativize(next).toString().replace(File.separator, ".").replace(".yml", "");
                    YamlConfiguration configuration = YamlConfiguration.loadConfiguration(Files.newBufferedReader(next, StandardCharsets.UTF_8));
                    for (String key : configuration.getKeys(false)) {
                        String name = node + "." + key;
                        try {
                            StatsSet statsSet = new StatsSet();
                            new YamlStatsHolder(configuration.getConfigurationSection(key)).accept(statsSet, VisitMode.DATA);
                            this.map.put(name, statsSet);
                        } catch (Throwable t) {
                            LOGGER.error(TLocale.asString("load.fail.node", name, t));
                        }
                    }
                } catch (Throwable t) {
                    LOGGER.error(TLocale.asString("load.fail.file", next, t));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.info(TLocale.asString("load.count", this.map.size()));
    }
}
