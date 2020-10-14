package io.izzel.mesmerize.impl.event;

import io.izzel.mesmerize.api.DefaultStats;
import io.izzel.mesmerize.api.data.complex.PotionValue;
import io.izzel.mesmerize.api.visitor.util.StatsSet;
import io.izzel.mesmerize.impl.config.spec.ConfigSpec;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Optional;

public class PotionApplyTask extends BukkitRunnable {

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            StatsSet set = StatsSet.of(player);
            Optional<List<PotionValue>> optional = DefaultStats.WEARING_POTION.tryApply(set, null);
            if (optional.isPresent()) {
                for (PotionValue potionValue : optional.get()) {
                    PotionEffect potionEffect = player.getPotionEffect(potionValue.get().getType());
                    if (potionEffect != null && potionEffect.getDuration() > ConfigSpec.spec().general().potionApplyInterval()) {
                        continue;
                    }
                    potionValue.get().apply(player);
                }
            }
        }
    }
}
