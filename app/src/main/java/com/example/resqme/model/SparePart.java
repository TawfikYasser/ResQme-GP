package com.example.resqme.model;

public class SparePart {
    String itemID, itemName, itemImage, itemPrice, itemNewOrUsed, itemStatus, itemServiceProviderId, itemCarType, itemAvailability;

    public SparePart(String itemID, String itemName, String itemImage, String itemPrice, String itemNewOrUsed, String itemStatus, String itemServiceProviderId, String itemCarType, String itemAvailability) {
        this.itemID = itemID;
        this.itemName = itemName;
        this.itemImage = itemImage;
        this.itemPrice = itemPrice;
        this.itemNewOrUsed = itemNewOrUsed;
        this.itemStatus = itemStatus;
        this.itemServiceProviderId = itemServiceProviderId;
        this.itemCarType = itemCarType;
        this.itemAvailability = itemAvailability;
    }
    public SparePart() {
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemNewOrUsed() {
        return itemNewOrUsed;
    }

    public void setItemNewOrUsed(String itemNewOrUsed) {
        this.itemNewOrUsed = itemNewOrUsed;
    }

    public String getItemStatus() {
        return itemStatus;
    }

    public void setItemStatus(String itemStatus) {
        this.itemStatus = itemStatus;
    }

    public String getItemServiceProviderId() {
        return itemServiceProviderId;
    }

    public void setItemServiceProviderId(String itemServiceProviderId) {
        this.itemServiceProviderId = itemServiceProviderId;
    }

    public String getItemCarType() {
        return itemCarType;
    }

    public void setItemCarType(String itemCarType) {
        this.itemCarType = itemCarType;
    }

    public String getItemAvailability() {
        return itemAvailability;
    }

    public void setItemAvailability(String itemAvailability) {
        this.itemAvailability = itemAvailability;
    }
}
