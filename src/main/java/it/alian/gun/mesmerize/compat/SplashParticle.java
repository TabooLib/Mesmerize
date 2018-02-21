package it.alian.gun.mesmerize.compat;

import it.alian.gun.mesmerize.MConfig;
import it.alian.gun.mesmerize.Mesmerize;
import it.alian.gun.mesmerize.util.Collections;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class SplashParticle implements Listener {

    private static SplashParticle impl;

    private static List<Effect> effects;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player) && MConfig.Misc.removeOnDeath) {
            Bukkit.getScheduler().runTaskLater(Mesmerize.instance, () -> event.getEntity().remove(), MConfig.Misc.removeDelay);
            ExperienceOrb orb = event.getEntity().getWorld().spawn(event.getEntity().getLocation(), ExperienceOrb.class);
            orb.setExperience(event.getDroppedExp());
        }
        impl.generateParticles(event.getEntity());
    }

    public abstract void generateParticles(Entity entity);

    public static void init() {
        effects = Arrays.stream(MConfig.Misc.splashParticles).map(Effect::valueOf).collect(Collectors.toList());
        if (MConfig.Misc.enableSplashParticles) {
            try {
                Bukkit.getWorlds().get(0).spigot();
                impl = new PlayEffectImpl();
            } catch (Throwable e) {
                impl = new AbstractImpl();
            }
            Bukkit.getPluginManager().registerEvents(impl, Mesmerize.instance);
        }
    }

    private static class PlayEffectImpl extends SplashParticle {

        @Override
        public void generateParticles(Entity entity) {
            if (entity instanceof LivingEntity && entity.getLastDamageCause() != null && entity.getLastDamageCause() instanceof EntityDamageByEntityEvent
                    && ((EntityDamageByEntityEvent) entity.getLastDamageCause()).getDamager() instanceof Player) {
                Effect effect = Collections.random(effects);
                entity.getWorld().spigot().playEffect(entity.getLocation(), effect, 0, 0,
                        0.5F, 0.5F, 0.5F, 0.5F, MConfig.Misc.particleAmount, MConfig.Misc.particleVisibleRange);
            }
        }
    }

    private static class AbstractImpl extends SplashParticle {

        @Override
        public void generateParticles(Entity entity) {

        }
    }

}
