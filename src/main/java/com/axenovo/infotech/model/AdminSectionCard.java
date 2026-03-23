package com.axenovo.infotech.model;

public class AdminSectionCard {

    private final String title;
    private final String href;
    private final String description;
    private final String value;
    private final String valueLabel;

    public AdminSectionCard(String title, String href, String description, String value, String valueLabel) {
        this.title = title;
        this.href = href;
        this.description = description;
        this.value = value;
        this.valueLabel = valueLabel;
    }

    public String getTitle() {
        return title;
    }

    public String getHref() {
        return href;
    }

    public String getDescription() {
        return description;
    }

    public String getValue() {
        return value;
    }

    public String getValueLabel() {
        return valueLabel;
    }
}
