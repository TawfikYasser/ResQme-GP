package com.example.resqme.common;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.resqme.R;
import com.example.resqme.customer.CustomerHome;
import com.example.resqme.model.Car;
import com.example.resqme.model.Customer;
import com.example.resqme.model.ServiceProvider;
import com.example.resqme.serviceProvider.ServiceProviderAddService;
import com.example.resqme.serviceProvider.ServiceProviderHome;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /// System Data START ************************************************************///
        mAuth = FirebaseAuth.getInstance();
        /// System Data END ************************************************************///
        databaseCustomers = FirebaseDatabase.getInstance().getReference("Customer");
        databaseServiceProviders = FirebaseDatabase.getInstance().getReference("ServiceProviders");
        carDB = FirebaseDatabase.getInstance().getReference().child("Cars");
        initViews();

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
                progressDialog.setMessage("من فضلك انتظر قليلاً...");
                progressDialog.show();
                String LoginEmail = mEmailLogin.getText().toString().trim();
                String LoginPass = mPasswordLogin.getText().toString().trim();
                if (!TextUtils.isEmpty(LoginEmail) && !TextUtils.isEmpty(LoginPass)) {
                    mAuth.signInWithEmailAndPassword(LoginEmail,LoginPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                SharedPreferences c_data = getSharedPreferences("CUSTOMER_LOCAL_DATA", MODE_PRIVATE);
                                SharedPreferences sp_data = getSharedPreferences("SP_LOCAL_DATA", MODE_PRIVATE);
                                if(c_data.contains("C_EMAIL") || sp_data.contains("SP_EMAIL")){
                                    if(!checkUserData(LoginEmail)){
                                        getUserData(LoginEmail);
                                    }
                                }else{
                                    getUserData(LoginEmail);
                                }
                            }else{
                                progressDialog.dismiss();
                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(Login.this, "خطأ: "+errorMessage, Toast.LENGTH_SHORT).show();
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
                break;
            case R.id.register_text_from_login:
                Intent toSignIntent = new Intent(Login.this,Registeration.class);
                startActivity(toSignIntent);
                finish();
                break;
        }
    }

    boolean checkUserData(String loginEmail) {
        boolean found = true;
        SharedPreferences userData = getSharedPreferences("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
        SharedPreferences userDataSP = getSharedPreferences("SP_LOCAL_DATA", Context.MODE_PRIVATE);
        if(userData.contains("C_EMAIL")){
            String c_email = userData.getString("C_EMAIL","C_DEFAULT");
            if(loginEmail.equals(c_email)){
                String c_carid = userData.getString("C_CARID","C_DEFAULT");
                Intent i = new Intent(Login.this, CustomerHome.class);
                progressDialog.dismiss();
                startActivity(i);
                finish();
            }else{
                found =  false;
            }
        }
        if(userDataSP.contains("SP_EMAIL")){
            String sp_email = userDataSP.getString("SP_EMAIL","SP_DEFAULT");
            String serviceType = userDataSP.getString("SP_ServiceType","SP_DEFAULT");
            if(loginEmail.equals(sp_email)){
                if(serviceType.isEmpty()){
                    Intent i = new Intent(Login.this, ServiceProviderAddService.class);
                    progressDialog.dismiss();
                    startActivity(i);
                    finish();
                }
                else{
                    Intent i = new Intent(Login.this, ServiceProviderAddService.class);
                    progressDialog.dismiss();
                    startActivity(i);
                    finish();
                }
            }else{
                found =  false;
            }
        }

//        String c_username = userData.getString("C_USERNAME","C_DEFAULT");
//        String c_password = userData.getString("C_PASSWORD","C_DEFAULT");
//        String c_address = userData.getString("C_ADDRESS","C_DEFAULT");
//        String c_whatsapp = userData.getString("C_WHATSAPP","C_DEFAULT");
//        String c_dob = userData.getString("C_DOB","C_DEFAULT");
//        String c_userimage = userData.getString("C_USERIMAGE","C_DEFAULT");
//        String c_usertype = userData.getString("C_USERTYPE","C_DEFAULT");
//        String c_usergender = userData.getString("C_USERGENDER","C_DEFAULT");
//        String c_carid = userData.getString("C_CARID","C_DEFAULT");
//        String c_userrate = userData.getString("C_USERRATE","C_DEFAULT");
//        String c_userid = userData.getString("C_USERID","C_DEFAULT");


//CHANGE C_DEFAULT to SP_DEFAULT
//        String sp_username = userData.getString("SP_USERNAME","C_DEFAULT");
//        String sp_password = userData.getString("SP_PASSWORD","C_DEFAULT");
//        String sp_address = userData.getString("SP_ADDRESS","C_DEFAULT");
//        String sp_whatsapp = userData.getString("SP_WHATSAPP","C_DEFAULT");
//        String sp_dob = userData.getString("SP_DOB","C_DEFAULT");
//        String sp_userimage = userData.getString("SP_USERIMAGE","C_DEFAULT");
//        String sp_usertype = userData.getString("SP_USERTYPE","C_DEFAULT");
//        String sp_usergender = userData.getString("SP_USERGENDER","C_DEFAULT");
//        String sp_userrate = userData.getString("SP_USERRATE","C_DEFAULT");
//        String sp_userid = userData.getString("SP_USERID","C_DEFAULT");

        return found;
    }


    //UDPDATE
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
                                    Intent i = new Intent(Login.this, CustomerHome.class);
                                    progressDialog.dismiss();
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
                            editor.apply();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if(serviceType.isEmpty()){
                                        Intent i = new Intent(Login.this, ServiceProviderAddService.class);
                                        startActivity(i);
                                        finish();
                                    }
                                    else{
                                        Intent i = new Intent(Login.this, ServiceProviderHome.class);
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