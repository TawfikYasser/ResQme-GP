package com.example.resqme.model;

public class Winch {

    String drivingLicense,carLicense,Location,Status,CostPerKM,id, winchPlate,Availability;

    public Winch(String drivingLicense, String carLicense, String location, String status, String costPerKM, String id, String winchPlate, String availability) {
        this.drivingLicense = drivingLicense;
        this.carLicense = carLicense;
        Location = location;
        Status = status;
        CostPerKM = costPerKM;
        this.id = id;
        this.winchPlate = winchPlate;
        Availability = availability;
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
