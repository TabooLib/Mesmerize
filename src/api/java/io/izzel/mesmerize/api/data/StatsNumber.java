package io.izzel.mesmerize.api.data;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Primitives;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

public class StatsNumber<T extends Number> {

    private final List<StatsNumber<T>> operations;
    private Class<T> valueType;
    private T absolutePart;
    private double relativePart;

    StatsNumber(Class<T> valueType, T absolutePart, double relativePart) {
        Preconditions.checkArgument(valueType.isPrimitive());
        this.valueType = valueType;
        this.absolutePart = absolutePart;
        this.relativePart = relativePart;
        this.operations = ImmutableList.of(this);
    }

    StatsNumber(Class<T> valueType, T absolutePart, double relativePart, List<StatsNumber<T>> operations) {
        Preconditions.checkArgument(valueType.isPrimitive());
        this.valueType = valueType;
        this.absolutePart = absolutePart;
        this.relativePart = relativePart;
        this.operations = operations;
    }

    public Class<T> getValueType() {
        return valueType;
    }

    public void setValueType(Class<T> valueType) {
        valueType = Primitives.unwrap(valueType);
        Preconditions.checkArgument(valueType.isPrimitive());
        this.valueType = valueType;
    }

    public T getAbsolutePart() {
        return absolutePart;
    }

    public void setAbsolutePart(T absolutePart) {
        this.absolutePart = absolutePart;
    }

    public double getRelativePart() {
        return relativePart;
    }

    public void setRelativePart(double relativePart) {
        this.relativePart = relativePart;
    }

    public boolean hasAbsolutePart() {
        return this.absolutePart != null;
    }

    public boolean hasRelativePart() {
        return Math.abs(this.relativePart) >= NumberValue.DBL_EPSILON;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatsNumber<?> that = (StatsNumber<?>) o;
        return Double.compare(that.relativePart, relativePart) == 0 &&
            Objects.equals(valueType, that.valueType) &&
            Objects.equals(absolutePart, that.absolutePart);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valueType, absolutePart, relativePart);
    }

    @Override
    public String toString() {
        return valueType.getName() + "[" + absolutePart + ", " + relativePart + "%]";
    }

    public int applyInt(int i) {
        return accept(i, (x, y) -> x.intValue() + y.intValue()).intValue();
    }

    public long applyLong(long l) {
        return accept(l, (x, y) -> x.longValue() + y.longValue()).longValue();
    }

    public float applyFloat(float f) {
        return accept(f, (x, y) -> x.floatValue() + y.floatValue()).floatValue();
    }

    public double applyDouble(double d) {
        for (StatsNumber<T> operation : this.operations) {
            if (operation.absolutePart != null) {
                d += operation.absolutePart.doubleValue();
            } else {
                d += d * (operation.relativePart / 100D);
            }
        }
        return d;
    }

    public double applyNegative(double d) {
        for (StatsNumber<T> operation : this.operations) {
            if (operation.absolutePart != null) {
                d -= operation.absolutePart.doubleValue();
            } else {
                d -= d * (operation.relativePart / 100D);
            }
        }
        return d;
    }

    @SuppressWarnings("unchecked")
    public T apply(T origin) {
        if (valueType == int.class) {
            return (T) Integer.valueOf(applyInt(origin.intValue()));
        } else if (valueType == long.class) {
            return (T) Long.valueOf(applyLong(origin.longValue()));
        } else if (valueType == float.class) {
            return (T) Float.valueOf(applyFloat(origin.floatValue()));
        } else {
            return (T) Double.valueOf(applyDouble(origin.doubleValue()));
        }
    }

    private Number accept(Number n, BiFunction<Number, Number, Number> abs) {
        for (StatsNumber<T> operation : this.operations) {
            if (operation.absolutePart != null) {
                n = abs.apply(n, operation.absolutePart);
            } else {
                n = abs.apply(n, n.doubleValue() * (operation.relativePart / 100D));
            }
        }
        return n;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Number> StatsNumber<T> ofAbsolute(T a) {
        return new StatsNumber<>((Class<T>) Primitives.unwrap(a.getClass()), a, 0D);
    }

    public static <T extends Number> StatsNumber<T> ofRelative(Class<T> valueType, double rel) {
        return new StatsNumber<>(Primitives.unwrap(valueType), null, rel);
    }

    public static <T extends Number> StatsNumber<T> of(Class<T> valueType) {
        return new StatsNumber<>(Primitives.unwrap(valueType), null, 0D);
    }

    public static <T extends Number> StatsNumber<T> merge(StatsNumber<T> a, StatsNumber<T> b) {
        Preconditions.checkArgument(a.valueType == b.valueType);
        if (a.valueType == int.class) {
            return merge(a, b, (x, y) -> x.intValue() + y.intValue());
        } else if (a.valueType == long.class) {
            return merge(a, b, (x, y) -> x.longValue() + y.longValue());
        } else if (a.valueType == float.class) {
            return merge(a, b, (x, y) -> x.floatValue() + y.floatValue());
        } else {
            return merge(a, b, (x, y) -> x.doubleValue() + y.doubleValue());
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Number> StatsNumber<T> merge(StatsNumber<T> a, StatsNumber<T> b, BiFunction<T, T, Number> absMerger) {
        T abs;
        if (a.absolutePart == null) {
            abs = b.absolutePart;
        } else if (b.absolutePart == null) {
            abs = a.absolutePart;
        } else {
            abs = (T) absMerger.apply(a.absolutePart, b.absolutePart);
        }
        double rel = (a.relativePart + 100D) * (b.relativePart + 100D) - 100D;
        return new StatsNumber<>(a.valueType, abs, rel, ImmutableList.<StatsNumber<T>>builder().addAll(a.operations).addAll(b.operations).build());
    }
}
