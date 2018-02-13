package it.alian.gun.mesmerize.compat.hook;

import com.sucy.skill.api.event.PlayerExperienceGainEvent;
import it.alian.gun.mesmerize.MConfig;
import it.alian.gun.mesmerize.Mesmerize;
import it.alian.gun.mesmerize.lore.LoreParser;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MesmerizeSkillApi implements Listener {

    public static void init() {
        if (MConfig.General.useSkillApi) {
            Bukkit.getPluginManager().registerEvents(new MesmerizeSkillApi(), Mesmerize.instance);
        }
    }

    @EventHandler
    public void onSkillExpGain(PlayerExperienceGainEvent event) {
        if (event.getPlayerData().getPlayer() != null) {
            event.setExp(event.getExp() * (LoreParser.getByEntityId(event.getPlayerData().getPlayer().getEntityId()).getOtherExpModifier() + 1));
        }
    }

}
