package io.izzel.mesmerize.api.display;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface DisplayPane extends Element {

    List<Element> getHeaders();

    void addHeader(Element element);

    List<Element> getElements();

    void addElement(Element element);

    DisplayPane createSubPane();

    DisplaySection createSection(String id);

    DisplaySection getOrCreateSection(String id);

    void addSection(DisplaySection section);

    @Nullable Element getPadding();

    void setPadding(Element padding);

    @NotNull DisplaySetting getDisplaySetting();
}
