package com.example.resqme.common;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.resqme.R;
import com.example.resqme.customer.CustomerHome;
import com.example.resqme.model.Customer;
import com.example.resqme.model.ServiceProvider;
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

    ProgressDialog progressDialog;




    // User Data Attrs
    int carID;
    String username = "";
    String password = "";
    String image = "";
    String address = "";
    String whatsApp = "";
    String bod = "";
    String userId = "";
    float rate = 0;
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
                String LoginEmail = mEmailLogin.getText().toString();
                String LoginPass = mPasswordLogin.getText().toString();
                if (!TextUtils.isEmpty(LoginEmail) && !TextUtils.isEmpty(LoginPass)) {
                    mAuth.signInWithEmailAndPassword(LoginEmail,LoginPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                getUserData(LoginEmail);
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

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent i = new Intent(Login.this, CustomerHome.class);
                                    i.putExtra("USERNAME", username);
                                    i.putExtra("CARDID", carID);
                                    i.putExtra("PASSWORD", password);
                                    i.putExtra("IMAGE", image);
                                    i.putExtra("ADDRESS", address);
                                    i.putExtra("WHATSAPP", whatsApp);
                                    i.putExtra("EMAIL", email);
                                    i.putExtra("BOD", bod);
                                    i.putExtra("TYPE", userType.toString());
                                    i.putExtra("USERID", userId);
                                    i.putExtra("RATE", rate);
                                    i.putExtra("GENDER", gender);
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

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent i = new Intent(Login.this, ServiceProviderHome.class);
                                    i.putExtra("USERNAME", username);
                                    i.putExtra("PASSWORD", password);
                                    i.putExtra("IMAGE", image);
                                    i.putExtra("ADDRESS", address);
                                    i.putExtra("WHATSAPP", whatsApp);
                                    i.putExtra("EMAIL", email);
                                    i.putExtra("BOD", bod);
                                    i.putExtra("TYPE", userType.toString());
                                    i.putExtra("USERID", userId);
                                    i.putExtra("RATE", rate);
                                    i.putExtra("GENDER", gender);
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

    }

}