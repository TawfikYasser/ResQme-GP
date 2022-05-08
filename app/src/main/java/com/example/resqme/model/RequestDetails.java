package com.example.resqme.model;

public class RequestDetails {
    String requestDetailsID, requestID, customerID, serviceProviderID,
    Lights, Battery, Oil, Filter, Tiers, Other;

    public RequestDetails(String requestDetailsID, String requestID, String customerID, String serviceProviderID, String lights, String battery, String oil, String filter, String tiers, String other) {
        this.requestDetailsID = requestDetailsID;
        this.requestID = requestID;
        this.customerID = customerID;
        this.serviceProviderID = serviceProviderID;
        Lights = lights;
        Battery = battery;
        Oil = oil;
        Filter = filter;
        Tiers = tiers;
        Other = other;
    }



    public RequestDetails() {
    }

    public String getRequestDetailsID() {
        return requestDetailsID;
    }

    public void setRequestDetailsID(String requestDetailsID) {
        this.requestDetailsID = requestDetailsID;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getServiceProviderID() {
        return serviceProviderID;
    }

    public void setServiceProviderID(String serviceProviderID) {
        this.serviceProviderID = serviceProviderID;
    }

    public String getLights() {
        return Lights;
    }

    public void setLights(String lights) {
        Lights = lights;
    }

    public String getBattery() {
        return Battery;
    }

    public void setBattery(String battery) {
        Battery = battery;
    }

    public String getOil() {
        return Oil;
    }

    public void setOil(String oil) {
        Oil = oil;
    }

    public String getFilter() {
        return Filter;
    }

    public void setFilter(String filter) {
        Filter = filter;
    }

    public String getTiers() {
        return Tiers;
    }

    public void setTiers(String tiers) {
        Tiers = tiers;
    }

    public String getOther() {
        return Other;
    }

    public void setOther(String other) {
        Other = other;
    }
}
