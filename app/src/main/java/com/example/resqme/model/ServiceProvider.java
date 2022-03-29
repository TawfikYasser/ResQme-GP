package com.example.resqme.model;

public class ServiceProvider {
    String username = "";
    String email = "";
    String password = "";
    String image = "";
    String address = "";
    String whatsApp = "";
    String bod = "";
    String userId = "";
    float rate = 0;
    String gender = "";
    String userType = "";
    String serviceType = "";
    String isCMC = "";
    String isWinch = "";
    String isSpareParts = "";

    public ServiceProvider(String username, String email, String password, String image, String address, String whatsApp, String bod, String userId, float rate, String gender, String userType, String serviceType, String isCMC, String isWinch, String isSpareParts) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.image = image;
        this.address = address;
        this.whatsApp = whatsApp;
        this.bod = bod;
        this.userId = userId;
        this.rate = rate;
        this.gender = gender;
        this.userType = userType;
        this.serviceType = serviceType;
        this.isCMC = isCMC;
        this.isWinch = isWinch;
        this.isSpareParts = isSpareParts;
    }

    ServiceProvider(){}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWhatsApp() {
        return whatsApp;
    }

    public void setWhatsApp(String whatsApp) {
        this.whatsApp = whatsApp;
    }

    public String getBod() {
        return bod;
    }

    public void setBod(String bod) {
        this.bod = bod;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getIsCMC() {
        return isCMC;
    }

    public void setIsCMC(String isCMC) {
        this.isCMC = isCMC;
    }

    public String getIsWinch() {
        return isWinch;
    }

    public void setIsWinch(String isWinch) {
        this.isWinch = isWinch;
    }

    public String getIsSpareParts() {
        return isSpareParts;
    }

    public void setIsSpareParts(String isSpareParts) {
        this.isSpareParts = isSpareParts;
    }
}
