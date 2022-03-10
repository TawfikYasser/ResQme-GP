package com.example.resqme.customer;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.resqme.R;
import com.example.resqme.common.AddressMap;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerUpdateProfile extends AppCompatActivity implements View.OnClickListener{

    // Views Initiation
    CircleImageView iUserImage;
    TextInputEditText etUsername, etWhatsApp, etPassword, etAddress;
    Button  btnUpdateAccount, btnChooseImage, btnChooseAddress;
    TextView tvLogin, tvAddress;

    Uri mainImageUri = null;

    //Firebase Initiation
    DatabaseReference databaseTableCustomers, databaseTableSP; // Reference on database
    StorageReference mStorageReference;


    //More views
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);
        initViews();
        firebaseData();

    }

    void initViews(){
        iUserImage = findViewById(R.id.choose_image_register);

        btnChooseImage = findViewById(R.id.choose_image_button);
        btnChooseImage.setOnClickListener(this);

        etUsername = findViewById(R.id.username_register);
        etWhatsApp = findViewById(R.id.whatsapp_register);
        etPassword = findViewById(R.id.password_register);

        btnUpdateAccount = findViewById(R.id.update_btn);
        btnUpdateAccount.setOnClickListener(this);
        btnChooseAddress = findViewById(R.id.choose_address_button);
        btnChooseAddress.setOnClickListener(this);

        tvLogin = findViewById(R.id.login_text_from_register);
        tvLogin.setOnClickListener(this);
        tvAddress = findViewById(R.id.choosed_address_text);

        progressDialog = new ProgressDialog(this);
    }

    void firebaseData(){
        databaseTableCustomers = FirebaseDatabase.getInstance().getReference().child("Customer");

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.update_btn: 
                updateProfile();
                break;
        }
    }

    private void updateProfile() {

    }

    void getAddress() {
        Intent addressMapIntent = new Intent(com.example.resqme.customer.CustomerUpdateProfile.this, AddressMap.class);
        startActivityForResult(addressMapIntent, 11);
    }

    private void checkLocationPermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                getAddress();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(),"");
                intent.setData(uri);
                startActivity(intent);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }


    void gettingImageFromGallery(){
        ImagePicker.with(this)
                .crop()	 //Crop image(Optional), Check Customization for more option
                .compress(1024)	//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 11){
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("ADDRESS_VALUE");
                tvAddress.setVisibility(View.VISIBLE);
                tvAddress.setText(result);
            }
        }else{
            Uri uri = data.getData();
            mainImageUri = uri;
            iUserImage.setImageURI(uri);
        }

    }/**
    void saveLocalDataCustomer(String username, String email, String password, String address, String whatsApp,
                               String DOB, String userImage, String userType, String userGender, String carID, String userRate, String userID){

        SharedPreferences cld = getSharedPreferences ("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = cld.edit();
        editor.putString("C_USERNAME", username);
        editor.putString("C_EMAIL", email);
        editor.putString("C_PASSWORD", password);
        editor.putString("C_ADDRESS", address);
        editor.putString("C_WHATSAPP", whatsApp);
        editor.putString("C_DOB", DOB);
        editor.putString("C_USERIMAGE", userImage);
        editor.putString("C_USERTYPE", userType);
        editor.putString("C_USERGENDER", userGender);
        editor.putString("C_CARID", carID);
        editor.putString("C_USERRATE", userRate);
        editor.putString("C_USERID", userID);
        editor.apply();
    }
    void saveLocalDataSP(String username, String email, String password, String address, String whatsApp,
                         String DOB, String userImage, String userType, String userGender, String userRate, String userID){

        SharedPreferences spld = getSharedPreferences ("SP_LOCAL_DATA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = spld.edit();
        editor.putString("SP_USERNAME", username);
        editor.putString("SP_EMAIL", email);
        editor.putString("SP_PASSWORD", password);
        editor.putString("SP_ADDRESS", address);
        editor.putString("SP_WHATSAPP", whatsApp);
        editor.putString("SP_DOB", DOB);
        editor.putString("SP_USERIMAGE", userImage);
        editor.putString("SP_USERTYPE", userType);
        editor.putString("SP_USERGENDER", userGender);
        editor.putString("SP_USERRATE", userRate);
        editor.putString("SP_USERID", userID);
        editor.apply();

    }
*/
}