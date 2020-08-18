package io.izzel.mesmerize.impl.element;

import io.izzel.mesmerize.api.display.DisplayPane;
import io.izzel.mesmerize.api.display.DisplaySection;
import io.izzel.mesmerize.api.display.DisplaySetting;
import io.izzel.mesmerize.api.display.Element;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class SimplePane implements DisplayPane {

    private final DisplaySetting displaySetting;

    private final List<Element> headers = new ArrayList<>();
    private final List<Element> elements = new ArrayList<>();
    private final Map<String, DisplaySection> sections = new LinkedHashMap<>();
    private final int depth;
    private Element padding;

    public SimplePane(DisplaySetting displaySetting, int depth) {
        this.displaySetting = displaySetting;
        this.depth = depth;
        if (displaySetting.paddingWithRandomColor()) {
            this.padding = Element.of(displaySetting.paddingElement());
        }
    }

    @Override
    public List<Element> getHeaders() {
        return headers;
    }

    @Override
    public void addHeader(Element element) {
        headers.add(element);
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
    public DisplayPane createSubPane() {
        return new SimplePane(displaySetting, depth + 1);
    }

    @Override
    public DisplaySection createSection(String id) {
        return new SimpleSection(id);
    }

    @Override
    public DisplaySection getOrCreateSection(String id) {
        DisplaySection section = this.sections.get(id);
        return section == null ? new SimpleSection(id) : section;
    }

    @Override
    public void addSection(DisplaySection section) {
        this.sections.put(section.getId(), section);
    }

    @Override
    public @Nullable Element getPadding() {
        return padding;
    }

    @Override
    public void setPadding(Element padding) {
        this.padding = padding;
    }

    @Override
    public @NotNull DisplaySetting getDisplaySetting() {
        return displaySetting;
    }

    @Override
    public String toString() {
        if (elements.isEmpty()) return "";

        // todo
        StringBuilder builder = new StringBuilder();
        for (Element header : headers) {
            builder.append(header).append("\n");
        }
        for (Element element : elements) {
            builder.append(element).append("\n");
        }
        for (DisplaySection value : sections.values()) {
            builder.append(value).append("\n");
        }

        String result;
        if (displaySetting.paddingWithRandomColor() && this.padding != null) {
            if (depth > 0 && (depth != 1 || !displaySetting.flattenFirstPadding())) {
                String[] split = builder.toString().trim().split("\\n");
                ChatColor chatColor = ChatColor.values()[ThreadLocalRandom.current().nextInt(16)];
                StringBuilder padding = new StringBuilder();
                for (String s : split) {
                    padding.append(chatColor).append(this.padding).append(ChatColor.RESET).append(s).append("\n");
                }
                result = padding.toString().trim();
            } else {
                result = builder.toString().trim();
            }
        } else {
            result = builder.toString().trim();
        }

        return result;
    }

    public List<String> toLore() {
        return Arrays.asList(toString().split("\\n"));
    }
}
