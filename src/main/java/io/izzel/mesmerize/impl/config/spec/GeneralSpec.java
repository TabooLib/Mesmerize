package io.izzel.mesmerize.impl.config.spec;

public class GeneralSpec {

    private boolean debug = false;
    private boolean updateCheck = true;
    private long attributeApplyInterval = 20L;
    private double defaultAttackRange = 5.0D;
    private double defaultHitChance = 1.0D;
    private double defaultHealth = 20.0D;
    private double defaultAttackSpeed = 4.0D;
    private double defaultMoveSpeed = 0.1D;
    private double defaultFlySpeed = 0.2D;

    public boolean debug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean updateCheck() {
        return updateCheck;
    }

    public void setUpdateCheck(boolean updateCheck) {
        this.updateCheck = updateCheck;
    }

    public long attributeApplyInterval() {
        return attributeApplyInterval;
    }

    public void setAttributeApplyInterval(long attributeApplyInterval) {
        this.attributeApplyInterval = attributeApplyInterval;
    }

    public double defaultAttackRange() {
        return defaultAttackRange;
    }

    public void setDefaultAttackRange(double defaultAttackRange) {
        this.defaultAttackRange = defaultAttackRange;
    }

    public double defaultHitChance() {
        return defaultHitChance;
    }

    public void setDefaultHitChance(double defaultHitChance) {
        this.defaultHitChance = defaultHitChance;
    }

    public double defaultHealth() {
        return defaultHealth;
    }

    public void setDefaultHealth(double defaultHealth) {
        this.defaultHealth = defaultHealth;
    }

    public double defaultAttackSpeed() {
        return defaultAttackSpeed;
    }

    public void setDefaultAttackSpeed(double defaultAttackSpeed) {
        this.defaultAttackSpeed = defaultAttackSpeed;
    }

    public double defaultMoveSpeed() {
        return defaultMoveSpeed;
    }

    public void setDefaultMoveSpeed(double defaultMoveSpeed) {
        this.defaultMoveSpeed = defaultMoveSpeed;
    }

    public double defaultFlySpeed() {
        return defaultFlySpeed;
    }

    public void setDefaultFlySpeed(double defaultFlySpeed) {
        this.defaultFlySpeed = defaultFlySpeed;
    }
}
