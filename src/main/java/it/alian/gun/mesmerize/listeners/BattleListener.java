package it.alian.gun.mesmerize.listeners;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import it.alian.gun.mesmerize.MConfig;
import it.alian.gun.mesmerize.MTasks;
import it.alian.gun.mesmerize.Mesmerize;
import it.alian.gun.mesmerize.compat.AttackDamage;
import it.alian.gun.mesmerize.compat.AttackSpeed;
import it.alian.gun.mesmerize.compat.Compat;
import it.alian.gun.mesmerize.compat.ShieldBlocking;
import it.alian.gun.mesmerize.compat.hook.MesmerizeHolograph;
import it.alian.gun.mesmerize.lore.LoreCalculator;
import it.alian.gun.mesmerize.lore.LoreInfo;
import it.alian.gun.mesmerize.lore.LoreParser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.*;

public class BattleListener implements Listener {

    @EventHandler
    public void onAttackRange(PlayerInteractEvent event) {
        long nano = System.nanoTime();
        performRangeAttack(event);
        if (MConfig.debug)
            System.out.println(event.getEventName() + " processed in " + (System.nanoTime() - nano) * 1E-6 + " ms.");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onAttack(EntityDamageByEntityEvent event) {
        long nano = System.nanoTime();
        performAttack(event);
        if (MConfig.debug)
            System.out.println(event.getEventName() + " processed in " + (System.nanoTime() - nano) * 1E-6 + " ms.");
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        long nano = System.nanoTime();
        if (event.getRightClicked() instanceof Player && ShieldBlocking.check((Player) event.getRightClicked())) {
            Player other = (Player) event.getRightClicked();
            EntityDamageByEntityEvent e = new EntityDamageByEntityEvent(event.getPlayer(),
                    event.getRightClicked(), EntityDamageEvent.DamageCause.ENTITY_ATTACK,
                    new EnumMap<>(ImmutableMap.of(EntityDamageEvent.DamageModifier.BASE, AttackDamage.getAttackSpeed(other.getEquipment().getItemInHand()))),
                    new EnumMap<>(ImmutableMap.of(EntityDamageEvent.DamageModifier.BASE, Functions.constant(0.0))));
            performAttack(e);
            other.setLastDamageCause(e);
            other.setLastDamage(e.getDamage());
            other.setHealth(it.alian.gun.mesmerize.util.Math.constraint(other.getMaxHealth(), 0,
                    other.getHealth() - e.getDamage()));
            Vector vector = event.getPlayer().getLocation().getDirection().normalize().multiply(0.3).setY(0.2);
            other.setVelocity(vector);
        }
        if (MConfig.debug)
            System.out.println(event.getEventName() + " processed in " + (System.nanoTime() - nano) * 1E-6 + " ms.");
    }

    private static void performRangeAttack(PlayerInteractEvent event) {
        if (MConfig.Performance.enableLongerRange && event.getAction() == Action.LEFT_CLICK_AIR) {
            if (event.getPlayer() != null && AttackSpeed.check(event.getPlayer()))
                return;
            if (event.getPlayer().getEquipment().getItemInHand() == null || event.getPlayer().getEquipment().getItemInHand().getType() == Material.AIR ||
                    !event.getPlayer().getEquipment().getItemInHand().hasItemMeta() || !event.getPlayer().getEquipment().getItemInHand().getItemMeta().hasLore())
                return;
            Map<Integer, Location> map = new HashMap<>();
            for (LivingEntity entity : event.getPlayer().getWorld().getLivingEntities()) {
                map.put(entity.getEntityId(), entity.getEyeLocation());
            }
            int playerId = event.getPlayer().getEntityId();
            World world = event.getPlayer().getWorld();
            Location source = event.getPlayer().getEyeLocation().clone();
            MTasks.execute(() -> {
                LoreInfo info = LoreParser.getByEntityId(playerId);
                List<Integer> list = new ArrayList<>();
                for (Map.Entry<Integer, Location> entry : map.entrySet()) {
                    Location target = entry.getValue();
                    if ((target.distance(source) < Math.min(MConfig.Performance.maxAttackRange, info.getAttackRange() + MConfig.General.baseAttackRange)) &&
                            (new Vector(target.getX() - source.getX(), target.getY() - source.getY(),
                                    target.getZ() - source.getZ()).angle(source.getDirection()) < (Math.PI / 48D))) {
                        list.add(entry.getKey());
                    }
                }
                list.sort(Comparator.comparing(integer -> map.get(integer).distanceSquared(source)));
                list.stream().findFirst().ifPresent(integer -> MTasks.runLater(() -> {
                    LivingEntity other = ((LivingEntity) Compat.getByEntityId(integer, world));
                    Player player = Objects.requireNonNull((Player) Compat.getByEntityId(playerId, world));
                    if (other != null && !other.hasMetadata("NPC") && player.hasLineOfSight(other) &&
                            MesmerizeHolograph.isHolographEntity(other)) {
                        EntityDamageByEntityEvent e = new EntityDamageByEntityEvent(player,
                                Compat.getByEntityId(integer, world), EntityDamageEvent.DamageCause.ENTITY_ATTACK,
                                new EnumMap<>(ImmutableMap.of(EntityDamageEvent.DamageModifier.BASE, 1.0)),
                                new EnumMap<>(ImmutableMap.of(EntityDamageEvent.DamageModifier.BASE, Functions.constant(0.0))));
                        Bukkit.getPluginManager().callEvent(e);
                        if (!e.isCancelled()) {
                            other.setLastDamageCause(e);
                            other.setLastDamage(e.getDamage());
                            other.setHealth(it.alian.gun.mesmerize.util.Math.constraint(other.getMaxHealth(), 0,
                                    other.getHealth() - e.getDamage()));
                            Vector vector = player.getLocation().getDirection().normalize().multiply(0.3).setY(0.2);
                            other.setVelocity(vector);
                        }
                    }
                }));
            });
        }
    }

    private static void performAttack(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof LivingEntity) {
            if (event.getEntity().hasMetadata("NPC"))
                return;
            LivingEntity entity = (LivingEntity) event.getEntity();
            LivingEntity source = null;
            boolean bow = false;
            if (event.getDamager() instanceof LivingEntity)
                source = ((LivingEntity) event.getDamager());
            if (event.getDamager() instanceof Projectile
                    && ((Projectile) event.getDamager()).getShooter() instanceof LivingEntity) {
                source = (LivingEntity) ((Projectile) event.getDamager()).getShooter();
                bow = true;
            }
            if (source == null)
                return;
            if (source instanceof Player && AttackSpeed.check((Player) source))
                return;
            if (MesmerizeHolograph.isHolographEntity(entity))
                return;
            // 攻击
            LoreInfo[] info = new LoreInfo[]{LoreParser.getByEntityId(source.getEntityId()),
                    LoreParser.getByEntityId(entity.getEntityId())};
            // 攻击范围
            if ((info[0].getAttackRange() + MConfig.General.baseAttackRange) * (info[0].getAttackRange() + MConfig.General.baseAttackRange)
                    < source.getLocation().distanceSquared(entity.getLocation())) {
                event.setCancelled(true);
                return;
            }
            // 命中及闪避
            if (Math.random() > (MConfig.General.baseAccuracy + info[0].getAccuracy() - MConfig.General.baseDodge - info[1].getDodge())) {
                event.setCancelled(true);
                if (MConfig.CombatMessage.showOnMiss) {
                    LivingEntity finalSource = source;
                    MTasks.execute(() -> {
                        if (entity instanceof Player)
                            finalSource.sendMessage(String.format(MConfig.CombatMessage.onMiss, ((Player) entity).getDisplayName()));
                        else
                            finalSource.sendMessage(String.format(MConfig.CombatMessage.onMiss, entity.getName()));
                    });
                }
                if (MConfig.CombatMessage.showOnDodge) {
                    LivingEntity finalSource = source;
                    MTasks.execute(() -> {
                        if (finalSource instanceof Player)
                            entity.sendMessage(String.format(MConfig.CombatMessage.onDodge, ((Player) finalSource).getDisplayName()));
                        else
                            entity.sendMessage(String.format(MConfig.CombatMessage.onDodge, finalSource.getName()));
                    });
                }
                return;
            }
            // 会心一击
            if (Math.random() < info[0].getSuddenDeath()) {
                event.setDamage(entity.getHealth());
                if (MConfig.CombatMessage.showOnSuddenDeath) {
                    LivingEntity finalSource = source;
                    MTasks.execute(() -> {
                        if (entity instanceof Player)
                            finalSource.sendMessage(String.format(MConfig.CombatMessage.onSuddenDeath, ((Player) entity).getDisplayName()
                                    , event.getDamage()));
                        else
                            finalSource.sendMessage(String.format(MConfig.CombatMessage.onSuddenDeath, entity.getName()
                                    , event.getDamage()));
                    });
                }
            } else {
                event.setDamage(LoreCalculator.finalDamage(event.getDamage(), info[0], info[1], source, entity, bow));
            }
            if (MConfig.CombatMessage.showOnDamage) {
                LivingEntity finalSource = source;
                MTasks.execute(() -> {
                    if (entity instanceof Player)
                        finalSource.sendMessage(String.format(MConfig.CombatMessage.onDamage, ((Player) entity).getDisplayName()
                                , event.getDamage()));
                    else
                        finalSource.sendMessage(String.format(MConfig.CombatMessage.onDamage, entity.getName()
                                , event.getDamage()));
                });
            }
            // 反弹
            {
                double health = source.getHealth(), prev = health;
                health = health - LoreCalculator.finalReflect(event.getDamage(), info[1]);
                if (health < 0) health = 0;
                source.setHealth(health);
                if (MConfig.CombatMessage.showOnReflect && (prev - health) > 1E-6) {
                    LivingEntity finalSource = source;
                    double finalHealth = health;
                    MTasks.execute(() -> {
                        if (finalSource instanceof Player) {
                            entity.sendMessage(String.format(MConfig.CombatMessage.onReflect, prev - finalHealth,
                                    ((Player) finalSource).getDisplayName()));
                        } else {
                            entity.sendMessage(String.format(MConfig.CombatMessage.onReflect, prev - finalHealth,
                                    finalSource.getName()));
                        }
                    });
                }
            }
            // 吸血
            {
                double health = source.getHealth(), prev = health;
                health += info[0].getLifeSteal() * event.getDamage();
                if (health > source.getMaxHealth())
                    health = source.getMaxHealth();
                source.setHealth(health);
                if (MConfig.CombatMessage.showOnLifeSteal && (health - prev) > 1E-6) {
                    LivingEntity finalSource = source;
                    double finalHealth = health;
                    MTasks.execute(() -> {
                        if (entity instanceof Player)
                            finalSource.sendMessage(String.format(MConfig.CombatMessage.onLifeSteal, ((Player) entity).getDisplayName(),
                                    finalHealth - prev));
                        else
                            finalSource.sendMessage(String.format(MConfig.CombatMessage.onLifeSteal, entity.getName(),
                                    finalHealth - prev));
                    });
                }
            }
        }
    }

    public static void init() {
        Bukkit.getPluginManager().registerEvents(new BattleListener(), Mesmerize.instance);
    }

}
