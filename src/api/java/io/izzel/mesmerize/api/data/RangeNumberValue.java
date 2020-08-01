package io.izzel.mesmerize.api.data;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class RangeNumberValue<T extends Number> extends MultiValue<StatsNumber<T>, NumberValue<T>> {

    private RangeNumberValue(Supplier<NumberValue<T>> supplier) {
        super(false, supplier);
    }

    public RangeNumberValue(Supplier<NumberValue<T>> supplier, NumberValue<T> value) {
        super(false, supplier, ImmutableList.of(value));
    }

    public RangeNumberValue(Supplier<NumberValue<T>> supplier, NumberValue<T> low, NumberValue<T> high) {
        super(false, supplier, ImmutableList.of(low, high));
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
}
