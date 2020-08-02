package io.izzel.mesmerize.api.data;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Primitives;
import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.VisitMode;
import io.izzel.mesmerize.api.visitor.impl.AbstractValue;
import org.jetbrains.annotations.Contract;

import java.util.function.BiFunction;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class NumberValue<T extends Number> extends AbstractValue<StatsNumber<T>> {

    private final boolean allowRelative, allowDecimal, allowCoerce;

    private final StatsNumber<T> number;

    public NumberValue(StatsNumber<T> number) {
        this(true, true, true, number);
    }

    public NumberValue(boolean allowRelative, boolean allowDecimal, boolean allowCoerce, Class<T> valueType) {
        this(allowRelative, allowDecimal, allowCoerce, StatsNumber.of(valueType));
    }

    public NumberValue(boolean allowRelative, boolean allowDecimal, boolean allowCoerce, StatsNumber<T> number) {
        this.allowRelative = allowRelative;
        this.allowDecimal = allowDecimal;
        this.allowCoerce = allowCoerce;
        this.number = number;
    }

    @Override
    public StatsNumber<T> get() {
        return number;
    }

    @Override
    public void accept(ValueVisitor visitor, VisitMode mode) {
        if (number.getAbsolutePart() != null) {
            Number absolutePart = number.getAbsolutePart();
            Class<?> valueType = number.getValueType();
            if (valueType == int.class) {
                visitor.visitInt(absolutePart.intValue());
            } else if (valueType == long.class) {
                visitor.visitLong(absolutePart.longValue());
            } else if (valueType == float.class) {
                visitor.visitFloat(absolutePart.floatValue());
            } else {
                visitor.visitLong(absolutePart.longValue());
            }
        } else {
            visitor.visitString(number.getRelativePart() + "%");
        }
        visitor.visitEnd();
    }

    @Override
    public void visitInt(int i) {
        T value = (T) Integer.valueOf(i);
        if (this.number.getValueType() == int.class || this.allowCoerce) {
            this.number.setAbsolutePart(value);
        } else {
            super.visitInt(i);
        }
    }

    @Override
    public void visitLong(long l) {
        T value = (T) Long.valueOf(l);
        if (this.number.getValueType() == long.class || this.allowCoerce) {
            this.number.setAbsolutePart(value);
        } else {
            super.visitLong(l);
        }
    }

    @Override
    public void visitFloat(float f) {
        T value = (T) Float.valueOf(f);
        if (this.allowDecimal && (this.number.getValueType() == float.class || this.allowCoerce)) {
            this.number.setAbsolutePart(value);
        } else {
            super.visitFloat(f);
        }
    }

    @Override
    public void visitDouble(double d) {
        T value = (T) Double.valueOf(d);
        if (this.allowDecimal && (this.number.getValueType() == double.class || this.allowCoerce)) {
            this.number.setAbsolutePart(value);
        } else {
            super.visitDouble(d);
        }
    }

    @Override
    public void visitString(String s) {
        if (s.endsWith("%") && this.allowRelative) {
            if (this.allowDecimal) {
                this.number.setRelativePart(Double.parseDouble(s.substring(0, s.length() - 1)));
            } else {
                this.number.setRelativePart(Long.parseLong(s.substring(0, s.length() - 1)));
            }
        } else if (this.allowCoerce) {
            if (this.allowDecimal) {
                this.number.setAbsolutePart((T) Double.valueOf(s));
            } else {
                this.number.setAbsolutePart((T) Long.valueOf(s));
            }
        } else {
            super.visitString(s);
        }
    }

    @Override
    public void visitEnd() {
        Preconditions.checkNotNull(number, "empty");
    }

    public static NumberValueBuilder<?> builder() {
        return new NumberValueBuilder<>();
    }

    public static <T extends Number> BiFunction<NumberValue<T>, NumberValue<T>, NumberValue<T>> defaultMerger() {
        return (a, b) -> new NumberValue<>(StatsNumber.merge(a.number, b.number));
    }

    public static class NumberValueBuilder<T extends Number> {

        private boolean allowRelative, allowDecimal, allowCoerce = true;
        private Class<T> valueType;

        @Contract("_ -> this")
        public <N extends Number> NumberValueBuilder<N> valueType(Class<N> valueType) {
            this.valueType = (Class<T>) Primitives.unwrap(valueType);
            return (NumberValueBuilder<N>) this;
        }

        @Contract("-> this")
        public NumberValueBuilder<T> allowRelative() {
            this.allowRelative = true;
            return this;
        }

        @Contract("-> this")
        public NumberValueBuilder<T> allowDecimal() {
            this.allowDecimal = true;
            return this;
        }

        @Contract("-> this")
        public NumberValueBuilder<T> disallowCoerce() {
            this.allowCoerce = false;
            return this;
        }

        @Contract("-> new")
        public NumberValue<T> build() {
            return new NumberValue<>(this.allowRelative, this.allowDecimal, allowCoerce, valueType);
        }

        @Contract("-> new")
        public Supplier<NumberValue<T>> buildSupplier() {
            return this::build;
        }
    }
}
