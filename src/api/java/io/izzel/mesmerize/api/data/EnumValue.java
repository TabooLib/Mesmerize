package io.izzel.mesmerize.api.data;

import com.google.common.base.Preconditions;
import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.VisitMode;
import io.izzel.mesmerize.api.visitor.impl.AbstractValue;
import org.jetbrains.annotations.Contract;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class EnumValue<E extends Enum<E>> extends AbstractValue<E> {

    private final Class<E> enumClass;
    private final Function<String, Optional<E>> matcher;
    private final boolean allowNullValue;
    private E instance;

    public EnumValue(Class<E> enumClass, Function<String, Optional<E>> matcher, boolean allowNullValue, E instance) {
        this.enumClass = enumClass;
        this.matcher = matcher;
        this.allowNullValue = allowNullValue;
        this.instance = instance;
    }

    @Override
    public E get() {
        return instance;
    }

    @Override
    public void accept(ValueVisitor visitor, VisitMode mode) {
        if (this.allowNullValue && this.instance == null) {
            visitor.visitString("<null>");
        } else {
            visitor.visitString(instance.name());
        }
        visitor.visitEnd();
    }

    public Class<E> getEnumClass() {
        return enumClass;
    }

    public boolean isAllowNullValue() {
        return allowNullValue;
    }

    @Override
    public void visitString(String s) {
        if (s == null || s.equals("<null>")) return;
        this.instance = this.matcher.apply(s).orElse(null);
    }

    @Override
    public void visitEnd() {
        if (!this.allowNullValue) {
            Preconditions.checkNotNull(this.instance, "enum");
        }
    }

    public static EnumValueBuilder<?> builder() {
        return new EnumValueBuilder<>();
    }

    public static class EnumValueBuilder<E extends Enum<E>> {

        private Class<E> cl;
        private boolean allowNull = false;
        private Function<String, Optional<E>> matcher;
        private E defaultValue;

        @SuppressWarnings("unchecked")
        @Contract("_ -> this")
        public <N_E extends Enum<N_E>> EnumValueBuilder<N_E> enumClass(Class<N_E> enumClass) {
            this.cl = (Class<E>) enumClass;
            return (EnumValueBuilder<N_E>) this;
        }

        @Contract("_ -> this")
        public EnumValueBuilder<E> optionalMatcher(Function<String, Optional<E>> matcher) {
            this.matcher = matcher;
            return this;
        }

        @Contract("_ -> this")
        public EnumValueBuilder<E> matcher(Function<String, E> matcher) {
            this.matcher = s -> {
                E e = matcher.apply(s);
                return e == null ? Optional.empty() : Optional.of(e);
            };
            return this;
        }

        @Contract("-> this")
        public EnumValueBuilder<E> allowNullValue() {
            this.allowNull = true;
            return this;
        }

        @Contract("_ -> this")
        public EnumValueBuilder<E> defaultValue(E value) {
            this.defaultValue = value;
            return this;
        }

        @Contract("-> new")
        public EnumValue<E> build() {
            Preconditions.checkNotNull(cl, "enumClass");
            Preconditions.checkNotNull(matcher, "matcher");
            return new EnumValue<>(cl, matcher, allowNull, defaultValue);
        }

        @Contract("-> new")
        public Supplier<EnumValue<E>> buildSupplier() {
            return this::build;
        }
    }
}
