package com.example.resqme.model;

public class WinchRequest {

    String winchRequestID, customerID, customerLat, customerLong, customerCarID, winchOwnerID, winchOwnerLat, winchOwnerLong, winchID, serviceCost, winchRequestDescription, winchRequestInitiationDate, winchRequestStatus;

    public WinchRequest(String winchRequestID, String customerID, String customerLat, String customerLong, String customerCarID, String winchOwnerID, String winchOwnerLat, String winchOwnerLong, String winchID, String serviceCost, String winchRequestDescription, String winchRequestInitiationDate, String winchRequestStatus) {
        this.winchRequestID = winchRequestID;
        this.customerID = customerID;
        this.customerLat = customerLat;
        this.customerLong = customerLong;
        this.customerCarID = customerCarID;
        this.winchOwnerID = winchOwnerID;
        this.winchOwnerLat = winchOwnerLat;
        this.winchOwnerLong = winchOwnerLong;
        this.winchID = winchID;
        this.serviceCost = serviceCost;
        this.winchRequestDescription = winchRequestDescription;
        this.winchRequestInitiationDate = winchRequestInitiationDate;
        this.winchRequestStatus = winchRequestStatus;
    }

    public WinchRequest(){

    }

    public String getWinchRequestID() {
        return winchRequestID;
    }

    public void setWinchRequestID(String winchRequestID) {
        this.winchRequestID = winchRequestID;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getWinchOwnerID() {
        return winchOwnerID;
    }

    public void setWinchOwnerID(String winchOwnerID) {
        this.winchOwnerID = winchOwnerID;
    }

    public String getWinchID() {
        return winchID;
    }

    public void setWinchID(String winchID) {
        this.winchID = winchID;
    }

    public String getServiceCost() {
        return serviceCost;
    }

    public void setServiceCost(String serviceCost) {
        this.serviceCost = serviceCost;
    }

    public String getWinchRequestDescription() {
        return winchRequestDescription;
    }

    public void setWinchRequestDescription(String winchRequestDescription) {
        this.winchRequestDescription = winchRequestDescription;
    }

    public String getWinchRequestInitiationDate() {
        return winchRequestInitiationDate;
    }

    public void setWinchRequestInitiationDate(String winchRequestInitiationDate) {
        this.winchRequestInitiationDate = winchRequestInitiationDate;
    }

    public String getWinchRequestStatus() {
        return winchRequestStatus;
    }

    public void setWinchRequestStatus(String winchRequestStatus) {
        this.winchRequestStatus = winchRequestStatus;
    }

    public String getCustomerLat() {
        return customerLat;
    }

    public void setCustomerLat(String customerLat) {
        this.customerLat = customerLat;
    }

    public String getCustomerLong() {
        return customerLong;
    }

    public void setCustomerLong(String customerLong) {
        this.customerLong = customerLong;
    }

    public String getWinchOwnerLat() {
        return winchOwnerLat;
    }

    public void setWinchOwnerLat(String winchOwnerLat) {
        this.winchOwnerLat = winchOwnerLat;
    }

    public String getWinchOwnerLong() {
        return winchOwnerLong;
    }

    public void setWinchOwnerLong(String winchOwnerLong) {
        this.winchOwnerLong = winchOwnerLong;
    }

    public String getCustomerCarID() {
        return customerCarID;
    }

    public void setCustomerCarID(String customerCarID) {
        this.customerCarID = customerCarID;
    }
}
