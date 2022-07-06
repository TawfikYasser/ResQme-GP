package com.example.resqme.model;

public class Rate {
    String rateId, customerID, spId, rateValue, rateText, requestID, rateFrom;

    public Rate(String rateId, String customerID, String spId, String rateValue, String rateText, String requestID, String rateFrom) {
        this.rateId = rateId;
        this.customerID = customerID;
        this.spId = spId;
        this.rateValue = rateValue;
        this.rateText = rateText;
        this.requestID = requestID;
        this.rateFrom = rateFrom;
    }

    public Rate() {
    }

    public String getRateFrom() {
        return rateFrom;
    }

    public void setRateFrom(String rateFrom) {
        this.rateFrom = rateFrom;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getRateId() {
        return rateId;
    }

    public void setRateId(String rateId) {
        this.rateId = rateId;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getSpId() {
        return spId;
    }

    public void setSpId(String spId) {
        this.spId = spId;
    }

    public String getRateValue() {
        return rateValue;
    }

    public void setRateValue(String rateValue) {
        this.rateValue = rateValue;
    }

    public String getRateText() {
        return rateText;
    }

    public void setRateText(String rateText) {
        this.rateText = rateText;
    }
}
