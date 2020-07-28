package io.izzel.mesmerize.api.visitor.util;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import io.izzel.mesmerize.api.visitor.InfoKey;
import io.izzel.mesmerize.api.visitor.InfoVisitor;
import org.jetbrains.annotations.NotNull;

public class MultimapInfoWriter implements InfoVisitor {

    private final ListMultimap<InfoKey<?>, ?> multimap;

    public MultimapInfoWriter(ListMultimap<InfoKey<?>, ?> multimap) {
        this.multimap = multimap;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public <T> void visitInfo(InfoKey<T> key, @NotNull T value) {
        ((Multimap) this.multimap).put(key, value);
    }

    @Override
    public void visitEnd() {
    }
}
