package com.axenovo.infotech.model;

public class AdminPortfolioRow {

    private final String projectName;
    private final String summary;
    private final String dateLabel;
    private final String status;
    private final String imagePath;

    public AdminPortfolioRow(String projectName, String summary, String dateLabel, String status, String imagePath) {
        this.projectName = projectName;
        this.summary = summary;
        this.dateLabel = dateLabel;
        this.status = status;
        this.imagePath = imagePath;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getSummary() {
        return summary;
    }

    public String getDateLabel() {
        return dateLabel;
    }

    public String getStatus() {
        return status;
    }

    public String getImagePath() {
        return imagePath;
    }
}
