package it.alian.gun.mesmerize.lore;

import com.google.gson.Gson;

import java.util.Random;
import java.util.regex.Pattern;

public class Number {

    public static void main(String[] args) {
        System.out.println(new Gson().toJson(Number.of("5-12")));
    }

    private static Random random = new Random();
    private static final Pattern rangePattern = Pattern.compile("[+\\-]?[0-9]+(\\.[0-9]+)?%?-[+\\-]?[0-9]+(\\.[0-9]+)?%?");

    public double get() {
        return 0;
    }

    public boolean chance() {
        return false;
    }

    public static Number of(String text) {
        try {
            if (isDouble(text)) {
                DoubleNumber number = new DoubleNumber();
                number.value = Double.parseDouble(text);
                return number;
            } else if (text.endsWith("%") && isDouble(text.substring(0, text.length() - 1))) {
                try {
                    PercentageNumber number = new PercentageNumber();
                    number.value = Double.parseDouble(text.substring(0, text.length() - 1)) / 100F;
                    return number;
                } catch (Exception ignored) {
                    return new Number();
                }
            } else if (rangePattern.matcher(text).matches()) {
                String[] numbers = text.split("-");
                RangeNumber number = new RangeNumber();
                if (numbers.length == 2) {
                    number.start = Number.of(numbers[0]).get();
                    number.end = Number.of(numbers[1]).get();
                }
                if (numbers.length == 3) {
                    if (numbers[0].length() == 0) {
                        number.start = 0 - Number.of(numbers[1]).get();
                        number.end = Number.of(numbers[2]).get();
                    } else {
                        number.start = Number.of(numbers[0]).get();
                        number.end = 0 - Number.of(numbers[2]).get();
                    }
                }
                if (numbers.length == 4) {
                    number.start = 0 - Number.of(numbers[1]).get();
                    number.end = 0 - Number.of(numbers[3]).get();
                }
                return number;
            } else
                return new Number();
        } catch (Exception e) {
            return new Number();
        }
    }

    private static boolean isDouble(String text) {
        try {
            Double.parseDouble(text);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static double randomBetween(double a, double b) {
        if (a < b)
            return random.nextFloat() * (b - a) + a;
        else
            return random.nextFloat() * (a - b) + b;
    }

    public static class PercentageNumber extends Number {

        private double value;

        @Override
        public double get() {
            return value;
        }

        @Override
        public boolean chance() {
            return random.nextFloat() < value;
        }
    }

    public static class RangeNumber extends Number {

        private double start, end;

        @Override
        public double get() {
            return Number.randomBetween(start, end);
        }

        @Override
        public boolean chance() {
            return random.nextFloat() < Number.randomBetween(start, end);
        }
    }

    public static class DoubleNumber extends Number {

        private double value;

        @Override
        public double get() {
            return value;
        }

        @Override
        public boolean chance() {
            return random.nextFloat() < value;
        }
    }

}
