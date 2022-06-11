package com.example.resqme.customer;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.resqme.R;
import com.example.resqme.common.AddressMap;
import com.example.resqme.common.InternetConnection;
import com.example.resqme.common.Splash;
import com.example.resqme.serviceProvider.ServiceProviderHome;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerUpdateProfile extends AppCompatActivity implements View.OnClickListener{
    CircleImageView iUserImage;
    TextInputEditText etUsername, etWhatsApp, etPassword;
    Button btnUpdateAccount, btnChooseImage, btnChooseAddress;
    TextView tvAddress;
    Uri mainImageUri = null;
    ProgressDialog progressDialog;
    DatabaseReference databaseCustomers;
    SharedPreferences c_data;
    FirebaseUser user;
    InternetConnection ic;
    Locale locale;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_update_profile);
        if(Locale.getDefault().getLanguage().equals("ar")){
            locale = new Locale("en");
            Locale.setDefault(locale);
            Resources resources = CustomerUpdateProfile.this.getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }else{
            locale = new Locale("en");
        }
        initViews();
        initToolbar();
        forceRTLIfSupported();
        user = FirebaseAuth.getInstance().getCurrentUser();
        c_data = getSharedPreferences("CUSTOMER_LOCAL_DATA", MODE_PRIVATE);
        etUsername.setText(c_data.getString("C_USERNAME","C_DEFAULT"));
        etPassword.setText(c_data.getString("C_PASSWORD","C_DEFAULT"));
        etWhatsApp.setText(c_data.getString("C_WHATSAPP","C_DEFAULT"));
        Glide.with(this).load(c_data.getString("C_USERIMAGE","C_DEFAULT")).into(iUserImage);
        ic = new InternetConnection(this);

        ActivityResultLauncher<String> launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                mainImageUri = result;
                iUserImage.setImageURI(result);
            }
        });
        btnChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launcher.launch("image/*");
            }
        });
    }
    void initViews(){
        iUserImage = findViewById(R.id.customer_update_profile_image);
        btnChooseImage = findViewById(R.id.choose_image_button_update_customer_profile);
        btnChooseImage.setOnClickListener(this);
        etUsername = findViewById(R.id.username_register);
        etWhatsApp = findViewById(R.id.whatsapp_register);
        etPassword = findViewById(R.id.password_register);
        btnUpdateAccount = findViewById(R.id.customer_profile_update_btn);
        btnUpdateAccount.setOnClickListener(this);
        btnChooseAddress = findViewById(R.id.choose_address_button_update_profile_customer);
        btnChooseAddress.setOnClickListener(this);
        tvAddress = findViewById(R.id.update_profile_choosed_address_text);
        progressDialog = new ProgressDialog(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.customer_profile_update_btn:
                if(!ic.checkInternetConnection()){
                    Snackbar.make(CustomerUpdateProfile.this.findViewById(android.R.id.content),"لا يوجد إتصال بالإنترنت." ,Snackbar.LENGTH_LONG)
                            .setBackgroundTint(getResources().getColor(R.color.red_color))
                            .setTextColor(getResources().getColor(R.color.white))
                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                }else{
                    updateProfileClick();
                }
                break;
            case R.id.choose_address_button_update_profile_customer:
                checkLocationPermission();
                break;
        }
    }


    void updateProfileClick(){
        databaseCustomers = FirebaseDatabase.getInstance().getReference("Customer");
        new AlertDialog.Builder(this, R.style.AlertDialogCustom)
                .setTitle("تأكيد تغيير البيانات")
                .setMessage("هل أنت متأكد من البيانات التي تم تغييرها؟ ، من فضلك راجع جميع البيانات...(أي بيانات لم يتم تغييرها أو إذا قمت بمسح اسم المستخدم أو كلمة المرور أو رقم الواتساب لن يتم اعتبارها)")
                .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.setMessage(" يرجى الانتظار قليلاً، جاري تغيير البيانات...");
                        progressDialog.show();
                        // Check if username changed, change it in firebase and local data
                        if(!TextUtils.isEmpty(etUsername.getText())){
                            if(!etUsername.getText().equals(c_data.getString("C_USERNAME","C_DEFAULT"))){
                                // Change username in firebase for the current user, in realtime db, in local data
                                UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(etUsername.getText().toString().trim()).build();
                                user.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            databaseCustomers.child(user.getUid()).child("username").setValue(etUsername.getText().toString());
                                            SharedPreferences cld = getSharedPreferences ("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = cld.edit();
                                            editor.putString("C_USERNAME", etUsername.getText().toString());
                                            editor.apply();
                                        }
                                    }
                                });
                            }
                        }

                        //Check if password changed, change it in firebase and local data
                        if(!TextUtils.isEmpty(etPassword.getText())){
                            if(!etPassword.getText().equals(c_data.getString("C_PASSWORD","C_DEFAULT"))){
                                user.updatePassword(etPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            databaseCustomers.child(user.getUid()).child("password").setValue(etPassword.getText().toString());
                                            SharedPreferences cld = getSharedPreferences ("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = cld.edit();
                                            editor.putString("C_PASSWORD", etPassword.getText().toString());
                                            editor.apply();
                                        }
                                    }
                                });


                            }
                        }

                        //Check if whatsApp changed, change it in firebase, local data
                        if(!TextUtils.isEmpty(etWhatsApp.getText())){
                            if(!etWhatsApp.getText().equals(c_data.getString("C_WHATSAPP","C_DEFAULT"))){
                                databaseCustomers.child(user.getUid()).child("whatsApp").setValue(etWhatsApp.getText().toString());
                                SharedPreferences cld = getSharedPreferences ("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = cld.edit();
                                editor.putString("C_WHATSAPP", etWhatsApp.getText().toString());
                                editor.apply();
                            }
                        }


                        //Check if address changed, change it in firebase, local data
                        if(!TextUtils.isEmpty(tvAddress.getText())){
                            databaseCustomers.child(user.getUid()).child("address").setValue(tvAddress.getText().toString());
                            SharedPreferences cld = getSharedPreferences ("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = cld.edit();
                            editor.putString("C_ADDRESS", tvAddress.getText().toString());
                            editor.apply();
                        }


                        //Check if image changed, upload it to storage, change it in firebase realtime db, change in local data
                        if(mainImageUri != null){
                            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                            final StorageReference image = storageReference.child("UserImages").child(mainImageUri.getLastPathSegment());
                            // Upload the image to firebase storage
                            image.putFile(mainImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> taskImage) {
                                            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                                    .setPhotoUri(taskImage.getResult()).build();
                                            user.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        databaseCustomers.child(user.getUid()).child("image").setValue(taskImage.getResult().toString());
                                                        SharedPreferences cld = getSharedPreferences ("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
                                                        SharedPreferences.Editor editor = cld.edit();
                                                        editor.putString("C_USERIMAGE", taskImage.getResult().toString());
                                                        editor.apply();
                                                        progressDialog.dismiss();
                                                        finish();
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                        if(mainImageUri == null){
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    finish();
                                }
                            }, 1000);

                        }
                    }
                })
                .setNegativeButton("لا", null)
                .show();
    }

    void getAddress() {
        Intent addressMapIntent = new Intent(com.example.resqme.customer.CustomerUpdateProfile.this, AddressMap.class);
        startActivityForResult(addressMapIntent, 11);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 11) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("ADDRESS_VALUE");
                tvAddress.setVisibility(View.VISIBLE);
                tvAddress.setText(result);
            }
        }
    }
    private void checkLocationPermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                if(!ic.checkInternetConnection()){
                    Snackbar.make(CustomerUpdateProfile.this.findViewById(android.R.id.content),"لا يوجد إتصال بالإنترنت." ,Snackbar.LENGTH_LONG)
                            .setBackgroundTint(getResources().getColor(R.color.red_color))
                            .setTextColor(getResources().getColor(R.color.white))
                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                }else{
                    getAddress();
                }
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
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_customer_profile_update);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("تحديث بيانات الصفحة الشخصية");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextAppearance(CustomerUpdateProfile.this, R.style.Theme_ResQme);
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
}