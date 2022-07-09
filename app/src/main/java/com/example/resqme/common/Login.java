package com.example.resqme.common;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.resqme.R;
import com.example.resqme.customer.CustomerHome;
import com.example.resqme.model.CMC;
import com.example.resqme.model.Car;
import com.example.resqme.model.Customer;
import com.example.resqme.model.ServiceProvider;
import com.example.resqme.model.SparePart;
import com.example.resqme.model.Winch;
import com.example.resqme.serviceProvider.ServiceProviderAddService;
import com.example.resqme.serviceProvider.ServiceProviderHome;
import com.example.resqme.serviceProvider.ServiceProviderSettings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.Locale;

public class Login extends AppCompatActivity implements View.OnClickListener{

    private TextInputEditText mEmailLogin,mPasswordLogin;
    private Button mLoginBtn;
    private TextView mDontHave;
    private FirebaseAuth mAuth;
    private StringBuilder userType;
    private DatabaseReference databaseCustomers;
    private DatabaseReference databaseServiceProviders;
    private DatabaseReference carDB;
    ProgressDialog progressDialog;




    // User Data Attrs
    String carID;
    String username = "";
    String password = "";
    String image = "";
    String address = "";
    String whatsApp = "";
    String bod = "";
    String userId = "";
    String rate = "";
    String gender = "";
    String isCMC ="", isWinch = "", isSpareParts = "";
    Locale locale;
    InternetConnection ic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if(Locale.getDefault().getLanguage().equals("ar")){
            locale = new Locale("en");
            Locale.setDefault(locale);
            Resources resources = Login.this.getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }else{
            locale = new Locale("en");
        }
        /// System Data START ************************************************************///
        mAuth = FirebaseAuth.getInstance();
        /// System Data END ************************************************************///
        ic = new InternetConnection(this);
        databaseCustomers = FirebaseDatabase.getInstance().getReference("Customer");
        databaseServiceProviders = FirebaseDatabase.getInstance().getReference("ServiceProviders");
        carDB = FirebaseDatabase.getInstance().getReference().child("Cars");
        initViews();
        forceRTLIfSupported();


    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void forceRTLIfSupported() {
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
    }

