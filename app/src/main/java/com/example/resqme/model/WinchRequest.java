package com.example.resqme.model;

public class WinchRequest {

    String winchRequestID, customerID, winchOwnerID, winchID, serviceCost, winchRequestDescription, winchRequestInitiationDate;

    public WinchRequest(String winchRequestID, String customerID, String winchOwnerID, String winchID, String serviceCost, String winchRequestDescription, String winchRequestInitiationDate) {
        this.winchRequestID = winchRequestID;
        this.customerID = customerID;
        this.winchOwnerID = winchOwnerID;
        this.winchID = winchID;
        this.serviceCost = serviceCost;
        this.winchRequestDescription = winchRequestDescription;
        this.winchRequestInitiationDate = winchRequestInitiationDate;
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
}
