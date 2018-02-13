package it.alian.gun.mesmerize.util;

import java.util.List;
import java.util.Random;

public class Collections {

    private static Random random = new Random();

    public static <T> T random(List<T> collection) {
        return collection.get(random.nextInt(collection.size()));
    }

}
