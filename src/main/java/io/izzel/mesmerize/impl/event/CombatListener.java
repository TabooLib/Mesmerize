package io.izzel.mesmerize.impl.event;

import io.izzel.mesmerize.api.DefaultStats;
import io.izzel.mesmerize.api.cause.CauseManager;
import io.izzel.mesmerize.api.cause.ContextKeys;
import io.izzel.mesmerize.api.data.NumberValue;
import io.izzel.mesmerize.api.data.StatsNumber;
import io.izzel.mesmerize.api.service.StatsService;
import io.izzel.mesmerize.api.visitor.util.StatsSet;
import io.izzel.mesmerize.impl.config.spec.ConfigSpec;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Optional;
import java.util.Random;

public class CombatListener implements Listener {

    private final Random random = new Random();

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) event.getEntity();
            Entity damager = event.getDamager();
            if (entity.hasMetadata("NPC")) return;
            boolean rangeAttack = damager instanceof Projectile;
            if (rangeAttack && !(((Projectile) damager).getShooter() instanceof LivingEntity))
                return;
            if (!rangeAttack && !(damager instanceof LivingEntity))
                return;
            LivingEntity realDamager = rangeAttack ? ((LivingEntity) ((Projectile) damager).getShooter()) : (LivingEntity) damager;
            if (realDamager == null) return;
            try (CauseManager.StackFrame frame = CauseManager.instance().pushStackFrame()) {
                frame.pushContext(ContextKeys.SOURCE, realDamager);
                frame.pushContext(ContextKeys.TARGET, entity);
                StatsSet damagerSet = StatsSet.of(damager);
                StatsSet entitySet = StatsSet.of(entity);

                // ATTACK_RANGE
                if (!rangeAttack) {
                    Optional<StatsNumber<Double>> attackRange = DefaultStats.ATTACK_RANGE.tryApply(damagerSet, event);
                    if (attackRange.isPresent()) {
                        double range = attackRange.get().applyDouble(ConfigSpec.spec().general().defaultAttackRange());
                        if (range < entity.getLocation().distance(damager.getLocation())) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                }

                // ACCURACY/DODGE
                double hitChance = ConfigSpec.spec().general().defaultHitChance();
                Optional<StatsNumber<Double>> accuracy = DefaultStats.ACCURACY.tryApply(damagerSet, event);
                if (accuracy.isPresent()) {
                    hitChance = accuracy.get().applyDouble(hitChance);
                }
                Optional<StatsNumber<Double>> dodge = DefaultStats.DODGE.tryApply(entitySet, event);
                if (dodge.isPresent()) {
                    hitChance = dodge.get().applyNegative(hitChance);
                }
                if (random.nextDouble() >= hitChance) {
                    event.setCancelled(true);
                    return;
                }

                // DAMAGE/DEFENSE/CRIT
                double calculate = StatsService.instance().getDamageCalculator().calculate(event, event.getFinalDamage(), damagerSet, entitySet);
                Optional<StatsNumber<Double>> critChance = DefaultStats.CRIT_CHANCE.tryApply(damagerSet, event);
                if (critChance.isPresent()) {
                    double chance = critChance.get().applyDouble(1D) - 1D;
                    if (random.nextDouble() < chance) {
                        Optional<StatsNumber<Double>> critDamage = DefaultStats.CRIT_DAMAGE.tryApply(damagerSet, event);
                        if (critDamage.isPresent()) {
                            calculate = critDamage.get().applyDouble(calculate);
                        }
                    }
                }

                event.setDamage(calculate);

                // THORNS
                Optional<StatsNumber<Double>> thornsChance = DefaultStats.THORNS_CHANCE.tryApply(entitySet, event);
                if (thornsChance.isPresent()) {
                    double chance = thornsChance.get().applyDouble(1D) - 1D;
                    if (random.nextDouble() < chance) {
                        double thornsValue = 0;
                        Optional<StatsNumber<Double>> thorns = DefaultStats.THORNS.tryApply(entitySet, event);
                        if (thorns.isPresent()) {
                            thornsValue = thorns.get().applyDouble(thornsValue);
                        }
                        if (rangeAttack) {
                            Optional<StatsNumber<Double>> rangeThorns = DefaultStats.RANGE_THORNS.tryApply(entitySet, event);
                            if (rangeThorns.isPresent()) {
                                thornsValue = rangeThorns.get().applyDouble(thornsValue);
                            }
                        } else {
                            Optional<StatsNumber<Double>> meleeThorns = DefaultStats.MELEE_THORNS.tryApply(entitySet, event);
                            if (meleeThorns.isPresent()) {
                                thornsValue = meleeThorns.get().applyDouble(thornsValue);
                            }
                        }
                        if (thornsValue > NumberValue.DBL_EPSILON) {
                            realDamager.damage(thornsValue);
                        }
                    }
                }

                // LIFESTEAL
                Optional<StatsNumber<Double>> lifestealChance = DefaultStats.LIFESTEAL_CHANCE.tryApply(damagerSet, event);
                if (lifestealChance.isPresent()) {
                    double chance = lifestealChance.get().applyDouble(1D) - 1D;
                    if (random.nextDouble() < chance) {
                        Optional<StatsNumber<Double>> lifesteal = DefaultStats.LIFESTEAL.tryApply(damagerSet, event);
                        if (lifesteal.isPresent()) {
                            double lifestealValue = lifesteal.get().applyDouble(calculate) - calculate;
                            realDamager.setHealth(realDamager.getHealth() + lifestealValue);
                        }
                    }
                }
            }
        }
    }
}
