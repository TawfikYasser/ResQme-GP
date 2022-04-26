package com.example.resqme.model;

public class RequestDetailsModel {
    String requestDetailsId, customerId, winchRequestId, Lights, Battery, Oil, Filter, Tier, Engine, other;

    public RequestDetailsModel(String requestDetailsId, String customerId, String winchRequestId, String lights, String battery, String oil, String filter, String tier, String engine, String other) {
        this.requestDetailsId = requestDetailsId;
        this.customerId = customerId;
        this.winchRequestId = winchRequestId;
        Lights = lights;
        Battery = battery;
        Oil = oil;
        Filter = filter;
        Tier = tier;
        Engine = engine;
        this.other = other;
    }

    public String getRequestDetailsId() {
        return requestDetailsId;
    }

    public void setRequestDetailsId(String requestDetailsId) {
        this.requestDetailsId = requestDetailsId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getWinchRequestId() {
        return winchRequestId;
    }

    public void setWinchRequestId(String winchRequestId) {
        this.winchRequestId = winchRequestId;
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

    public String getTier() {
        return Tier;
    }

    public void setTier(String tier) {
        Tier = tier;
    }

    public String getEngine() {
        return Engine;
    }

    public void setEngine(String engine) {
        Engine = engine;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }
}
