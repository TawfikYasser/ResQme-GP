package com.example.resqme.model;

public class CMC {
    String cmcID;
    String cmcName;
    String cmcImage;
    String cmcLocation;
    String CarMfgCountry;
    String cmcServiceProviderId;
    String cmcStatus;
    String cmcAvailablity,cmcMobileNumber;
    float rate = 0;


    public CMC(String cmcID, String cmcName, String cmcImage, String cmcLocation, String carMfgCountry, String cmcServiceProviderId, String cmcStatus, String cmcAvailablity, String cmcMobileNumber, float rate) {
        this.cmcID = cmcID;
        this.cmcName = cmcName;
        this.cmcImage = cmcImage;
        this.cmcLocation = cmcLocation;
        CarMfgCountry = carMfgCountry;
        this.cmcServiceProviderId = cmcServiceProviderId;
        this.cmcStatus = cmcStatus;
        this.cmcAvailablity = cmcAvailablity;
        this.cmcMobileNumber = cmcMobileNumber;
        this.rate = rate;
    }

    public CMC() {
    }
    public String getCmcAvailablity() {
        return cmcAvailablity;
    }

    public void setCmcAvailablity(String cmcAvailablity) {
        this.cmcAvailablity = cmcAvailablity;
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

    public String getCarMfgCountry() {
        return CarMfgCountry;
    }

    public void setCarMfgCountry(String cmcBrand) {
        this.CarMfgCountry = CarMfgCountry;
    }

    public String getCmcServiceProviderId() {
        return cmcServiceProviderId;
    }

    public void setCmcServiceProviderId(String cmcServiceProviderId) {
        this.cmcServiceProviderId = cmcServiceProviderId;
    }

    public String getCmcStatus() {
        return cmcStatus;
    }

    public void setCmcStatus(String cmcStatus) {
        this.cmcStatus = cmcStatus;
    }

    public String getcmcMobileNumber() {
        return cmcMobileNumber;
    }

    public void setcmcMobileNumber(String cmcMobileNumber) {
        this.cmcMobileNumber = cmcMobileNumber;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }
}
