package io.izzel.mesmerize.api.display;

public interface Element {

    @Override
    String toString();

    default Element then(Element element) {
        return new Element() {
            @Override
            public String toString() {
                return Element.this.toString() + element.toString();
            }
        };
    }

    static Element of(String x) {
        return new Element() {
            @Override
            public String toString() {
                return x;
            }
        };
    }

    Element EMPTY = of("");
}
