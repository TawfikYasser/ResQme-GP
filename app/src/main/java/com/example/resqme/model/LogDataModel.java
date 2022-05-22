package com.example.resqme.model;

public class LogDataModel {

    String logID, logTimestamp, userID, eventType, serviceID, serviceName, appClickName, pageName, deviceSDK, deviceName, deviceModel;

    public LogDataModel(String logID, String logTimestamp, String userID, String eventType, String serviceID, String serviceName, String appClickName, String pageName, String deviceSDK, String deviceName, String deviceModel) {
        this.logID = logID;
        this.logTimestamp = logTimestamp;
        this.userID = userID;
        this.eventType = eventType;
        this.serviceID = serviceID;
        this.serviceName = serviceName;
        this.appClickName = appClickName;
        this.pageName = pageName;
        this.deviceSDK = deviceSDK;
        this.deviceName = deviceName;
        this.deviceModel = deviceModel;
    }

    public LogDataModel(){}

    public String getLogID() {
        return logID;
    }

    public void setLogID(String logID) {
        this.logID = logID;
    }

    public String getLogTimestamp() {
        return logTimestamp;
    }

    public void setLogTimestamp(String logTimestamp) {
        this.logTimestamp = logTimestamp;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getServiceID() {
        return serviceID;
    }

    public void setServiceID(String serviceID) {
        this.serviceID = serviceID;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getAppClickName() {
        return appClickName;
    }

    public void setAppClickName(String appClickName) {
        this.appClickName = appClickName;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getDeviceSDK() {
        return deviceSDK;
    }

    public void setDeviceSDK(String deviceSDK) {
        this.deviceSDK = deviceSDK;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }
}
