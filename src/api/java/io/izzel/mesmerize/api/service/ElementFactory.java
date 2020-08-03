package io.izzel.mesmerize.api.service;

import io.izzel.mesmerize.api.data.StatsNumber;
import io.izzel.mesmerize.api.display.DisplayPane;
import io.izzel.mesmerize.api.display.Element;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public interface ElementFactory {

    DisplayPane createDisplayPane();

    Element createLocaleElement(String node, Object... args);

    Element namedValue(Element name, Element value);

    Element createNumberElement(Number number);

    Element createRelativeElement(StatsNumber<?> number);

    Element createRangeNumber(@NotNull Number first, @NotNull Number second);

    Element createRangeRelative(StatsNumber<?> first, StatsNumber<?> second);

    Element createDurationElement(Duration duration);

    static ElementFactory instance() {
        return StatsService.instance().getElementFactory();
    }
}
