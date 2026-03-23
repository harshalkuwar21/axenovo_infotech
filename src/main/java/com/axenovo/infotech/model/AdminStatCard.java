package com.axenovo.infotech.model;

public class AdminStatCard {

    private final String title;
    private final String value;
    private final String note;
    private final String symbol;
    private final String toneClass;

    public AdminStatCard(String title, String value, String note, String symbol, String toneClass) {
        this.title = title;
        this.value = value;
        this.note = note;
        this.symbol = symbol;
        this.toneClass = toneClass;
    }

    public String getTitle() {
        return title;
    }

    public String getValue() {
        return value;
    }

    public String getNote() {
        return note;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getToneClass() {
        return toneClass;
    }
}
