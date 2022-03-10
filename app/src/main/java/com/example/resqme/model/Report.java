package com.example.resqme.model;

public class Report {
    String reportDescription, reportID, userID, reportStatus, userEmail;

    public Report() {
    }

    public Report(String reportDescription, String reportID, String userID, String reportStatus, String userEmail) {
        this.reportDescription = reportDescription;
        this.reportID = reportID;
        this.userID = userID;
        this.reportStatus = reportStatus;
        this.userEmail = userEmail;
    }

    public String getReportDescription() {
        return reportDescription;
    }

    public void setReportDescription(String reportDescription) {
        this.reportDescription = reportDescription;
    }

    public String getReportID() {
        return reportID;
    }

    public void setReportID(String reportID) {
        this.reportID = reportID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getReportStatus() {
        return reportStatus;
    }

    public void setReportStatus(String reportStatus) {
        this.reportStatus = reportStatus;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
