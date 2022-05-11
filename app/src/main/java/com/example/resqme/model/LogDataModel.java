package com.example.resqme.model;

public class LogDataModel {
    String id, timestamp, userID, isService, clickedServiceID, appClick, eventName;

    public LogDataModel(String id, String timestamp, String userID, String isService, String clickedServiceID, String appClick, String eventName) {
        this.id = id;
        this.timestamp = timestamp;
        this.userID = userID;
        this.isService = isService;
        this.clickedServiceID = clickedServiceID;
        this.appClick = appClick;
        this.eventName = eventName;
    }

    public LogDataModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getIsService() {
        return isService;
    }

    public void setIsService(String isService) {
        this.isService = isService;
    }

    public String getClickedServiceID() {
        return clickedServiceID;
    }

    public void setClickedServiceID(String clickedServiceID) {
        this.clickedServiceID = clickedServiceID;
    }

    public String getAppClick() {
        return appClick;
    }

    public void setAppClick(String appClick) {
        this.appClick = appClick;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}