    void initViews() {
        userType = new StringBuilder();
        mEmailLogin=findViewById(R.id.email_login);
        mPasswordLogin=findViewById(R.id.password_login);
        mLoginBtn=findViewById(R.id.login_btn);
        mLoginBtn.setOnClickListener(this);
        mDontHave=findViewById(R.id.register_text_from_login);
        mDontHave.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_btn:
                if(!ic.checkInternetConnection()){
                    Snackbar.make(findViewById(android.R.id.content),"لا يوجد اتصال بالإنترنت",Snackbar.LENGTH_LONG)
                            .setBackgroundTint(getResources().getColor(R.color.red_color))
                            .setTextColor(getResources().getColor(R.color.white))
                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                }else{
                    loginProcess();
                }
                break;
            case R.id.register_text_from_login:
                Intent toSignIntent = new Intent(Login.this,Registeration.class);
                startActivity(toSignIntent);
                finish();
                break;
        }
    }

    void loginProcess(){
        String LoginEmail = mEmailLogin.getText().toString().trim();
        String LoginPass = mPasswordLogin.getText().toString().trim();
        if (!TextUtils.isEmpty(LoginEmail) && !TextUtils.isEmpty(LoginPass)) {
            progressDialog.setMessage("جاري تسجيل الدخول...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(LoginEmail,LoginPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Log.d("Login",FirebaseAuth.getInstance().getCurrentUser().getUid());
                        getUserData(LoginEmail);
                    }else{
                        progressDialog.dismiss();
                        String errorMessage = task.getException().getMessage();
                        Snackbar.make(findViewById(android.R.id.content),errorMessage,Snackbar.LENGTH_LONG)
                                .setBackgroundTint(getResources().getColor(R.color.red_color))
                                .setTextColor(getResources().getColor(R.color.white))
                                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                    }
                }
            });
        }else{
            progressDialog.dismiss();
            Snackbar.make(findViewById(android.R.id.content),"يجب إدخال جميع البيانات!",Snackbar.LENGTH_LONG)
                    .setBackgroundTint(getResources().getColor(R.color.red_color))
                    .setTextColor(getResources().getColor(R.color.white))
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
        }
    }

    void getUserData(String email) {
        databaseCustomers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Customer customer = snapshot.getValue(Customer.class);
                    if (customer != null) {
                        if (customer.getEmail().equals(email)) {
                            userType.append(customer.getUserType());
                            username = customer.getUsername();
                            carID = customer.getCarID();
                            password = customer.getPassword();
                            image = customer.getImage();
                            address = customer.getAddress();
                            whatsApp = customer.getWhatsApp();
                            bod = customer.getBod();
                            userId = customer.getUserId();
                            rate = customer.getRate();
                            gender = customer.getGender();
                            SharedPreferences cld = getSharedPreferences ("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = cld.edit();
                            editor.putString("C_USERNAME", username);
                            editor.putString("C_EMAIL", email);
                            editor.putString("C_PASSWORD", password);
                            editor.putString("C_ADDRESS", address);
                            editor.putString("C_WHATSAPP", whatsApp);
                            editor.putString("C_DOB", bod);
                            editor.putString("C_USERIMAGE", image);
                            editor.putString("C_USERTYPE", "عميل");
                            editor.putString("C_USERGENDER", gender);
                            editor.putString("C_CARID", String.valueOf(carID));
                            editor.putString("C_USERRATE", String.valueOf(rate));
                            editor.putString("C_USERID", userId);
                            editor.apply();

                            carDB.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot snapshotCar : snapshot.getChildren()) {
                                        Car car = snapshotCar.getValue(Car.class);
                                        if(car != null){
                                            if(car.getCarID().equals(String.valueOf(carID))){

                                                SharedPreferences cardLocalData = getSharedPreferences ("CAR_LOCAL_DATA", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor_car = cardLocalData.edit();
                                                editor_car.putString("CAR_ID", carID);
                                                editor_car.putString("CAR_USER_ID", userId);
                                                editor_car.putString("CAR_TYPE", car.getCarType());
                                                editor_car.putString("CAR_MODEL", car.getCarModel());
                                                editor_car.putString("CAR_MAINTENANCE", car.getCarMaintenance());
                                                editor_car.putString("CAR_TRANSMISSION", car.getCarTransmission());
                                                editor_car.putString("CAR_DRIVER_LICENCE", car.getCarDriverLicence());
                                                editor_car.putString("CAR_LICENCE", car.getCarLicence());
                                                editor_car.putString("CAR_STATUS", car.getCarStatus());
                                                editor_car.apply();

                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    Log.d("TAG", "LOGIN SUCCESS");
                                    Intent i = new Intent(Login.this, CustomerHome.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                    finish();
                                }
                            }, 1000);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseServiceProviders.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ServiceProvider serviceProvider = snapshot.getValue(ServiceProvider.class);

                    if (serviceProvider != null) {
                        if (serviceProvider.getEmail().equals(email)) {
                            userType.append(serviceProvider.getUserType());
                            username = serviceProvider.getUsername();
                            password = serviceProvider.getPassword();
                            image = serviceProvider.getImage();
                            address = serviceProvider.getAddress();
                            whatsApp = serviceProvider.getWhatsApp();
                            bod = serviceProvider.getBod();
                            userId = serviceProvider.getUserId();
                            rate = serviceProvider.getRate();
                            gender = serviceProvider.getGender();
                            String serviceType = serviceProvider.getServiceType();
                            isCMC = serviceProvider.getIsCMC();
                            isWinch = serviceProvider.getIsWinch();
                            isSpareParts = serviceProvider.getIsSpareParts();
                            SharedPreferences spld = getSharedPreferences ("SP_LOCAL_DATA", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = spld.edit();
                            editor.putString("SP_USERNAME", username);
                            editor.putString("SP_EMAIL", email);
                            editor.putString("SP_PASSWORD", password);
                            editor.putString("SP_ADDRESS", address);
                            editor.putString("SP_WHATSAPP", whatsApp);
                            editor.putString("SP_DOB", bod);
                            editor.putString("SP_USERIMAGE", image);
                            editor.putString("SP_USERTYPE", "مقدم خدمة");
                            editor.putString("SP_USERGENDER", gender);
                            editor.putString("SP_USERRATE", String.valueOf(rate));
                            editor.putString("SP_USERID", userId);
                            editor.putString("SP_ServiceType", serviceType);
                            editor.putString("SP_CMC", isCMC);
                            editor.putString("SP_WINCH", isWinch);
                            editor.putString("SP_SPARE_PARTS", isSpareParts);
                            editor.apply();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if(serviceType.isEmpty()){
                                        progressDialog.dismiss();
                                        Intent i = new Intent(Login.this, ServiceProviderAddService.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                        finish();
                                    }
                                    else{
                                        progressDialog.dismiss();
                                        Intent i = new Intent(Login.this, ServiceProviderHome.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                        finish();
                                    }

                                }
                            }, 1000);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}