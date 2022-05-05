package com.example.resqme.model;

public class NotificationResQme {
    String NotificationID, NotificationTitle, NotificationBody, NotificationUserID;

    public NotificationResQme(String notificationID, String notificationTitle, String notificationBody, String notificationUserID) {
        NotificationID = notificationID;
        NotificationTitle = notificationTitle;
        NotificationBody = notificationBody;
        NotificationUserID = notificationUserID;
    }

    public NotificationResQme() {
    }

    public String getNotificationID() {
        return NotificationID;
    }

    public void setNotificationID(String notificationID) {
        NotificationID = notificationID;
    }

    public String getNotificationTitle() {
        return NotificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        NotificationTitle = notificationTitle;
    }

    public String getNotificationBody() {
        return NotificationBody;
    }

    public void setNotificationBody(String notificationBody) {
        NotificationBody = notificationBody;
    }

    public String getNotificationUserID() {
        return NotificationUserID;
    }

    public void setNotificationUserID(String notificationUserID) {
        NotificationUserID = notificationUserID;
    }

}
