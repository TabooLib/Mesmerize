package io.izzel.mesmerize.impl.event;

import io.izzel.mesmerize.api.DefaultStats;
import io.izzel.mesmerize.api.data.NumberValue;
import io.izzel.mesmerize.api.data.RangeNumberValue;
import io.izzel.mesmerize.api.data.StatsNumber;
import io.izzel.mesmerize.api.event.StatsRefreshEvent;
import io.izzel.mesmerize.api.visitor.util.StatsSet;
import io.izzel.mesmerize.impl.Mesmerize;
import io.izzel.mesmerize.impl.config.spec.ConfigSpec;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class AttributeListener implements Listener {

    private final Random random = new Random();

    public AttributeListener() {
        Bukkit.getScheduler().runTaskTimer(Mesmerize.instance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Optional<List<NumberValue<Double>>> regen = DefaultStats.REGENERATION.tryApply(StatsSet.of(player), null);
                regen.ifPresent(list -> {
                    double heal = RangeNumberValue.applyAsDouble(0, list, random);
                    player.setHealth(player.getHealth() + heal);
                });
            }
        }, 0, ConfigSpec.spec().general().attributeApplyInterval());
    }

    @EventHandler
    public void onRefresh(StatsRefreshEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity) {
            StatsSet statsSet = event.getStatsSet();
            AttributeInstance attribute = ((LivingEntity) entity).getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (attribute != null) {
                Optional<StatsNumber<Double>> health = DefaultStats.HEALTH.tryApply(statsSet, event);
                health.ifPresent(number -> attribute.setBaseValue(number.applyDouble(ConfigSpec.spec().general().defaultHealth())));
            }
            AttributeInstance attackSpeedAttr = ((LivingEntity) entity).getAttribute(Attribute.GENERIC_ATTACK_SPEED);
            if (attackSpeedAttr != null) {
                Optional<StatsNumber<Double>> attackSpeed = DefaultStats.ATTACK_SPEED.tryApply(statsSet, event);
                attackSpeed.ifPresent(number -> attackSpeedAttr.setBaseValue(number.applyDouble(ConfigSpec.spec().general().defaultAttackSpeed())));
            }
            AttributeInstance moveSpeedAttr = ((LivingEntity) entity).getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
            if (moveSpeedAttr != null) {
                Optional<StatsNumber<Double>> moveSpeed = DefaultStats.MOVE_SPEED.tryApply(statsSet, event);
                moveSpeed.ifPresent(number -> moveSpeedAttr.setBaseValue(number.applyDouble(ConfigSpec.spec().general().defaultMoveSpeed())));
            }
            AttributeInstance flySpeedAttr = ((LivingEntity) entity).getAttribute(Attribute.GENERIC_FLYING_SPEED);
            if (flySpeedAttr != null) {
                Optional<StatsNumber<Double>> flySpeed = DefaultStats.FLY_SPEED.tryApply(statsSet, event);
                flySpeed.ifPresent(number -> flySpeedAttr.setBaseValue(number.applyDouble(ConfigSpec.spec().general().defaultFlySpeed())));
            }
        }
    }
}
