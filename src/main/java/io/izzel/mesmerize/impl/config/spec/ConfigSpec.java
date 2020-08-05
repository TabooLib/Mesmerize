package io.izzel.mesmerize.impl.config.spec;

import io.izzel.mesmerize.impl.Mesmerize;

public class ConfigSpec {

    private HealthSpec health;
    private GeneralSpec general;
    private PerformanceSpec performance;

    public HealthSpec health() {
        return health;
    }

    public void setHealth(HealthSpec health) {
        this.health = health;
    }

    public GeneralSpec general() {
        return general;
    }

    public void setGeneral(GeneralSpec general) {
        this.general = general;
    }

    public PerformanceSpec performance() {
        return performance;
    }

    public void setPerformance(PerformanceSpec performance) {
        this.performance = performance;
    }

    public static ConfigSpec spec() {
        return Mesmerize.instance().getConfigSpec();
    }

    @Override
    public String toString() {
        return "ConfigSpec{" +
            "health=" + health +
            '}';
    }
}
