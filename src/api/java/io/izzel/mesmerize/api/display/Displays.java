package io.izzel.mesmerize.api.display;

import io.izzel.mesmerize.api.service.ElementFactory;
import io.izzel.mesmerize.api.visitor.StatsValue;

import java.time.Duration;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class Displays {

    public static final Duration TICK = Duration.ofMillis(50);

    @SafeVarargs
    public static <V extends StatsValue<?>> BiConsumer<V, DisplayPane> translating(String key, Function<V, Object>... params) {
        return (value, pane) -> {
            Object[] objects = new Object[params.length];
            int i = 0;
            for (Function<V, Object> param : params) {
                objects[i++] = param.apply(value);
            }
            pane.addElement(ElementFactory.instance().createLocaleElement(key, objects));
        };
    }

    private Displays() {
    }
}
