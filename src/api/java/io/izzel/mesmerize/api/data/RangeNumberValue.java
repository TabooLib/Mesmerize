package io.izzel.mesmerize.api.data;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import io.izzel.mesmerize.api.display.DisplayPane;
import io.izzel.mesmerize.api.service.ElementFactory;

import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class RangeNumberValue<T extends Number> extends MultiValue<StatsNumber<T>, NumberValue<T>> {

    private RangeNumberValue(Supplier<NumberValue<T>> supplier) {
        super(true, supplier);
    }

    public RangeNumberValue(Supplier<NumberValue<T>> supplier, NumberValue<T> value) {
        super(true, supplier, ImmutableList.of(value));
    }

    public RangeNumberValue(Supplier<NumberValue<T>> supplier, NumberValue<T> low, NumberValue<T> high) {
        super(true, supplier, ImmutableList.of(low, high));
    }

    public StatsNumber<T> getLower() {
        return super.getValue(0);
    }

    public StatsNumber<T> getHigher() {
        return super.get().size() > 1 ? super.getValue(1) : super.getValue(0);
    }

    @Override
    public void visitEnd() {
        Preconditions.checkArgument(super.get().size() > 0 && super.get().size() <= 2);
    }

    public static <T extends Number> RangeNumberValue<T> of(Supplier<NumberValue<T>> supplier) {
        return new RangeNumberValue<>(supplier);
    }

    public static <T extends Number> Supplier<RangeNumberValue<T>> rangeValueSupplier(Supplier<NumberValue<T>> supplier) {
        return () -> of(supplier);
    }

    public static <T extends Number> BiFunction<RangeNumberValue<T>, RangeNumberValue<T>, RangeNumberValue<T>> sumMerger() {
        return (a, b) -> {
            List<NumberValue<T>> first = a.get();
            List<NumberValue<T>> second = b.get();
            if (first.size() == 1 && second.size() == 1) {
                return new RangeNumberValue<>(a.supplier, NumberValue.<T>defaultMerger().apply(first.get(0), second.get(0)));
            } else if (first.size() == 2 && second.size() == 2) {
                return new RangeNumberValue<>(a.supplier,
                    NumberValue.<T>defaultMerger().apply(first.get(0), second.get(0)),
                    NumberValue.<T>defaultMerger().apply(first.get(1), second.get(1))
                );
            } else if (first.size() == 1 && second.size() == 2) {
                return new RangeNumberValue<>(a.supplier,
                    NumberValue.<T>defaultMerger().apply(first.get(0), second.get(0)),
                    NumberValue.<T>defaultMerger().apply(first.get(0), second.get(1))
                );
            } else {
                return new RangeNumberValue<>(a.supplier,
                    NumberValue.<T>defaultMerger().apply(first.get(0), second.get(0)),
                    NumberValue.<T>defaultMerger().apply(first.get(1), second.get(0))
                );
            }
        };
    }

    public static <N extends Number, V extends RangeNumberValue<N>> BiConsumer<V, DisplayPane> defaultDisplay(String key) {
        return (range, pane) -> {
            ElementFactory factory = ElementFactory.instance();
            List<NumberValue<N>> values = range.get();
            if (values.size() == 1) {
                NumberValue.<N, NumberValue<N>>defaultDisplay(key).accept(values.get(0), pane);
            } else if (values.size() == 2) {
                StatsNumber<N> first = values.get(0).get();
                StatsNumber<N> second = values.get(1).get();
                if (first.hasAbsolutePart()) {
                    if (second.hasAbsolutePart()) {
                        pane.addElement(factory.createLocaleElement(key, factory.createRangeNumber(first.getAbsolutePart(), second.getAbsolutePart())));
                    } else {
                        pane.addElement(factory.createLocaleElement(key, factory.createNumberElement(first.getAbsolutePart())));
                    }
                } else if (second.hasAbsolutePart()) {
                    pane.addElement(factory.createLocaleElement(key, factory.createNumberElement(second.getAbsolutePart())));
                }
                if (first.hasRelativePart()) {
                    if (second.hasRelativePart()) {
                        pane.addElement(factory.createLocaleElement(key, factory.createRangeRelative(first, second)));
                    } else {
                        pane.addElement(factory.createLocaleElement(key, factory.createRelativeElement(first)));
                    }
                } else if (second.hasRelativePart()) {
                    pane.addElement(factory.createLocaleElement(key, factory.createRelativeElement(second)));
                }
            }
        };
    }

    public static double applyAsDouble(double d, List<NumberValue<Double>> values, Random random) {
        double value = values.get(0).get().applyDouble(d);
        if (values.size() > 1) {
            ListIterator<NumberValue<Double>> iterator = values.listIterator(1);
            while (iterator.hasNext()) {
                NumberValue<Double> next = iterator.next();
                value += random.nextDouble() * (next.get().applyDouble(d) - value);
            }
        }
        return value;
    }
}
