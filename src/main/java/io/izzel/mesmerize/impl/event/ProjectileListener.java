package io.izzel.mesmerize.impl.event;

import io.izzel.mesmerize.api.DefaultStats;
import io.izzel.mesmerize.api.cause.CauseManager;
import io.izzel.mesmerize.api.cause.ContextKeys;
import io.izzel.mesmerize.api.data.NumberValue;
import io.izzel.mesmerize.api.data.StatsNumber;
import io.izzel.mesmerize.api.service.StatsService;
import io.izzel.mesmerize.api.visitor.StatsVisitor;
import io.izzel.mesmerize.api.visitor.VisitMode;
import io.izzel.mesmerize.api.visitor.util.StatsSet;
import io.izzel.mesmerize.impl.Mesmerize;
import io.izzel.mesmerize.impl.config.spec.ConfigSpec;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.util.Comparator;
import java.util.Optional;
import java.util.Random;

public class ProjectileListener implements Listener {

    private final Random random = new Random();

    @EventHandler
    public void onRangeAttack(ProjectileLaunchEvent event) {
        ProjectileSource source = event.getEntity().getShooter();
        if (source instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) source;
            try (CauseManager.StackFrame frame = CauseManager.instance().pushStackFrame()) {
                frame.pushContext(ContextKeys.SOURCE, livingEntity);
                StatsSet statsSet = StatsSet.of(livingEntity);

                StatsVisitor writer = StatsService.instance().newPersistentWriter(event.getEntity());
                statsSet.accept(writer, VisitMode.DATA);

                Optional<StatsNumber<Double>> tracing = DefaultStats.TRACING.tryApply(statsSet, event);
                if (tracing.isPresent()) {
                    double tracingValue = tracing.get().applyDouble(0);
                    if (tracingValue > NumberValue.DBL_EPSILON) {
                        Location sourceEye = livingEntity.getEyeLocation();
                        Vector direction = sourceEye.getDirection();
                        int distance = ConfigSpec.spec().performance().maxTracingDistance();
                        double angle = Math.toRadians(ConfigSpec.spec().performance().maxTracingAngle());
                        boolean visibleCheck = ConfigSpec.spec().performance().tracingVisibleCheck();
                        livingEntity.getNearbyEntities(distance, distance, distance).stream()
                            .filter(it -> it instanceof LivingEntity && (!visibleCheck || livingEntity.hasLineOfSight(it)))
                            .filter(it -> Math.abs(((LivingEntity) it).getEyeLocation().subtract(sourceEye).toVector().angle(direction)) < angle)
                            .min(Comparator.comparingDouble(it -> Math.abs(((LivingEntity) it).getEyeLocation().subtract(sourceEye).toVector().angle(direction))))
                            .ifPresent(entity ->
                                new TracingTargetTask(event.getEntity(), (LivingEntity) entity, tracingValue).runTaskTimer(Mesmerize.instance(), 1, 1)
                            );
                    }
                }
            }
        }
    }
}
