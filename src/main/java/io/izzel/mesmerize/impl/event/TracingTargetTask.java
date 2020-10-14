package io.izzel.mesmerize.impl.event;

import io.izzel.taboolib.util.Quat;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class TracingTargetTask extends BukkitRunnable {

    private final Projectile projectile;
    private final LivingEntity target;
    private final double tracingRadians;

    public TracingTargetTask(Projectile projectile, LivingEntity target, double tracing) {
        this.projectile = projectile;
        this.target = target;
        this.tracingRadians = Math.toRadians(tracing);
    }

    @Override
    public void run() {
        if (target == null || target.isDead() || !target.isValid()
            || projectile.isDead() || !projectile.isValid() || projectile.isOnGround()) {
            this.cancel();
            return;
        }
        Vector velocity = projectile.getVelocity();
        Vector direction = target.getEyeLocation().subtract(projectile.getLocation()).toVector();
        if (Math.abs(velocity.angle(direction)) >= tracingRadians) {
            Vector rotate = Quat.radiansAxis(tracingRadians, velocity.getCrossProduct(direction)).rotate(velocity);
            projectile.setVelocity(rotate);
        }
    }
}
