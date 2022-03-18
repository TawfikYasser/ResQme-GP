package com.example.resqme.model;

public class Winch {

    String winchID, winchName, winchCostPerKM, winchStatus, winchAvailability, winchAddress, winchCurrentLocation, driverLicence, winchLicence, winchOwnerID;

    public Winch(String winchID, String winchName, String winchCostPerKM, String winchStatus, String winchAvailability, String winchAddress, String winchCurrentLocation, String driverLicence, String winchLicence, String winchOwnerID) {
        this.winchID = winchID;
        this.winchName = winchName;
        this.winchCostPerKM = winchCostPerKM;
        this.winchStatus = winchStatus;
        this.winchAvailability = winchAvailability;
        this.winchAddress = winchAddress;
        this.winchCurrentLocation = winchCurrentLocation;
        this.driverLicence = driverLicence;
        this.winchLicence = winchLicence;
        this.winchOwnerID = winchOwnerID;
    }

    public Winch(){}

    public String getWinchID() {
        return winchID;
    }

    public void setWinchID(String winchID) {
        this.winchID = winchID;
    }

    public String getWinchName() {
        return winchName;
    }

    public void setWinchName(String winchName) {
        this.winchName = winchName;
    }

    public String getWinchCostPerKM() {
        return winchCostPerKM;
    }

    public void setWinchCostPerKM(String winchCostPerKM) {
        this.winchCostPerKM = winchCostPerKM;
    }

    public String getWinchStatus() {
        return winchStatus;
    }

    public void setWinchStatus(String winchStatus) {
        this.winchStatus = winchStatus;
    }

    public String getWinchAvailability() {
        return winchAvailability;
    }

    public void setWinchAvailability(String winchAvailability) {
        this.winchAvailability = winchAvailability;
    }

    public String getWinchAddress() {
        return winchAddress;
    }

    public void setWinchAddress(String winchAddress) {
        this.winchAddress = winchAddress;
    }

    public String getWinchCurrentLocation() {
        return winchCurrentLocation;
    }

    public void setWinchCurrentLocation(String winchCurrentLocation) {
        this.winchCurrentLocation = winchCurrentLocation;
    }

    public String getDriverLicence() {
        return driverLicence;
    }

    public void setDriverLicence(String driverLicence) {
        this.driverLicence = driverLicence;
    }

    public String getWinchLicence() {
        return winchLicence;
    }

    public void setWinchLicence(String winchLicence) {
        this.winchLicence = winchLicence;
    }

    public String getWinchOwnerID() {
        return winchOwnerID;
    }

    public void setWinchOwnerID(String winchOwnerID) {
        this.winchOwnerID = winchOwnerID;
    }
}
