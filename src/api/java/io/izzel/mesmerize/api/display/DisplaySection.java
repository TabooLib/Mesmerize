package io.izzel.mesmerize.api.display;

import java.util.List;

public interface DisplaySection extends Element {

    String getId();

    void setTitle(Element element);

    void setTitlePadding(Element leftPrefix, Element left, Element right, Element rightSuffix);

    default void setTitlePadding(Element element) {
        setTitlePadding(element, element);
    }

    default void setTitlePadding(Element left, Element right) {
        setTitlePadding(null, left, right, null);
    }

    List<Element> getElements();

    void addElement(Element element);
}
