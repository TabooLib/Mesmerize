package io.izzel.mesmerize.impl.service;

import io.izzel.mesmerize.api.data.NumberValue;
import io.izzel.mesmerize.api.data.StatsNumber;
import io.izzel.mesmerize.api.display.DisplayPane;
import io.izzel.mesmerize.api.display.Element;
import io.izzel.mesmerize.api.service.ElementFactory;
import io.izzel.mesmerize.api.service.StatsService;
import io.izzel.mesmerize.api.visitor.StatsHolder;
import io.izzel.mesmerize.api.visitor.VisitMode;
import io.izzel.mesmerize.impl.config.spec.ConfigSpec;
import io.izzel.mesmerize.impl.element.DisplayVisitor;
import io.izzel.mesmerize.impl.element.SimplePane;
import io.izzel.taboolib.module.locale.TLocale;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;

public class SimpleElementFactory implements ElementFactory {

    private final DecimalFormat decimalFormat = new DecimalFormat(ConfigSpec.spec().displaySetting().decimalFormat());

    @Override
    public void updateLore(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            StatsHolder statsHolder = StatsService.instance().newStatsHolder(itemMeta);
            SimplePane displayPane = createDisplayPane();
            displayHolder(statsHolder, displayPane);
            itemMeta.setLore(displayPane.toLore());
            itemStack.setItemMeta(itemMeta);
        }
    }

    @Override
    public void displayHolder(StatsHolder holder, DisplayPane pane) {
        holder.accept(new DisplayVisitor(pane), VisitMode.DATA);
    }

    @Override
    public SimplePane createDisplayPane() {
        return new SimplePane(ConfigSpec.spec().displaySetting().clone(), 0);
    }

    @Override
    public Element createLocaleElement(String node, Object... args) {
        return Element.of(TLocale.asString(node, args));
    }

    @Override
    public Element createNumberElement(Number number) {
        return Element.of(decimalFormat.format(number));
    }

    @Override
    public Element createRelativeElement(StatsNumber<?> number) {
        return Element.of(decimalFormat.format(number.getRelativePart()) + "%");
    }

    @Override
    public Element createRangeNumber(@NotNull Number first, @NotNull Number second) {
        if (Math.abs(first.doubleValue() - second.doubleValue()) < NumberValue.DBL_EPSILON) {
            return createNumberElement(first);
        } else {
            return Element.of(TLocale.asString("format.range", createNumberElement(first), createNumberElement(second)));
        }
    }

    @Override
    public Element createRangeRelative(StatsNumber<?> first, StatsNumber<?> second) {
        if (Math.abs(first.getRelativePart() - second.getRelativePart()) < NumberValue.DBL_EPSILON) {
            return createRelativeElement(first);
        } else {
            return Element.of(TLocale.asString("format.range", createRelativeElement(first), createRelativeElement(second)));
        }
    }

    @Override
    public Element createDurationElement(Duration duration) {
        // todo
        return Element.of(duration.toString());
    }

    @Override
    public Element createDateElement(Instant instant) {
        // todo
        return Element.of(instant.toString());
    }
}
