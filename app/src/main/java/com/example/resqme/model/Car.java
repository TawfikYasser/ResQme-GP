package com.example.resqme.model;

public class Car {
    String carID, userID, carType, carModel, carMaintenance, carTransmission, carDriverLicence, carLicence, carStatus;

    public Car(String carID, String userID, String carType, String carModel, String carMaintenance, String carTransmission, String carDriverLicence, String carLicence, String carStatus) {
        this.carID = carID;
        this.userID = userID;
        this.carType = carType;
        this.carModel = carModel;
        this.carMaintenance = carMaintenance;
        this.carTransmission = carTransmission;
        this.carDriverLicence = carDriverLicence;
        this.carLicence = carLicence;
        this.carStatus = carStatus;
    }

    public Car(){

    }

    public String getCarID() {
        return carID;
    }

    public void setCarID(String carID) {
        this.carID = carID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getCarMaintenance() {
        return carMaintenance;
    }

    public void setCarMaintenance(String carMaintenance) {
        this.carMaintenance = carMaintenance;
    }

    public String getCarTransmission() {
        return carTransmission;
    }

    public void setCarTransmission(String carTransmission) {
        this.carTransmission = carTransmission;
    }

    public String getCarDriverLicence() {
        return carDriverLicence;
    }

    public void setCarDriverLicence(String carDriverLicence) {
        this.carDriverLicence = carDriverLicence;
    }

    public String getCarLicence() {
        return carLicence;
    }

    public void setCarLicence(String carLicence) {
        this.carLicence = carLicence;
    }

    public String getCarStatus() {
        return carStatus;
    }

    public void setCarStatus(String carStatus) {
        this.carStatus = carStatus;
    }
}
