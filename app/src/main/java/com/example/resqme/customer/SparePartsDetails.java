package com.example.resqme.customer;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.resqme.R;
import com.example.resqme.common.LogData;
import com.example.resqme.model.SparePart;
import com.example.resqme.model.SparePartInCart;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SparePartsDetails extends AppCompatActivity {
    ImageView itemImageView;
    TextView itemName, itemPrice, itemNewOrUsed, itemAvailability, itemCarType;
    MaterialButton BtnAddToCartFromDetailsPage;
    DatabaseReference shoppingCart;

    //Item Data
    String itemIdSTR, itemNameSTR, itemAvailabilitySTR, itemPriceSTR,
            itemCarTypeSTR, itemImageSTR, itemOwnerIDSTR, itemStatusSTR, itemNewOrUsedSTR;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spareparts_details);
        SharedPreferences userData = getSharedPreferences("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
        String c_userid = userData.getString("C_USERID", "C_DEFAULT");
        initViews();
        forceRTLIfSupported();
        initToolbar();
        shoppingCart = FirebaseDatabase.getInstance().getReference().child("ShoppingCart");
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
                shoppingCart.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (!snapshot.child(itemIdSTR+"-CCC-"+c_userid).exists()) {
                            SparePartInCart sparePart = new SparePartInCart(itemIdSTR+"-CCC-"+c_userid, itemIdSTR, c_userid, itemNameSTR, itemImageSTR, itemPriceSTR, itemNewOrUsedSTR,
                                    itemStatusSTR, itemOwnerIDSTR, itemCarTypeSTR, itemAvailabilitySTR);
                            shoppingCart.child(itemIdSTR+"-CCC-"+c_userid).setValue(sparePart);
                            Toast.makeText(SparePartsDetails.this,"تم إضافة: "+ itemNameSTR, Toast.LENGTH_SHORT).show();
                            LogData.saveLog(itemIdSTR,"TRUE","","FALSE");
                        }else{
                            Toast.makeText(SparePartsDetails.this, "موجود بالفعل", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
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