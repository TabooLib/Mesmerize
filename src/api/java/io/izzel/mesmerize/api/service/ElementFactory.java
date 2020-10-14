package io.izzel.mesmerize.api.service;

import io.izzel.mesmerize.api.data.StatsNumber;
import io.izzel.mesmerize.api.display.DisplayPane;
import io.izzel.mesmerize.api.display.Element;
import io.izzel.mesmerize.api.visitor.StatsHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;

public interface ElementFactory {

    void updateLore(ItemStack itemStack);

    void displayHolder(StatsHolder holder, DisplayPane pane);

    DisplayPane createDisplayPane();

    Element createLocaleElement(String node, Object... args);

    Element createNumberElement(Number number);

    Element createRelativeElement(StatsNumber<?> number);

    Element createRangeNumber(@NotNull Number first, @NotNull Number second);

    Element createRangeRelative(StatsNumber<?> first, StatsNumber<?> second);

    Element createDurationElement(Duration duration);

    Element createDateElement(Instant instant);

    Element createPotionElement(PotionEffectType potionEffectType);

    static ElementFactory instance() {
        return StatsService.instance().getElementFactory();
    }
}
