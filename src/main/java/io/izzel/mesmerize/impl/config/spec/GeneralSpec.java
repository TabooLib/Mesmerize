package io.izzel.mesmerize.impl.config.spec;

public class GeneralSpec {

    private boolean debug = false;
    private boolean updateCheck = true;
    private double defaultAttackRange = 5.0D;
    private double defaultHitChance = 1.0D;
    private boolean enableSpeedControl = true;
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

    public boolean enableSpeedControl() {
        return enableSpeedControl;
    }

    public void setEnableSpeedControl(boolean enableSpeedControl) {
        this.enableSpeedControl = enableSpeedControl;
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
