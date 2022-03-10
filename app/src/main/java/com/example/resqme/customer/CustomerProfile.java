package com.example.resqme.customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.resqme.R;
import com.example.resqme.common.Login;
import com.example.resqme.common.Registeration;
import com.example.resqme.model.Car;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerProfile extends AppCompatActivity implements View.OnClickListener {
    CircleImageView customerImage;
    TextView usernameTV, emailTV, addressTV, DOBTV, whatsAppTV, genderTV, userTypeTV, rateTV, carDataHeaderTV,
    carTypeTV, carModelTV, carMaintenanceTV, carTransTV;
    Button addCarBtn;
    LinearLayout carLayout;
    FirebaseAuth mAuth;
    TextView tvCarStatus;
    DatabaseReference customerTable;
    ImageView ivDriverLicence, ivCarLicence;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customerTable = FirebaseDatabase.getInstance().getReference().child("Cars");
        customerTable.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                carDataHeaderTV = findViewById(R.id.car_data_header);
                carLayout = findViewById(R.id.car_data_layout);
                carTypeTV = findViewById(R.id.car_type_text);
                carModelTV = findViewById(R.id.car_model_text);
                carMaintenanceTV = findViewById(R.id.car_maintenance_text);
                carTransTV = findViewById(R.id.car_trnasmission_text);
                ivDriverLicence = findViewById(R.id.driver_licence_image_after_approval);
                ivCarLicence = findViewById(R.id.car_licence_image__after_approval);

                SharedPreferences userData = getSharedPreferences("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
                SharedPreferences carLocalData = getSharedPreferences("CAR_LOCAL_DATA", Context.MODE_PRIVATE);
                String c_userid = userData.getString("C_USERID", "C_DEFAULT");
                String car_id_local = carLocalData.getString("CAR_ID", "CAR_DEFAULT");
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Car carObj = dataSnapshot.getValue(Car.class);
                    if (carObj.getCarID().equals(car_id_local) && carObj.getUserID().equals(c_userid)) {
                        if (carObj.getCarStatus().equals("Pending")) {
                            SharedPreferences changeCarStatus = getSharedPreferences("CAR_LOCAL_DATA", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor_carStatus = changeCarStatus.edit();
                            editor_carStatus.putString("CAR_STATUS", "Pending");
                            editor_carStatus.apply();
                            tvCarStatus.setVisibility(View.VISIBLE);
                            tvCarStatus.setText("يتم مراجعة بيانات العربية...");
                            tvCarStatus.setTextColor(Color.rgb(255, 166, 53));
                        } else if (carObj.getCarStatus().equals("Refused")) {
                            SharedPreferences changeCarStatus = getSharedPreferences("CAR_LOCAL_DATA", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor_carStatus = changeCarStatus.edit();
                            editor_carStatus.putString("CAR_STATUS", "Refused");
                            editor_carStatus.apply();
                            tvCarStatus.setVisibility(View.VISIBLE);
                            tvCarStatus.setText("تم رفض العربية، تواصل معانا لمعرفة معلومات اكتر.");
                            tvCarStatus.setTextColor(Color.RED);
                            carDataHeaderTV.setVisibility(View.GONE);
                            carLayout.setVisibility(View.GONE);
                        } else if (carObj.getCarStatus().equals("Approved")) {
                            SharedPreferences changeCarStatus = getSharedPreferences("CAR_LOCAL_DATA", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor_carStatus = changeCarStatus.edit();
                            editor_carStatus.putString("CAR_STATUS", "Approved");
                            editor_carStatus.apply();
                            tvCarStatus.setVisibility(View.VISIBLE);
                            tvCarStatus.setText("تم قبول العربية");
                            tvCarStatus.setTextColor(Color.GREEN);
                            // Showing Car Data
                            carDataHeaderTV.setVisibility(View.VISIBLE);
                            carLayout.setVisibility(View.VISIBLE);
                            SharedPreferences carDataInLocal = getSharedPreferences("CAR_LOCAL_DATA", Context.MODE_PRIVATE);
                            carTypeTV.setText(carDataInLocal.getString("CAR_TYPE","CAR_DEFAULT"));
                            carModelTV.setText(carDataInLocal.getString("CAR_MODEL","CAR_DEFAULT"));
                            carMaintenanceTV.setText(carDataInLocal.getString("CAR_MAINTENANCE","CAR_DEFAULT"));
                            carTransTV.setText(carDataInLocal.getString("CAR_TRANSMISSION","CAR_DEFAULT"));
                            ivDriverLicence.setImageURI(Uri.parse(carDataInLocal.getString("CAR_DRIVER_LICENCE","CAR_DEFAULT")));
                            ivCarLicence.setImageURI(Uri.parse(carDataInLocal.getString("CAR_LICENCE","CAR_DEFAULT")));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        setContentView(R.layout.activity_customer_profile);
        mAuth = FirebaseAuth.getInstance();
        initViews();
        initToolbar();
        forceRTLIfSupported();
        SharedPreferences userData = getSharedPreferences("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
        String c_email = userData.getString("C_EMAIL", "C_DEFAULT");
        String c_username = userData.getString("C_USERNAME", "C_DEFAULT");
        String c_password = userData.getString("C_PASSWORD", "C_DEFAULT");
        String c_address = userData.getString("C_ADDRESS", "C_DEFAULT");
        String c_whatsapp = userData.getString("C_WHATSAPP", "C_DEFAULT");
        String c_dob = userData.getString("C_DOB", "C_DEFAULT");
        String c_userimage = userData.getString("C_USERIMAGE", "C_DEFAULT");
        String c_usertype = userData.getString("C_USERTYPE", "C_DEFAULT");
        String c_usergender = userData.getString("C_USERGENDER", "C_DEFAULT");
        String c_carid = userData.getString("C_CARID", "C_DEFAULT");
        String c_userrate = userData.getString("C_USERRATE", "C_DEFAULT");
        String c_userid = userData.getString("C_USERID", "C_DEFAULT");
        customerImage.setImageURI(Uri.parse(c_userimage));
        usernameTV.setText(c_username);
        emailTV.setText(c_email);
        addressTV.setText(c_address);
        DOBTV.setText(c_dob);
        whatsAppTV.setText(c_whatsapp);
        genderTV.setText(c_usergender);
        userTypeTV.setText(c_usertype);
        rateTV.setText(c_userrate);
        if (!c_carid.equals("0")) {
            addCarBtn.setVisibility(View.GONE);
            SharedPreferences cardLocalData = getSharedPreferences("CAR_LOCAL_DATA", Context.MODE_PRIVATE);
            String car_status = cardLocalData.getString("CAR_STATUS", "CAR_DEFAULT");
            if (car_status.equals("Pending")) {
                tvCarStatus.setVisibility(View.VISIBLE);
                tvCarStatus.setText("يتم مراجعة بيانات العربية...");
                tvCarStatus.setTextColor(Color.rgb(255, 166, 53));
                SharedPreferences changeCarStatus = getSharedPreferences("CAR_LOCAL_DATA", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor_carStatus = changeCarStatus.edit();
                editor_carStatus.putString("CAR_STATUS", "Pending");
                editor_carStatus.apply();
            } else if (car_status.equals("Refused")) {
                tvCarStatus.setVisibility(View.VISIBLE);
                tvCarStatus.setText("تم رفض العربية، تواصل معانا لمعرفة معلومات اكتر.");
                tvCarStatus.setTextColor(Color.RED);
                SharedPreferences changeCarStatus = getSharedPreferences("CAR_LOCAL_DATA", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor_carStatus = changeCarStatus.edit();
                editor_carStatus.putString("CAR_STATUS", "Refused");
                editor_carStatus.apply();
                carDataHeaderTV.setVisibility(View.GONE);
                carLayout.setVisibility(View.GONE);
            } else if (car_status.equals("Approved")) {
                tvCarStatus.setVisibility(View.VISIBLE);
                tvCarStatus.setText("تم قبول العربية");
                tvCarStatus.setTextColor(Color.GREEN);
                SharedPreferences changeCarStatus = getSharedPreferences("CAR_LOCAL_DATA", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor_carStatus = changeCarStatus.edit();
                editor_carStatus.putString("CAR_STATUS", "Approved");
                editor_carStatus.apply();
                // Showing Car Data
                carDataHeaderTV.setVisibility(View.VISIBLE);
                carLayout.setVisibility(View.VISIBLE);
                SharedPreferences carDataInLocal = getSharedPreferences("CAR_LOCAL_DATA", Context.MODE_PRIVATE);
                carTypeTV.setText(carDataInLocal.getString("CAR_TYPE","CAR_DEFAULT"));
                carModelTV.setText(carDataInLocal.getString("CAR_MODEL","CAR_DEFAULT"));
                carMaintenanceTV.setText(carDataInLocal.getString("CAR_MAINTENANCE","CAR_DEFAULT"));
                carTransTV.setText(carDataInLocal.getString("CAR_TRANSMISSION","CAR_DEFAULT"));
                ivDriverLicence.setImageURI(Uri.parse(carDataInLocal.getString("CAR_DRIVER_LICENCE","CAR_DEFAULT")));
                ivCarLicence.setImageURI(Uri.parse(carDataInLocal.getString("CAR_LICENCE","CAR_DEFAULT")));
            }
        }
    }

    private void initViews() {

        carDataHeaderTV = findViewById(R.id.car_data_header);
        carLayout = findViewById(R.id.car_data_layout);
        carTypeTV = findViewById(R.id.car_type_text);
        carModelTV = findViewById(R.id.car_model_text);
        carMaintenanceTV = findViewById(R.id.car_maintenance_text);
        carTransTV = findViewById(R.id.car_trnasmission_text);
        ivDriverLicence = findViewById(R.id.driver_licence_image_after_approval);
        ivCarLicence = findViewById(R.id.car_licence_image__after_approval);

        customerImage = findViewById(R.id.customer_profile_profile_image);
        usernameTV = findViewById(R.id.username_profile_text);
        emailTV = findViewById(R.id.email_profile_text);
        addressTV = findViewById(R.id.address_profile_text);
        DOBTV = findViewById(R.id.dob_profile_text);
        whatsAppTV = findViewById(R.id.whatsapp_profile_text);
        genderTV = findViewById(R.id.gender_profile_text);
        userTypeTV = findViewById(R.id.userType_profile_text);
        rateTV = findViewById(R.id.userRate_profile_text);
        addCarBtn = findViewById(R.id.add_car_data_button);
        addCarBtn.setOnClickListener(this);
        tvCarStatus = findViewById(R.id.car_status_tv);
    }

    void sendToLogin() {
        Intent loginIntent = new Intent(CustomerProfile.this, Login.class);
        startActivity(loginIntent);
        finish();
    }


    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_customer_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("الصفحة الشخصية");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextAppearance(CustomerProfile.this, R.style.Theme_ResQme);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void forceRTLIfSupported() {
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_car_data_button:
                Intent mainIntent = new Intent(CustomerProfile.this, AddCarData.class);
                startActivity(mainIntent);
                break;


        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences userData = getSharedPreferences("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
        String c_carid = userData.getString("C_CARID", "C_DEFAULT");
        if (!c_carid.equals("0")) {
            addCarBtn.setVisibility(View.GONE);
            SharedPreferences cardLocalData = getSharedPreferences("CAR_LOCAL_DATA", Context.MODE_PRIVATE);
            String car_status = cardLocalData.getString("CAR_STATUS", "CAR_DEFAULT");
            if (car_status.equals("Pending")) {
                tvCarStatus.setVisibility(View.VISIBLE);
                tvCarStatus.setText("يتم مراجعة بيانات العربية...");
                tvCarStatus.setTextColor(Color.rgb(255, 166, 53));
                SharedPreferences changeCarStatus = getSharedPreferences("CAR_LOCAL_DATA", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor_carStatus = changeCarStatus.edit();
                editor_carStatus.putString("CAR_STATUS", "Pending");
                editor_carStatus.apply();
            } else if (car_status.equals("Refused")) {
                tvCarStatus.setVisibility(View.VISIBLE);
                tvCarStatus.setText("تم رفض العربية، تواصل معانا لمعرفة معلومات اكتر.");
                tvCarStatus.setTextColor(Color.RED);
                SharedPreferences changeCarStatus = getSharedPreferences("CAR_LOCAL_DATA", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor_carStatus = changeCarStatus.edit();
                editor_carStatus.putString("CAR_STATUS", "Refused");
                editor_carStatus.apply();
                carDataHeaderTV.setVisibility(View.GONE);
                carLayout.setVisibility(View.GONE);
            } else if (car_status.equals("Approved")) {
                tvCarStatus.setVisibility(View.VISIBLE);
                tvCarStatus.setText("تم قبول العربية");
                tvCarStatus.setTextColor(Color.GREEN);
                SharedPreferences changeCarStatus = getSharedPreferences("CAR_LOCAL_DATA", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor_carStatus = changeCarStatus.edit();
                editor_carStatus.putString("CAR_STATUS", "Approved");
                editor_carStatus.apply();
                // Showing Car Data
                carDataHeaderTV.setVisibility(View.VISIBLE);
                carLayout.setVisibility(View.VISIBLE);
                SharedPreferences carDataInLocal = getSharedPreferences("CAR_LOCAL_DATA", Context.MODE_PRIVATE);
                carTypeTV.setText(carDataInLocal.getString("CAR_TYPE","CAR_DEFAULT"));
                carModelTV.setText(carDataInLocal.getString("CAR_MODEL","CAR_DEFAULT"));
                carMaintenanceTV.setText(carDataInLocal.getString("CAR_MAINTENANCE","CAR_DEFAULT"));
                carTransTV.setText(carDataInLocal.getString("CAR_TRANSMISSION","CAR_DEFAULT"));
                ivDriverLicence.setImageURI(Uri.parse(carDataInLocal.getString("CAR_DRIVER_LICENCE","CAR_DEFAULT")));
                ivCarLicence.setImageURI(Uri.parse(carDataInLocal.getString("CAR_LICENCE","CAR_DEFAULT")));
            }
        }
    }
}