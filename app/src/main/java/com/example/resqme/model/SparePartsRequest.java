package com.example.resqme.model;

public class SparePartsRequest {
    String sparePartsRequestID, sparePartsRequestStatus, customerID, customerCurrentLat, customerCurrentLong,
            sparePartsRequestTimestamp, serviceCost, customerCarID, sparePartItemID, sparePartOwnerID;

    public SparePartsRequest(String sparePartsRequestID, String sparePartsRequestStatus, String customerID, String customerCurrentLat, String customerCurrentLong, String sparePartsRequestTimestamp, String serviceCost, String customerCarID, String sparePartItemID, String sparePartOwnerID) {
        this.sparePartsRequestID = sparePartsRequestID;
        this.sparePartsRequestStatus = sparePartsRequestStatus;
        this.customerID = customerID;
        this.customerCurrentLat = customerCurrentLat;
        this.customerCurrentLong = customerCurrentLong;
        this.sparePartsRequestTimestamp = sparePartsRequestTimestamp;
        this.serviceCost = serviceCost;
        this.customerCarID = customerCarID;
        this.sparePartItemID = sparePartItemID;
        this.sparePartOwnerID = sparePartOwnerID;
    }

    public String getSparePartsRequestStatus() {
        return sparePartsRequestStatus;
    }

    public void setSparePartsRequestStatus(String sparePartsRequestStatus) {
        this.sparePartsRequestStatus = sparePartsRequestStatus;
    }

    public String getSparePartOwnerID() {
        return sparePartOwnerID;
    }

    public void setSparePartOwnerID(String sparePartOwnerID) {
        this.sparePartOwnerID = sparePartOwnerID;
    }

    public SparePartsRequest(){}

    public String getSparePartsRequestID() {
        return sparePartsRequestID;
    }

    public void setSparePartsRequestID(String sparePartsRequestID) {
        this.sparePartsRequestID = sparePartsRequestID;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getCustomerCurrentLat() {
        return customerCurrentLat;
    }

    public void setCustomerCurrentLat(String customerCurrentLat) {
        this.customerCurrentLat = customerCurrentLat;
    }

    public String getCustomerCurrentLong() {
        return customerCurrentLong;
    }

    public void setCustomerCurrentLong(String customerCurrentLong) {
        this.customerCurrentLong = customerCurrentLong;
    }

    public String getSparePartsRequestTimestamp() {
        return sparePartsRequestTimestamp;
    }

    public void setSparePartsRequestTimestamp(String sparePartsRequestTimestamp) {
        this.sparePartsRequestTimestamp = sparePartsRequestTimestamp;
    }

    public String getServiceCost() {
        return serviceCost;
    }

    public void setServiceCost(String serviceCost) {
        this.serviceCost = serviceCost;
    }

    public String getCustomerCarID() {
        return customerCarID;
    }

    public void setCustomerCarID(String customerCarID) {
        this.customerCarID = customerCarID;
    }

    public String getSparePartItemID() {
        return sparePartItemID;
    }

    public void setSparePartItemID(String sparePartItemID) {
        this.sparePartItemID = sparePartItemID;
    }
}
