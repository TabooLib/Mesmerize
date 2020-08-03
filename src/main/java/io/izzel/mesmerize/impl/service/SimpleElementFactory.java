package io.izzel.mesmerize.impl.service;

import io.izzel.mesmerize.api.data.StatsNumber;
import io.izzel.mesmerize.api.display.DisplayPane;
import io.izzel.mesmerize.api.display.Element;
import io.izzel.mesmerize.api.service.ElementFactory;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;

public class SimpleElementFactory implements ElementFactory {

    @Override
    public DisplayPane createDisplayPane() {
        return null;
    }

    @Override
    public Element createLocaleElement(String node, Object... args) {
        return null;
    }

    @Override
    public Element createNumberElement(Number number) {
        return null;
    }

    @Override
    public Element createRelativeElement(StatsNumber<?> number) {
        return null;
    }

    @Override
    public Element createRangeNumber(@NotNull Number first, @NotNull Number second) {
        return null;
    }

    @Override
    public Element createRangeRelative(StatsNumber<?> first, StatsNumber<?> second) {
        return null;
    }

    @Override
    public Element createDurationElement(Duration duration) {
        return null;
    }

    @Override
    public Element createDateElement(Instant instant) {
        return null;
    }
}
