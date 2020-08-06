package io.izzel.mesmerize.impl.config.spec;

public class GeneralSpec {

    private boolean debug = false;
    private boolean updateCheck = true;
    private long attributeApplyInterval = 20L;
    private double defaultAttackRange = 5.0D;
    private double defaultHitChance = 1.0D;

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
}
