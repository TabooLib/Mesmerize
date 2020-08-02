package io.izzel.mesmerize.api.data;

import com.google.common.primitives.Primitives;
import io.izzel.mesmerize.api.visitor.MapVisitor;
import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.VisitMode;
import io.izzel.mesmerize.api.visitor.impl.AbstractMapVisitor;
import io.izzel.mesmerize.api.visitor.impl.AbstractValue;
import io.izzel.mesmerize.api.visitor.impl.AbstractValueVisitor;
import org.jetbrains.annotations.Contract;

import java.util.function.BiFunction;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class NumberValue<T extends Number> extends AbstractValue<StatsNumber<T>> {

    public static final double DBL_EPSILON = Double.longBitsToDouble(0x3cb0000000000000L);

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
            if (Math.abs(number.getRelativePart()) >= DBL_EPSILON) {
                MapVisitor mapVisitor = visitor.visitMap();
                ValueVisitor valueVisitor = mapVisitor.visit("abs");
                acceptByType(valueVisitor);
                valueVisitor.visitEnd();
                ValueVisitor rel = mapVisitor.visit("rel");
                rel.visitString(number.getRelativePart() + "%");
                rel.visitEnd();
                mapVisitor.visitEnd();
            } else {
                acceptByType(visitor);
            }
        } else {
            visitor.visitString(number.getRelativePart() + "%");
        }
        visitor.visitEnd();
    }

    private void acceptByType(ValueVisitor visitor) {
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
    public MapVisitor visitMap() {
        return new AbstractMapVisitor(null) {
            @Override
            public ValueVisitor visit(String key) {
                if (key.equals("abs")) {
                    return new AbstractValueVisitor(null) {
                        @Override
                        public void visitInt(int i) {
                            NumberValue.this.visitInt(i);
                        }

                        @Override
                        public void visitLong(long l) {
                            NumberValue.this.visitLong(l);
                        }

                        @Override
                        public void visitFloat(float f) {
                            NumberValue.this.visitFloat(f);
                        }

                        @Override
                        public void visitDouble(double d) {
                            NumberValue.this.visitDouble(d);
                        }
                    };
                } else if (key.equals("rel")) {
                    return new AbstractValueVisitor(null) {
                        @Override
                        public void visitString(String s) {
                            NumberValue.this.visitString(s);
                        }
                    };
                } else {
                    return AbstractValueVisitor.EMPTY;
                }
            }
        };
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
