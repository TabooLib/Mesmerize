package io.izzel.mesmerize.impl.config.spec;

import io.izzel.mesmerize.impl.Mesmerize;

public class ConfigSpec {

    private HealthSpec health;

    public HealthSpec health() {
        return health;
    }

    public void setHealth(HealthSpec health) {
        this.health = health;
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
