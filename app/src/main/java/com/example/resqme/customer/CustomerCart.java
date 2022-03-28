package com.example.resqme.customer;

import com.example.resqme.model.SparePart;

import java.util.ArrayList;
import java.util.Collection;
import java.util.RandomAccess;

public class CustomerCart {
    public ArrayList<SparePart> sparePartArrayList;

    public CustomerCart() {
        sparePartArrayList = new ArrayList<>();

    }

    public ArrayList<SparePart> getSparePartArrayList() {
        return sparePartArrayList;
    }

    public void setSparePartArrayList(SparePart sparePart) {
        this.sparePartArrayList.add(sparePart);
    }

}
