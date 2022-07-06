package com.example.resqme.model;

public class CMCRequest {

    String cmcRequestID, cmcID, cmcName, cmcImage, cmcLocation, cmcAvailability, cmcStatus, cmcRequestDescription, cmcRequestStatus,
            cmcOwnerID, customerID, requestCMCTimestamp, customerLat, customerLong, customerCarID, serviceCost;

    public CMCRequest(String cmcRequestID, String cmcID, String cmcName, String cmcImage, String cmcLocation, String cmcAvailability, String cmcStatus, String cmcRequestDescription, String cmcRequestStatus, String cmcOwnerID, String customerID, String requestCMCTimestamp, String customerLat, String customerLong, String customerCarID, String serviceCost) {
        this.cmcRequestID = cmcRequestID;
        this.cmcID = cmcID;
        this.cmcName = cmcName;
        this.cmcImage = cmcImage;
        this.cmcLocation = cmcLocation;
        this.cmcAvailability = cmcAvailability;
        this.cmcStatus = cmcStatus;
        this.cmcRequestDescription = cmcRequestDescription;
        this.cmcRequestStatus = cmcRequestStatus;
        this.cmcOwnerID = cmcOwnerID;
        this.customerID = customerID;
        this.requestCMCTimestamp = requestCMCTimestamp;
        this.customerLat = customerLat;
        this.customerLong = customerLong;
        this.customerCarID = customerCarID;
        this.serviceCost = serviceCost;
    }
    public CMCRequest(){}

    public String getCmcRequestID() {
        return cmcRequestID;
    }

    public void setCmcRequestID(String cmcRequestID) {
        this.cmcRequestID = cmcRequestID;
    }

    public String getCmcID() {
        return cmcID;
    }

    public void setCmcID(String cmcID) {
        this.cmcID = cmcID;
    }

    public String getCmcName() {
        return cmcName;
    }

    public void setCmcName(String cmcName) {
        this.cmcName = cmcName;
    }

    public String getCmcImage() {
        return cmcImage;
    }

    public void setCmcImage(String cmcImage) {
        this.cmcImage = cmcImage;
    }

    public String getCmcLocation() {
        return cmcLocation;
    }

    public void setCmcLocation(String cmcLocation) {
        this.cmcLocation = cmcLocation;
    }

    public String getCmcAvailability() {
        return cmcAvailability;
    }

    public void setCmcAvailability(String cmcAvailability) {
        this.cmcAvailability = cmcAvailability;
    }

    public String getCmcStatus() {
        return cmcStatus;
    }

    public void setCmcStatus(String cmcStatus) {
        this.cmcStatus = cmcStatus;
    }

    public String getCmcRequestDescription() {
        return cmcRequestDescription;
    }

    public void setCmcRequestDescription(String cmcRequestDescription) {
        this.cmcRequestDescription = cmcRequestDescription;
    }

    public String getCmcRequestStatus() {
        return cmcRequestStatus;
    }

    public void setCmcRequestStatus(String cmcRequestStatus) {
        this.cmcRequestStatus = cmcRequestStatus;
    }

    public String getCmcOwnerID() {
        return cmcOwnerID;
    }

    public void setCmcOwnerID(String cmcOwnerID) {
        this.cmcOwnerID = cmcOwnerID;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getRequestCMCTimestamp() {
        return requestCMCTimestamp;
    }

    public void setRequestCMCTimestamp(String requestCMCTimestamp) {
        this.requestCMCTimestamp = requestCMCTimestamp;
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

    public String getCustomerCarID() {
        return customerCarID;
    }

    public void setCustomerCarID(String customerCarID) {
        this.customerCarID = customerCarID;
    }

    public String getServiceCost() {
        return serviceCost;
    }

    public void setServiceCost(String serviceCost) {
        this.serviceCost = serviceCost;
    }
}
