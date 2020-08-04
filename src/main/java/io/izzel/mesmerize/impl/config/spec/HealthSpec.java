package io.izzel.mesmerize.impl.config.spec;

public class HealthSpec {

    private boolean enableHealthControl = true;

    private boolean healthScaled = true;

    private double heathScale = 20.0D;

    private double minimalHealth = 1.0D;

    private double maximumHealth = Integer.MAX_VALUE;

    private long regenerationTicks = 20L;

    public boolean enableHealthControl() {
        return enableHealthControl;
    }

    public void setEnableHealthControl(boolean enableHealthControl) {
        this.enableHealthControl = enableHealthControl;
    }

    public boolean healthScaled() {
        return healthScaled;
    }

    public void setHealthScaled(boolean healthScaled) {
        this.healthScaled = healthScaled;
    }

    public double heathScale() {
        return heathScale;
    }

    public void setHeathScale(double heathScale) {
        this.heathScale = heathScale;
    }

    public double minimalHealth() {
        return minimalHealth;
    }

    public void setMinimalHealth(double minimalHealth) {
        this.minimalHealth = minimalHealth;
    }

    public double maximumHealth() {
        return maximumHealth;
    }

    public void setMaximumHealth(double maximumHealth) {
        this.maximumHealth = maximumHealth;
    }

    public long regenerationTicks() {
        return regenerationTicks;
    }

    public void setRegenerationTicks(long regenerationTicks) {
        this.regenerationTicks = regenerationTicks;
    }

    @Override
    public String toString() {
        return "HealthSpec{" +
            "enableHealthControl=" + enableHealthControl +
            ", healthScaled=" + healthScaled +
            ", heathScale=" + heathScale +
            ", minimalHealth=" + minimalHealth +
            ", maximumHealth=" + maximumHealth +
            ", regenerationTicks=" + regenerationTicks +
            '}';
    }
}
