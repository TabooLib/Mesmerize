package io.izzel.mesmerize.impl.util;

import io.izzel.mesmerize.api.display.Element;
import io.izzel.taboolib.module.locale.TLocale;

public class TLocaleElement implements Element {

    private final String node;
    private final Object[] args;

    public TLocaleElement(String node, Object[] args) {
        this.node = node;
        this.args = args;
    }

    @Override
    public String toString() {
        return TLocale.asString(node, args);
    }
}
