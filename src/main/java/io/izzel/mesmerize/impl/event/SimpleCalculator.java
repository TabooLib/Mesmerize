package io.izzel.mesmerize.impl.event;

import io.izzel.mesmerize.api.DefaultStats;
import io.izzel.mesmerize.api.data.NumberValue;
import io.izzel.mesmerize.api.data.RangeNumberValue;
import io.izzel.mesmerize.api.data.StatsNumber;
import io.izzel.mesmerize.api.event.DamageCalculator;
import io.izzel.mesmerize.api.visitor.util.StatsSet;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class SimpleCalculator implements DamageCalculator {

    private final Random random = new Random();

    @Override
    public double calculate(@Nullable Event event, double baseDamage, StatsSet source, StatsSet target) {
        double baseDefense = 0;
        Optional<List<NumberValue<Double>>> damage = DefaultStats.DAMAGE.tryApply(source, event);
        if (damage.isPresent()) {
            baseDamage = RangeNumberValue.applyAsDouble(baseDamage, damage.get(), random);
        }
        Optional<List<NumberValue<Double>>> defense = DefaultStats.DEFENSE.tryApply(target, event);
        if (defense.isPresent()) {
            baseDefense = RangeNumberValue.applyAsDouble(baseDefense, defense.get(), random);
        }
        if (event instanceof EntityDamageByEntityEvent) {
            Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
            if (damager instanceof Player || (damager instanceof Projectile && ((Projectile) damager).getShooter() instanceof Player)) {
                if (((EntityDamageByEntityEvent) event).getEntity() instanceof Player) {
                    Optional<List<NumberValue<Double>>> pvpDamage = DefaultStats.PVP_DAMAGE.tryApply(source, event);
                    if (pvpDamage.isPresent()) {
                        baseDamage = RangeNumberValue.applyAsDouble(baseDamage, pvpDamage.get(), random);
                    }
                    Optional<List<NumberValue<Double>>> pvpDefense = DefaultStats.PVP_DEFENSE.tryApply(target, event);
                    if (pvpDefense.isPresent()) {
                        baseDefense = RangeNumberValue.applyAsDouble(baseDefense, pvpDefense.get(), random);
                    }
                } else {
                    Optional<List<NumberValue<Double>>> pveDamage = DefaultStats.PVE_DAMAGE.tryApply(source, event);
                    if (pveDamage.isPresent()) {
                        baseDamage = RangeNumberValue.applyAsDouble(baseDamage, pveDamage.get(), random);
                    }
                    Optional<List<NumberValue<Double>>> pveDefense = DefaultStats.PVE_DEFENSE.tryApply(target, event);
                    if (pveDefense.isPresent()) {
                        baseDefense = RangeNumberValue.applyAsDouble(baseDefense, pveDefense.get(), random);
                    }
                }
            }
            if (damager instanceof Projectile) {
                Optional<List<NumberValue<Double>>> rangeDamage = DefaultStats.RANGE_DAMAGE.tryApply(source, event);
                if (rangeDamage.isPresent()) {
                    baseDamage = RangeNumberValue.applyAsDouble(baseDamage, rangeDamage.get(), random);
                }
                Optional<List<NumberValue<Double>>> rangeDefense = DefaultStats.RANGE_DEFENSE.tryApply(target, event);
                if (rangeDefense.isPresent()) {
                    baseDefense = RangeNumberValue.applyAsDouble(baseDefense, rangeDefense.get(), random);
                }
            }
        }
        double finalDamage = baseDamage - baseDefense;
        Optional<StatsNumber<Double>> realDamage = DefaultStats.REAL_DAMAGE.tryApply(source, event);
        if (realDamage.isPresent()) {
            finalDamage = Math.max(finalDamage, realDamage.get().applyDouble(0));
        }
        return finalDamage;
    }
}
