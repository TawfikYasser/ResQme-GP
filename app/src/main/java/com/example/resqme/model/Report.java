package com.example.resqme.model;

public class Report {
    String reportDescription,reportID,userID,reportStatus;

    public Report() {
    }

    public Report(String reportDescription, String reportID, String userID, String reportStatus) {
        this.reportDescription = reportDescription;
        this.reportID = reportID;
        this.userID = userID;
        this.reportStatus = reportStatus;
    }

    public String getReportDescription() {
        return reportDescription;
    }

    public String getUserID() {
        return userID;
    }

    public String getReportStatus() {
        return reportStatus;
    }

    public void setReportDescription(String reportDescription) {
        this.reportDescription = reportDescription;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setReportStatus(String reportStatus) {
        this.reportStatus = reportStatus;
    }

    public String getReportID() {
        return reportID;
    }

    public void setReportID(String reportID) {
        this.reportID = reportID;
    }
}
