package com.example.resqme.model;

public class CMC {
    String cmcID, cmcName, cmcImage, cmcLocation, cmcBrand, cmcServiceProviderId, cmcStatus, cmcAvailability;

    public CMC(String cmcID, String cmcName, String cmcImage, String cmcLocation, String cmcBrand, String cmcServiceProviderId, String cmcStatus, String cmcAvailability) {
        this.cmcID = cmcID;
        this.cmcName = cmcName;
        this.cmcImage = cmcImage;
        this.cmcLocation = cmcLocation;
        this.cmcBrand = cmcBrand;
        this.cmcServiceProviderId = cmcServiceProviderId;
        this.cmcStatus = cmcStatus;
        this.cmcAvailability = cmcAvailability;
    }

    public CMC() {
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

    public String getCmcBrand() {
        return cmcBrand;
    }

    public void setCmcBrand(String cmcBrand) {
        this.cmcBrand = cmcBrand;
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

    public String getCmcAvailability() {
        return cmcAvailability;
    }

    public void setCmcAvailability(String cmcAvailability) {
        this.cmcAvailability = cmcAvailability;
    }
}