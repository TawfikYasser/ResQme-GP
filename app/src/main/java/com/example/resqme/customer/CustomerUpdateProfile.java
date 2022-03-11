package com.example.resqme.customer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.resqme.R;
import com.example.resqme.common.AddressMap;
import com.example.resqme.common.Registeration;
import com.example.resqme.model.Customer;
import com.example.resqme.model.ServiceProvider;
import com.example.resqme.serviceProvider.ServiceProviderHome;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
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
    Button btnUpdateAccount, btnChooseImage, btnChooseAddress;
    TextView tvLogin, tvAddress;

    Uri mainImageUri = null;

    //Firebase Initiation
    DatabaseReference databaseTableCustomers, databaseTableSP; // Reference on database
    StorageReference mStorageReference;
    //More views
    ProgressDialog progressDialog;
    private DatabaseReference databaseCustomers;
    SharedPreferences c_data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_update_profile);
        initViews();
        firebaseData();
        c_data = getSharedPreferences("CUSTOMER_LOCAL_DATA", MODE_PRIVATE);

        etUsername.setText(c_data.getString("C_USERNAME","C_DEFAULT"));
        etPassword.setText(c_data.getString("C_PASSWORD","C_DEFAULT"));
        etWhatsApp.setText(c_data.getString("C_WHATSAPP","C_DEFAULT"));
        String UserID= c_data.getString("C_USERID","C_DEFAULT");
        Toast.makeText(this,UserID,Toast.LENGTH_LONG).show();
        // etAddress.setText(c_data.getString("C_ADDRESS","C_DEFAULT"));

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
                updateProfileClick();
                break;
            case R.id.choose_image_button:
                gettingImageFromGallery();
                break;
            case R.id.choose_address_button:
                checkLocationPermission();
                break;
        }
    }


//    void updateUserInfo(String username, final FirebaseUser user){
//
//
//        final String userEmail= user.getEmail();
//        final String generatedID = user.getUid();
//
//        final StorageReference filepath = mStorageReference.child("UserImages").child(mainImageUri.getLastPathSegment());
//
//
//        filepath.putFile(mainImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//                        UserProfileChangeRequest userProfileChangeRequest =new UserProfileChangeRequest.Builder()
//                                .setDisplayName(username).setPhotoUri(uri).build();
//
//                        if(selectedUserType.equals("عميل")){
//
//                            DatabaseReference databaseReference_customer =databaseTableCustomers.child(generatedID);
//
//                            Customer customer = new Customer(0, username, userEmail, etPassword.getText().toString(), uri.toString(), tvAddress.getText().toString().trim(), etWhatsApp.getText().toString(), bod, generatedID, 5, rbtnMale.getText().toString(), selectedUserType);
//
//                            databaseReference_customer.setValue(customer);
//
//                        }else{
//                            DatabaseReference databaseReference_sp =databaseTableSP.child(generatedID);
//
//                            ServiceProvider serviceProvider = new ServiceProvider(username, userEmail, etPassword.getText().toString(), uri.toString(), tvAddress.getText().toString().trim(), etWhatsApp.getText().toString(), bod, generatedID, 5, rbtnMale.getText().toString(), selectedUserType);
//
//                            databaseReference_sp.setValue(serviceProvider);
//                        }
//
//
//                        user.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if(task.isSuccessful()){
//                                    //user info updated
//                                    int selectedType = rgUserType.getCheckedRadioButtonId();
//                                    rbtnCustomer=(RadioButton)findViewById(selectedType);
//                                    if(rbtnCustomer.getText().toString().equals("عميل")){
//                                        saveLocalDataCustomer(username, userEmail, etPassword.getText().toString().trim(),
//                                                tvAddress.getText().toString().trim(), etWhatsApp.getText().toString().trim(), bod, mainImageUri.toString(),
//                                                "عميل", rbtnMale.getText().toString(), "0", "5", generatedID);
//                                        Intent mainIntent = new Intent(Registeration.this, CustomerHome.class);
//                                        startActivity(mainIntent);
//                                        finish();
//                                    }else{
//                                        saveLocalDataSP(username, userEmail, etPassword.getText().toString().trim(),
//                                                tvAddress.getText().toString().trim(), etWhatsApp.getText().toString().trim(), bod, mainImageUri.toString(),
//                                                "مقدم خدمة", rbtnMale.getText().toString(), "5", generatedID);
//                                        Intent mainIntent = new Intent(Registeration.this, ServiceProviderHome.class);
//                                        startActivity(mainIntent);
//                                        finish();
//                                    }
//                                    progressDialog.dismiss();
//                                }else{
//                                    //something wrong
//                                    progressDialog.dismiss();
//                                    Snackbar.make(findViewById(android.R.id.content),"يوجد خطأ ما، برجاء المحاولة مرة أخرى!",Snackbar.LENGTH_LONG)
//                                            .setBackgroundTint(getResources().getColor(R.color.red_color))
//                                            .setTextColor(getResources().getColor(R.color.white))
//                                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
//                                }
//                            }
//                        });
//                    }
//                });
//            }
//
//        });
//
//    }

    void updateProfileClick(){
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String whatsApp = etWhatsApp.getText().toString();
        c_data = getSharedPreferences("CUSTOMER_LOCAL_DATA", MODE_PRIVATE);

        String UserID= c_data.getString("C_USERID","C_DEFAULT");

        databaseCustomers = FirebaseDatabase.getInstance().getReference("Customer");
        databaseCustomers.child(UserID).child("username").setValue(username);


        if(!TextUtils.isEmpty(username) || !TextUtils.isEmpty(password)
                || !TextUtils.isEmpty(whatsApp) || mainImageUri != null || !TextUtils.isEmpty(tvAddress.getText())){
            new AlertDialog.Builder(this)

                    .setTitle("تأكيد تغيير البيانات")
                    .setMessage("هل أنت متأكد من البيانات التي تم إدخالها؟ ، من فضلك راجع جميع البيانات...")
                    .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            progressDialog.setMessage(" يرجى الانتظار قليلاً، جاري تغيير البيانات... ");
                            progressDialog.show();
                            //updateAccount(password.trim(),username.trim());
                        }
                    })
                    .setNegativeButton("لا", null)
                    .show();
        }else{
            progressDialog.dismiss();
            Snackbar.make(findViewById(android.R.id.content),"يجب إدخال جميع البيانات!",Snackbar.LENGTH_LONG)
                    .setBackgroundTint(getResources().getColor(R.color.red_color))
                    .setTextColor(getResources().getColor(R.color.white))
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    void getAddress() {
        Intent addressMapIntent = new Intent(com.example.resqme.customer.CustomerUpdateProfile.this, AddressMap.class);
        startActivityForResult(addressMapIntent, 11);
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

    }

    void saveLocalDataCustomer(String username, String email, String password, String address, String whatsApp,
                               String DOB, String userImage, String userType, String userGender, String carID, String userRate, String userID){

        SharedPreferences cld = getSharedPreferences ("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = cld.edit();
        editor.putString("C_USERNAME", username);
        editor.putString("C_PASSWORD", password);
        editor.putString("C_ADDRESS", address);
        editor.putString("C_WHATSAPP", whatsApp);
        editor.putString("C_USERIMAGE", userImage);
        editor.apply();
    }
    void gettingImageFromGallery(){
        ImagePicker.with(this)
                .crop()	 //Crop image(Optional), Check Customization for more option
                .compress(1024)	//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start();
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
}