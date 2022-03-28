package com.example.resqme.customer;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.resqme.R;
import com.example.resqme.model.SparePart;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class SparePartsDetails extends AppCompatActivity {
    ImageView itemImageView;
    TextView itemName, itemPrice, itemNewOrUsed, itemAvailability, itemCarType;
    MaterialButton BtnAddToCartFromDetailsPage;


    //Item Data
    String itemIdSTR, itemNameSTR, itemAvailabilitySTR, itemPriceSTR,
            itemCarTypeSTR, itemImageSTR, itemOwnerIDSTR, itemStatusSTR, itemNewOrUsedSTR;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spareparts_details);
        initViews();
        forceRTLIfSupported();
        initToolbar();

        Intent intent = getIntent();
        itemIdSTR = intent.getStringExtra("ITEM_ID");
        itemNameSTR = intent.getStringExtra("ITEM_NAME");
        itemAvailabilitySTR = intent.getStringExtra("ITEM_AVAILABILITY");
        itemPriceSTR = intent.getStringExtra("ITEM_PRICE");
        itemCarTypeSTR = intent.getStringExtra("ITEM_CARTYPE");
        itemImageSTR = intent.getStringExtra("ITEM_IMAGE");
        itemOwnerIDSTR = intent.getStringExtra("ITEM_OWNER_ID");
        itemStatusSTR = intent.getStringExtra("ITEM_STATUS");
        itemNewOrUsedSTR = intent.getStringExtra("ITEM_NEWORUSED");


        Glide.with(this).load(itemImageSTR).into(itemImageView);
        itemName.setText(itemNameSTR);
        itemCarType.setText(itemCarTypeSTR);
        itemPrice.setText(itemPriceSTR);
        itemNewOrUsed.setText(itemNewOrUsedSTR);
        if(itemAvailabilitySTR.equals("Available")){
            itemAvailability.setText("متاح");
            itemAvailability.setTextColor(Color.GREEN);
        }else{
            itemAvailability.setText("غير متاح");
            itemAvailability.setTextColor(Color.rgb(255, 166, 53));
        }

        BtnAddToCartFromDetailsPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Need to handle duplicates, and save data to firebase.
                CustomerHome.customerCart.setSparePartArrayList(new SparePart(
                        itemIdSTR, itemNameSTR, itemImageSTR, itemPriceSTR, itemNewOrUsedSTR, itemStatusSTR, itemOwnerIDSTR, itemCarTypeSTR, itemAvailabilitySTR
                ));
            }
        });
    }

    private void initViews() {
        itemImageView = findViewById(R.id.sparepart_image_detailspage);
        itemName = findViewById(R.id.sparepartname_detailspag);
        itemCarType = findViewById(R.id.sparepartspecialization_detailspag);
        itemPrice = findViewById(R.id.sparepartprice_detailspag);
        itemNewOrUsed = findViewById(R.id.sparepartneworused_detailspag);
        itemAvailability = findViewById(R.id.sparepartavailablenow_detailspag);
        BtnAddToCartFromDetailsPage = findViewById(R.id.add_to_car_btn_in_details_page);
    }

    private void initToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_spareparts_details);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("تفاصيل قطعة الغيار");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextAppearance(SparePartsDetails.this, R.style.Theme_ResQme);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void forceRTLIfSupported() {
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
    }
}