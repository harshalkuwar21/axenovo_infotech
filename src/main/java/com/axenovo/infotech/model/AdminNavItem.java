package com.axenovo.infotech.model;

public class AdminNavItem {

    private final String label;
    private final String href;
    private final String symbol;
    private final boolean active;
    private final String badge;

    public AdminNavItem(String label, String href, String symbol, boolean active, String badge) {
        this.label = label;
        this.href = href;
        this.symbol = symbol;
        this.active = active;
        this.badge = badge;
    }

    public String getLabel() {
        return label;
    }

    public String getHref() {
        return href;
    }

    public String getSymbol() {
        return symbol;
    }

    public boolean isActive() {
        return active;
    }

    public String getBadge() {
        return badge;
    }
}
