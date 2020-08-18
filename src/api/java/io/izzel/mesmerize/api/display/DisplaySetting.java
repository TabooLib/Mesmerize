package io.izzel.mesmerize.api.display;

public class DisplaySetting implements Cloneable {

    private int maxWidth = 30;

    private Layout layout = Layout.LINE;

    private String decimalFormat = "+0.##;-0.##";

    private boolean paddingWithRandomColor = false;

    private String paddingElement = "| ";

    private boolean flattenFirstPadding = true;

    public int maxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public Layout layout() {
        return layout;
    }

    public void setLayout(Layout layout) {
        this.layout = layout;
    }

    public String decimalFormat() {
        return decimalFormat;
    }

    public void setDecimalFormat(String decimalFormat) {
        this.decimalFormat = decimalFormat;
    }

    public boolean paddingWithRandomColor() {
        return paddingWithRandomColor;
    }

    public void setPaddingWithRandomColor(boolean paddingWithRandomColor) {
        this.paddingWithRandomColor = paddingWithRandomColor;
    }

    public String paddingElement() {
        return paddingElement;
    }

    public void setPaddingElement(String paddingElement) {
        this.paddingElement = paddingElement;
    }

    public boolean flattenFirstPadding() {
        return flattenFirstPadding;
    }

    public void setFlattenFirstPadding(boolean flattenFirstPadding) {
        this.flattenFirstPadding = flattenFirstPadding;
    }

    @Override
    public DisplaySetting clone() {
        try {
            return (DisplaySetting) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException(e);
        }
    }

    public enum Layout {
        LINE, FLOW
    }
}
