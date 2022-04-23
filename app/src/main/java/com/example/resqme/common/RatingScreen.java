package com.example.resqme.common;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.resqme.R;
import com.example.resqme.customer.AddCarData;
import com.example.resqme.model.Customer;
import com.example.resqme.model.Rate;
import com.example.resqme.model.ServiceProvider;
import com.example.resqme.model.WinchRequest;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class RatingScreen extends AppCompatActivity {
    RatingBar ratingBar;
    String ratingValue = "";
    TextInputEditText ratingText;
    MaterialButton saveRate;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_screen);
        forceRTLIfSupported();
        initToolbar();
        Locale.setDefault(new Locale("ar"));
        progressDialog = new ProgressDialog(this);
        Intent getWinchRequestID = getIntent();
        String winchRequestID = getWinchRequestID.getStringExtra("WINCH_REQUEST_ID");
        String spID = getWinchRequestID.getStringExtra("WINCH_REQUEST_SP_ID");
        String customerID = getWinchRequestID.getStringExtra("WINCH_REQUEST_CUSTOMER_ID");
        String from = getWinchRequestID.getStringExtra("FROM_REQUEST");

        ratingBar = findViewById(R.id.rating_bar_page);
        ratingText = findViewById(R.id.rating_description_et);
        saveRate = findViewById(R.id.save_rating_btn);
        saveRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(ratingText.getText().toString().trim())){
                    if(String.valueOf(ratingBar.getRating()).equals("0.0")){
                        Snackbar.make(findViewById(android.R.id.content),"من فضلك اختر تقييم من 1 الى 5",Snackbar.LENGTH_LONG)
                                .setBackgroundTint(getResources().getColor(R.color.red_color))
                                .setTextColor(getResources().getColor(R.color.white))
                                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();

                    }else{
                        progressDialog.show();
                        progressDialog.setMessage("من فضلك انتظر قليلاً...");
                        if(from.equals("CUSTOMER")){
                            Query query = FirebaseDatabase.getInstance().getReference("ServiceProviders").
                                    orderByChild("userId").equalTo(spID);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        ServiceProvider serviceProvider = dataSnapshot.getValue(ServiceProvider.class);
                                        double totalNewRate = (Double.parseDouble(serviceProvider.getRate()) + Double.parseDouble(String.valueOf(ratingBar.getRating()))) / 2;
                                        DatabaseReference spTable = FirebaseDatabase.getInstance().getReference().child("ServiceProviders");
                                        spTable.child(spID).child("rate").setValue(String.valueOf(totalNewRate));
                                        // Save the rate in the rate table
                                        DatabaseReference rateTable = FirebaseDatabase.getInstance().getReference().child("Rate");
                                        String rateID = rateTable.push().getKey();
                                        Rate rate = new Rate(rateID, customerID, spID, String.valueOf(ratingBar.getRating()), ratingText.getText().toString().trim(), winchRequestID, "Customer");
                                        rateTable.child(rateID).setValue(rate);

                                        progressDialog.dismiss();
                                        Toast.makeText(RatingScreen.this, "تمت عملية التقييم بنجاح!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }else if(from.equals("SP")){
                            Query query = FirebaseDatabase.getInstance().getReference("Customer").
                                    orderByChild("userId").equalTo(customerID);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        Customer customer = dataSnapshot.getValue(Customer.class);
                                        double totalNewRate = (Double.parseDouble(customer.getRate()) + Double.parseDouble(String.valueOf(ratingBar.getRating()))) / 2;
                                        DatabaseReference customerTable = FirebaseDatabase.getInstance().getReference().child("Customer");
                                        customerTable.child(customerID).child("rate").setValue(String.valueOf(totalNewRate));
                                        // Save the rate in the rate table
                                        DatabaseReference rateTable = FirebaseDatabase.getInstance().getReference().child("Rate");
                                        String rateID = rateTable.push().getKey();
                                        Rate rate = new Rate(rateID, customerID, spID, String.valueOf(ratingBar.getRating()), ratingText.getText().toString().trim(), winchRequestID, "ServiceProvider");
                                        rateTable.child(rateID).setValue(rate);

                                        progressDialog.dismiss();
                                        Toast.makeText(RatingScreen.this, "تمت عملية التقييم بنجاح!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                    }
                }else{
                    Snackbar.make(findViewById(android.R.id.content),"من فضلك قم بكتابة تقييم...",Snackbar.LENGTH_LONG)
                            .setBackgroundTint(getResources().getColor(R.color.red_color))
                            .setTextColor(getResources().getColor(R.color.white))
                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                }
            }
        });


    }
    private void initToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_rating);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("رأيك في الخدمة");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextAppearance(RatingScreen.this, R.style.Theme_ResQme);
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