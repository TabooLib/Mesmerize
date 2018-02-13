package it.alian.gun.mesmerize.util;

import java.util.Random;

public class Math {

    private static Random random = new Random();

    public static double constraint(double max, double min, double value) {
        return java.lang.Math.max(min, java.lang.Math.min(max, value));
    }

    public static double min(double a, double b) {
        return java.lang.Math.min(a, b);
    }

    public static double random() {
        return random.nextDouble();
    }

}
