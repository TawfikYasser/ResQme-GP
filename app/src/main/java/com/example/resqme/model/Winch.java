package com.example.resqme.model;

public class Winch {

    String drivingLicense,carLicense,Location,Status,CostPerKM,id, winchPlate,Availability,SP_ID;

    public Winch(String drivingLicense, String carLicense, String location, String status, String costPerKM, String id, String winchPlate, String availability, String SP_ID) {
        this.drivingLicense = drivingLicense;
        this.carLicense = carLicense;
        Location = location;
        Status = status;
        CostPerKM = costPerKM;
        this.id = id;
        this.winchPlate = winchPlate;
        Availability = availability;
        this.SP_ID = SP_ID;
    }

    public String getDrivingLicense() {
        return drivingLicense;
    }

    public String getCarLicense() {
        return carLicense;
    }

    public String getLocation() {
        return Location;
    }

    public String getStatus() {
        return Status;
    }

    public String getSP_ID() {
        return SP_ID;
    }

    public void setSP_ID(String SP_ID) {
        this.SP_ID = SP_ID;
    }

    public String getCostPerKM() {
        return CostPerKM;
    }

    public String getId() {
        return id;
    }

    public String getWinchPlate() {
        return winchPlate;
    }

    public String getAvailability() {
        return Availability;
    }
}
