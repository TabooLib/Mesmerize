package io.izzel.mesmerize.impl.event;

import io.izzel.mesmerize.api.DefaultStats;
import io.izzel.mesmerize.api.data.NumberValue;
import io.izzel.mesmerize.api.data.RangeNumberValue;
import io.izzel.mesmerize.api.event.StatsRefreshEvent;
import io.izzel.mesmerize.api.visitor.util.StatsSet;
import io.izzel.mesmerize.impl.Mesmerize;
import io.izzel.mesmerize.impl.config.spec.ConfigSpec;
import io.izzel.mesmerize.impl.config.spec.GeneralSpec;
import io.izzel.mesmerize.impl.config.spec.HealthSpec;
import io.izzel.mesmerize.impl.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class AttributeListener implements Listener {

    private final Random random = new Random();

    public AttributeListener() {
        Bukkit.getScheduler().runTaskTimer(Mesmerize.instance(), () -> {
            if (!ConfigSpec.spec().health().enableHealthControl()) {
                return;
            }
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.isDead() || !player.isValid()) continue;
                Optional<List<NumberValue<Double>>> regen = DefaultStats.REGENERATION.tryApply(StatsSet.of(player), null);
                regen.ifPresent(list -> {
                    //noinspection ConstantConditions
                    double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                    double heal = RangeNumberValue.applyAsDouble(0, list, random);
                    player.setHealth(Util.clamp(player.getHealth() + heal, 0, maxHealth));
                });
            }
        }, 0, ConfigSpec.spec().health().regenerationTicks());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        HealthSpec health = ConfigSpec.spec().health();
        if (health.enableHealthControl()) {
            if (health.healthScaled()) {
                event.getPlayer().setHealthScaled(true);
                event.getPlayer().setHealthScale(health.heathScale());
            } else {
                event.getPlayer().setHealthScaled(false);
            }
            //noinspection deprecation
            event.getPlayer().setMaxHealth(health.defaultHealth());
        }
    }

    @EventHandler
    public void onRefresh(StatsRefreshEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            StatsSet statsSet = event.getStatsSet();

            if (ConfigSpec.spec().health().enableHealthControl()) {
                AttributeInstance attribute = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                if (attribute != null) {
                    HealthSpec healthSpec = ConfigSpec.spec().health();
                    for (AttributeModifier modifier : attribute.getModifiers()) {
                        if (modifier.getName().equals("mesmerize:health")) {
                            attribute.removeModifier(modifier);
                        }
                    }
                    double baseHealth = attribute.getValue();
                    double healthModifier = DefaultStats.HEALTH.tryApply(statsSet, event).map(number -> {
                        double value = number.applyDouble(baseHealth);
                        return Util.clamp(value, healthSpec.minimalHealth(), healthSpec.maximumHealth());
                    }).orElse(baseHealth) - baseHealth;
                    attribute.addModifier(new AttributeModifier("mesmerize:health", healthModifier, AttributeModifier.Operation.ADD_NUMBER));
                    livingEntity.setHealth(Util.clamp(livingEntity.getHealth(), 0, baseHealth + healthModifier));
                }
            }

            GeneralSpec generalSpec = ConfigSpec.spec().general();
            if (generalSpec.enableSpeedControl()) {
                AttributeInstance attackSpeedAttr = livingEntity.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
                if (attackSpeedAttr != null) {
                    attackSpeedAttr.setBaseValue(DefaultStats.ATTACK_SPEED.tryApply(statsSet, event)
                        .map(number -> number.applyDouble(generalSpec.defaultAttackSpeed()))
                        .orElse(generalSpec.defaultAttackSpeed()));
                }

                double moveSpeed = DefaultStats.MOVE_SPEED.tryApply(statsSet, event).map(number -> {
                    double value = number.applyDouble(generalSpec.defaultMoveSpeed());
                    return Util.clamp(value, -1, 1);
                }).orElse(generalSpec.defaultMoveSpeed());
                double flySpeed = DefaultStats.FLY_SPEED.tryApply(statsSet, event).map(number -> {
                    double value = number.applyDouble(generalSpec.defaultFlySpeed());
                    return Util.clamp(value, -1, 1);
                }).orElse(generalSpec.defaultFlySpeed());
                if (entity instanceof Player) {
                    Player player = (Player) entity;
                    player.setWalkSpeed((float) moveSpeed);
                    player.setFlySpeed((float) flySpeed);
                } else {
                    AttributeInstance moveSpeedAttr = ((LivingEntity) entity).getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
                    if (moveSpeedAttr != null) {
                        moveSpeedAttr.setBaseValue(moveSpeed);
                    }
                    AttributeInstance flySpeedAttr = ((LivingEntity) entity).getAttribute(Attribute.GENERIC_FLYING_SPEED);
                    if (flySpeedAttr != null) {
                        flySpeedAttr.setBaseValue(flySpeed);
                    }
                }
            }
        }
    }
}
