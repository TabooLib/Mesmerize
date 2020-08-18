package io.izzel.mesmerize.impl.element;

import io.izzel.mesmerize.api.display.DisplaySection;
import io.izzel.mesmerize.api.display.Element;

import java.util.ArrayList;
import java.util.List;

public class SimpleSection implements DisplaySection {

    private final String id;

    private final List<Element> elements = new ArrayList<>();
    private Element leftPrefix, left, title, right, rightSuffix;

    public SimpleSection(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setTitle(Element element) {
        this.title = element;
    }

    @Override
    public void setTitlePadding(Element leftPrefix, Element left, Element right, Element rightSuffix) {
        this.leftPrefix = leftPrefix;
        this.left = left;
        this.right = right;
        this.rightSuffix = rightSuffix;
    }

    @Override
    public List<Element> getElements() {
        return elements;
    }

    @Override
    public void addElement(Element element) {
        elements.add(element);
    }

    @Override
    public String toString() {
        if (elements.isEmpty()) return "";

        // todo
        StringBuilder builder = new StringBuilder();
        if (title != null) {
            if (leftPrefix != null) builder.append(leftPrefix);
            if (left != null) builder.append(left);
            builder.append(title);
            if (right != null) builder.append(right);
            if (rightSuffix != null) builder.append(rightSuffix);
            builder.append("\n");
        }
        for (Element element : elements) {
            builder.append(element).append("\n");
        }
        return builder.toString().trim();
    }
}
